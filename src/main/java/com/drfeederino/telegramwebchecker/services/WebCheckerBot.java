package com.drfeederino.telegramwebchecker.services;

import com.drfeederino.telegramwebchecker.entities.TelegramUser;
import com.drfeederino.telegramwebchecker.enums.UserStatus;
import com.drfeederino.telegramwebchecker.parsers.IDataTrackingParser;
import com.drfeederino.telegramwebchecker.repository.TelegramUserRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.regex.Matcher;

import static com.drfeederino.telegramwebchecker.constants.BotMessages.*;
import static com.drfeederino.telegramwebchecker.constants.Patterns.BARCODE_NUMBER;
import static com.drfeederino.telegramwebchecker.constants.Patterns.NUMBER;
import static com.drfeederino.telegramwebchecker.services.AESEncryption.decryptData;
import static com.drfeederino.telegramwebchecker.services.AESEncryption.encryptData;


@Service
@Slf4j
public class WebCheckerBot extends TelegramLongPollingBot {

    private final TelegramUserRepository userRepository;
    private final IDataTrackingParser trackingParser;

    @Value("${BOT_NAME}")
    private String botName;
    @Value("${BOT_TOKEN}")
    private String botToken;

    @Autowired
    public WebCheckerBot(
            TelegramUserRepository repository,
            IDataTrackingParser trackingParser
    ) {
        this.userRepository = repository;
        this.trackingParser = trackingParser;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @PostConstruct
    public void register() {
        try {
            log.info("Registering bot.");
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
            log.info("Successfully registered bot.");
        } catch (TelegramApiException e) {
            log.info("Error occurred {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            handleUserUpdate(update);
        } else {
            sendUpdate(update.getMessage().getChatId(), WELCOME_MESSAGE);
        }
    }

    private void handleUserUpdate(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        TelegramUser telegramUser = getTelegramUser(userId);
        if (telegramUser == null) {
            handleNewUser(userId);
        } else {
            handleUserStatus(telegramUser, update);
        }
    }

    @SneakyThrows
    private void handleNewUser(Long userId) {
        log.info("Registering new user.");
        TelegramUser telegramUser = new TelegramUser(userId, UserStatus.REGISTERED, null, null, null);
        userRepository.save(telegramUser);
        sendUpdate(userId, WELCOME_MESSAGE);
    }

    @SneakyThrows
    private void handleUserStatus(TelegramUser user, Update update) {
        String text = update.getMessage().getText();
        if (text == null || text.isEmpty()) {
            log.warn("Empty message.");
            return; // nothing to do
        }
        if (text.contains("/stop")) { // when user stops bot
            log.info("Deleting user.");
            userRepository.deleteById(user.getId());
            sendUpdate(user.getId(), USER_DELETED);
            return;
        }

        if (text.contains("/status")) { // when user stops bot
            log.info("Checking user's status.");
            sendUpdate(user.getId(), USER_CHECKING_STATUS_NOW);
            TelegramUser telegramUser = trackingParser.updateUserStatus(user);
            sendNewStatus(telegramUser.getId(), telegramUser.getLastStatus());
            return;
        }

        // usual case - everything is ok, proceed to add info.
        String number = null;
        String barcode = null;
        Matcher numberMatcher = NUMBER.matcher(text);
        Matcher barcodeMatcher = BARCODE_NUMBER.matcher(text);
        if (numberMatcher.find()) {
            number = numberMatcher.group();
        }
        if (barcodeMatcher.find()) {
            barcode = barcodeMatcher.group();
        }

        if (barcode != null && number != null) {
            log.info("Completed user registration. Retrieving latest status.");
            user.setStatus(UserStatus.COMPLETE);
            user.setBarcode(encryptData(barcode));
            user.setNumber(encryptData(number));
            sendUpdate(user.getId(), USER_COMPLETED_REGISTRATION);
            TelegramUser telegramUser = trackingParser.updateUserStatus(user);
            userRepository.save(user);
            sendNewStatus(telegramUser.getId(), telegramUser.getLastStatus());
        } else {
            sendUpdate(user.getId(), USER_INCOMPLETE_REGISTRATION);
        }

    }

    private TelegramUser getTelegramUser(Long chatId) {
        return userRepository.findById(chatId).orElse(null);
    }

    @SneakyThrows
    public void sendUpdate(Long id, String updateMessage) {
        executeAsync(buildMessage(id, updateMessage));
    }

    private SendMessage buildMessage(Long id, String message) {
        SendMessage sendMessage = new SendMessage(); // Create a SendMessage object with mandatory fields
        sendMessage.setChatId(id.toString());
        sendMessage.setText(message);
        sendMessage.enableHtml(true);
        return sendMessage;
    }

    @Scheduled(fixedRate = 1000 * 60 * 60)
    private void scheduleUpdate() {
        log.info("Scheduled update. Retrieving information for users.");
        List<TelegramUser> updatedUsers = trackingParser.updateApplicationStatuses(userRepository.findAll());
        userRepository.saveAll(updatedUsers);
        updatedUsers.forEach(user -> sendNewStatus(user.getId(), user.getLastStatus()));
        log.info("Scheduled update complete. Updated statuses for " + updatedUsers.size() + " out of " + userRepository.findAll().size() + " users.");
    }

    @SneakyThrows
    public void sendNewStatus(Long id, String updateMessage) {
        sendUpdate(id, decryptData(updateMessage));
    }

}

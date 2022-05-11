package com.drfeederino.telegramwebchecker.parsers;

import com.drfeederino.telegramwebchecker.entities.TelegramUser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.drfeederino.telegramwebchecker.constants.BotMessages.UNKNOWN_STATUS;
import static com.drfeederino.telegramwebchecker.services.AESEncryption.decryptData;
import static com.drfeederino.telegramwebchecker.services.AESEncryption.encryptData;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

@Slf4j
@Service
public class IDataTrackingParser extends TrackingParser {
    private static final String STATUS_CHECK = "https://idata.com.tr/de/en/app/%s/%s/1";
    private static final String USER_STATUS_MESSAGE = "%s,\nYour status is:\n%s\n<i>In English:</i>\n<b>%s</b>\n<i>in Russian:</i>\n<b>%s</b>\n";
    private static final String TRANSLATE_PAGE = "https://translate.google.co.in/?sl=auto&tl=%s&text=%s&op=translate";
    private static final String TRANSLATION_RESULT_XPATH = "/html/body/c-wiz/div/div[2]/c-wiz/div[2]/c-wiz/div[1]/div[2]/div[3]/c-wiz[2]/div[8]/div/div[1]";

    private static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return "Unknown user";
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    @Override
    public List<TelegramUser> updateApplicationStatuses(List<TelegramUser> users) {
        FirefoxDriver driver = createHeadlessDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10L));
        ArrayList<TelegramUser> updatedUsers = new ArrayList<>();
        try {
            users.stream()
                    .filter(user -> user.getBarcode() != null && user.getNumber() != null)
                    .forEach(data -> {
                        try {
                            String[] numbers = decryptData(data.getNumber()).split(";");
                            String[] barcodes = decryptData(data.getBarcode()).split(";");
                            StringBuilder result = new StringBuilder("");
                            for (int i = 0; i < numbers.length; i++) {
                                driver.get(String.format(STATUS_CHECK, numbers[i], barcodes[i]));
                                String body = wait.until(presenceOfElementLocated(By.tagName("body"))).getText();
                                driver.get(String.format(TRANSLATE_PAGE, "en", body.split("#")[1]));
                                String eng = wait.until(presenceOfElementLocated(By.xpath(TRANSLATION_RESULT_XPATH))).getText();
                                driver.get(String.format(TRANSLATE_PAGE, "ru", body.split("#")[1]));
                                String rus = wait.until(presenceOfElementLocated(By.xpath(TRANSLATION_RESULT_XPATH))).getText();
                                result.append(parseText(body, eng, rus));
                                log.info("Successfully parsed body.");
                            }
                            compareStatusesAndUpdate(data, result.toString(), updatedUsers);
                        } catch (Exception e) {
                            log.info("Application status not found.");
                            compareStatusesAndUpdate(data, UNKNOWN_STATUS, updatedUsers);
                        }
                    });
        } finally {
            driver.quit();
        }

        return updatedUsers;
    }

    private String parseText(String result, String eng, String rus) {
        if (result == null || result.isEmpty()) {
            return null;
        }
        String[] strings = result.split("#");
        if (strings.length <= 2) {
            return "Application can't be parsed.;";
        }
        return String.format(USER_STATUS_MESSAGE, capitalizeFirstLetter(strings[2].trim()), strings[1].trim(), eng, rus) + ";";
    }

    @SneakyThrows
    private void compareStatusesAndUpdate(TelegramUser user, String result, List<TelegramUser> updatedUsers) {
        if (result != null && !result.equalsIgnoreCase(decryptData(user.getLastStatus()))) {
            user.setLastStatus(encryptData(result));
            updatedUsers.add(user);
        }
    }

    @SneakyThrows
    private void compareStatusesAndUpdate(TelegramUser user, String result) {
        if (result != null && !result.equalsIgnoreCase(decryptData(user.getLastStatus()))) {
            user.setLastStatus(encryptData(result));
        }
    }

    @Override
    public TelegramUser updateUserStatus(TelegramUser user) {
        FirefoxDriver driver = createHeadlessDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10L));
        try {
            String[] numbers = decryptData(user.getNumber()).split(";");
            String[] barcodes = decryptData(user.getBarcode()).split(";");
            StringBuilder result = new StringBuilder("");
            for (int i = 0; i < numbers.length; i++) {
                driver.get(String.format(STATUS_CHECK, numbers[i], barcodes[i]));
                String body = wait.until(presenceOfElementLocated(By.tagName("body"))).getText();
                driver.get(String.format(TRANSLATE_PAGE, "en", body.split("#")[1]));
                String eng = wait.until(presenceOfElementLocated(By.xpath(TRANSLATION_RESULT_XPATH))).getText();
                driver.get(String.format(TRANSLATE_PAGE, "ru", body.split("#")[1]));
                String rus = wait.until(presenceOfElementLocated(By.xpath(TRANSLATION_RESULT_XPATH))).getText();
                result.append(parseText(body, eng, rus));
                log.info("Successfully parsed body.");
            }
            compareStatusesAndUpdate(user, result.toString());
        } catch (Exception e) {
            log.info("Application status not found.");
            compareStatusesAndUpdate(user, UNKNOWN_STATUS);
        } finally {
            driver.quit();
        }

        return user;
    }
}

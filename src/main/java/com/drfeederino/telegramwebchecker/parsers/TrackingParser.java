package com.drfeederino.telegramwebchecker.parsers;

import com.drfeederino.telegramwebchecker.entities.TelegramUser;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.List;

public abstract class TrackingParser {
    public abstract List<TelegramUser> updateApplicationStatuses(List<TelegramUser> users);
    public abstract TelegramUser updateUserStatus(TelegramUser user);
    protected FirefoxDriver createHeadlessDriver() {
        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(true);
        options.setLogLevel(FirefoxDriverLogLevel.FATAL);
        return new FirefoxDriver(options);
    }

}

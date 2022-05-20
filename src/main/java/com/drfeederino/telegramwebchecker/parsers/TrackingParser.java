package com.drfeederino.telegramwebchecker.parsers;

import com.drfeederino.telegramwebchecker.entities.TelegramUser;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public abstract class TrackingParser {
    private static final URL DOCKER_REMOTE_ADDRESS;

    static {
        try {
            String url = System.getenv("SELENIUM_ADDRESS");
            DOCKER_REMOTE_ADDRESS = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract List<TelegramUser> updateApplicationStatuses(List<TelegramUser> users);

    public abstract TelegramUser updateUserStatus(TelegramUser user);

    protected RemoteWebDriver createHeadlessDriver() {
        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(true);
        options.setLogLevel(FirefoxDriverLogLevel.FATAL);
        return new RemoteWebDriver(DOCKER_REMOTE_ADDRESS, options);
    }

}

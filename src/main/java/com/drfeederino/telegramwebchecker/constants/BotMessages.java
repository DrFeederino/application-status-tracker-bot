package com.drfeederino.telegramwebchecker.constants;

public final class BotMessages {
    public static final String WELCOME_MESSAGE = "Hi!\uD83D\uDC4B\n" +
            "I can check your application status on the iData's website and notify you if anything's new.\n" +
            "To function correctly I require your passport number and barcode.\n" +
            "This info is needed so I can keep an eye on application status. The check is performed hourly.\n" +
            "Message is expected to be like this:\n" +
            "XXXXX XXXXX\n" +
            "where the first number is your passport, and the latter is your barcode number.\n" +
            "<b>New</b>: can track multiple applications. Just provide another pair of passport number & barcode!\n" +
            "If you reply with the proper format of message, you give your consent to store personal information.\n" +
            "The bot can collect your: name, telegram user id, passport number, barcode number & the last status of application. Everything (except user ID) is stored in the encrypted form.\n" +
            "If you want to delete all your information, simply send /stop command.";
    public static final String USER_COMPLETED_REGISTRATION = "All info is set. Now sit back and relax. I'll let you know if there are any updates.";
    public static final String USER_INCOMPLETE_REGISTRATION = "Something's wrong. I need a number with exact 9 digits in it & barcode should have 7 digits.\n" +
            "Message is expected to be like this:\n" +
            "XXXXX XXXXX\n" +
            "where the first number is your passport, and the latter is your barcode number.\n";
    public static final String USER_DELETED = "Your information has been deleted. Goodbye!";
    public static final String USER_CHECKING_STATUS_NOW = "Checking your information. Hold on...";
    public static final String UNKNOWN_STATUS = "Application hasn't been found or the site is having issues right now.";
    public static final String DUPLICATED_INFO = "Duplicated details of barcode or passport number found. Please, make sure to supply a new pair.";
    public static final String ADDED_ADDITIONAL_INFO = "Successfully added another barcode and passport number for tracking.";

    private BotMessages() {
    }

}

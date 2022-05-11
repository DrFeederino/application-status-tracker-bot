package com.drfeederino.telegramwebchecker.enums;

public enum UserStatus {
    REGISTERED, // user has been registered in the database; requires additional info for tracking
    COMPLETE, // all the user info is provided, ready to rock'n'roll
    MIGRATION_COMPLETE_V_1_1_0; // new status for users updated to new version

    public static UserStatus getEnum(String value) {
        for (UserStatus v : values())
            if (v.name().equalsIgnoreCase(value)) return v;
        return null;
    }

}

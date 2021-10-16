package com.inz.carvisor.entities;

public enum UserPrivileges {
    STANDARD_USER(100),
    MODERATOR(200),
    ADMINISTRATOR(300);

    int level;

    UserPrivileges(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}

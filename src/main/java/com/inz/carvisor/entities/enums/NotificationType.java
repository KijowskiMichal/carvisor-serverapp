package com.inz.carvisor.entities.enums;

public enum NotificationType {

    SPEEDING,
    LEAVING_THE_ZONE;

    public static NotificationType matchNotificationType(String type) {
        if ("SPEEDING".equals(type)) return SPEEDING;
        else return LEAVING_THE_ZONE;
    }
}

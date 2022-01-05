package com.inz.carvisor.constants;

public class AttributeKey {

    public static class CommonKey {

        public static final String PAGE = "page";
        public static final String PAGE_MAX = "pageMax";
        public static final String USER = "user";
        public static final String CAR = "car";
        public static final String LOGIN = "login";
        public static final String PASSWORD = "password";
        public static final String ID = "id";
    }

    public static class User {

        public final static String NAME = "name";
        public final static String SURNAME = "surname";
        public final static String NICK = "nick";
        public final static String PASSWORD = "password";
        public final static String PHONE_NUMBER = "phoneNumber";
        public final static String IMAGE = "image";
        public final static String NFC = "nfc";
        public final static String USER_PRIVILEGES = "userPrivileges";
        public static final String SAFETY_POINTS = "safetyPoints";
        public static final String ECO_POINTS = "ecoPoints";
        public static final String SAFETY_RANKING_POSITION = "safetyRankingPosition";
        public static final String ECO_RANKING_POSITION = "ecoRankingPosition";
        public static final String TIME_FROM = "timeFrom";
        public static final String TIME_TO = "timeTo";
        public static final String TAG = "tag";
    }

    public static class Car {

        public static final String YEAR_OF_PRODUCTION = "yearOfProduction";
    }

    public static class Offence {

        public static final String LIST_OF_OFFENCES = "listOfOffencess";
        public static final String TIME = "time";
        public static final String IMPORTANT = "important";
        public static final String TYPE = "type";
        public static final String VALUE = "value";
        public static final String LOCATION = "location";
    }

    public static class Notification {

        public static final String LIST_OF_NOTIFICATIONS = "listOfNotification";
        public static final String TYPE = "type";
        public static final String VALUE = "value";
        public static final String DATE = "date";
        public static final String LOCATION = "location";
        public static final String USER_ID = "userID";
        public static final String USER_NAME = "userName";
        public static final String DEVICE_ID = "deviceID";
        public static final String DEVICE_LICENSE_PLATE = "deviceLicensePlate";
    }

    public static class Error {

        public static final String TYPE = "type";
        public static final String VALUE = "value";
        public static final String TIMESTAMP = "timestamp";
    }

    public static class Track {

        public static final String LIST_OF_TRACKS = "listOfTracks";
        public static final String TIME = "time";
        public static final String PRIVATE = "privateTrack";
        public static final String GPS_LONGITUDE = "gps_longitude";
        public static final String GPS_LATITUDE = "gps_latitude";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String NFC_TAG = "nfc_tag";
        public static final String DATE = "date";
        public static final String AMOUNT_OF_TRACK = "tracks";
        public static final String ECO_POINTS = "ecoPoints";
        public static final String COMBUSTION = "combustion";
        public static final String SPEED = "speed";
        public static final String REVOLUTION = "revolution";
        public static final String DISTANCE = "distance";

        public static final String LOCATION_FROM = "locationFrom";
        public static final String LOCATION_TO = "locationTo";
        public static final String SAFETY_POINTS = "safetyPoints";
        public static final String LIST_OF_OFFENCES = "listOfOffencess";
        public static final String OBD = "obd";
        public static final String GPS_POS = "gps_pos";
        public static final String ADDRESS = "address";
    }

    public static class Calendar {

        public static final String ID = "id";
        public static final String START_TIMESTAMP = "start";
        public static final String END_TIMESTAMP = "end";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String TYPE = "type";
        public static final String DEVICE_ID = "device";
        public static final String DRAGGABLE = "draggable";
        public static final String REMIND = "remind";
        public static final String COLOR = "color";
    }

    public static class Zone {

        public static final String NAME = "name";
        public static final String POINT_X = "pointX";
        public static final String POINT_Y = "pointY";
        public static final String RADIUS = "radius";
        public static final String LIST_OF_ZONES = "listOfZones";
        public static final String ZONES_IDS = "zonesIds";
    }

    public static class Report {

        public static final String TYPE = "type";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String START = "start";
        public static final String END = "end";
        public static final String LIST_OF_USER_IDS = "listOfUserIds";
        public static final String ID = "id";
        public static final String LOADING = "loading";
        public static final String LIST_OF_REPORTS = "listOfRaports";
    }
}

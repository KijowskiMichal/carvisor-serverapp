package com.inz.carvisor.controller;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.dao.*;
import com.inz.carvisor.entities.builders.*;
import com.inz.carvisor.entities.enums.EventType;
import com.inz.carvisor.entities.enums.NotificationType;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.entities.model.Notification;
import com.inz.carvisor.entities.model.Setting;
import com.inz.carvisor.entities.model.User;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * REST com.inz.carvisor.controller responsible for demo data management.
 */
@RestController
@RequestMapping("/demo")
public class DemoREST {
    public static final String DEF_IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAgAAAAIACAIAAAB7GkOtAAAAAXNSR0IArs4c6QAHOqFJREFUeNrs4V2SbVuSHe";
    public static final String USER_PHOTOS_DIRECTORY_NAME = "demoUserPhotos";
    public static final String CAR_PHOTOS_DIRECTORY_NAME = "demoCarPhotos";

    UserDaoJdbc userDaoJdbc;
    CarDaoJdbc carDaoJdbc;
    SettingDaoJdbc settingDaoJdbc;
    TrackREST trackREST;
    ErrorDaoJdbc errorDaoJdbc;
    NotificationDaoJdbc notificationDaoJdbc;
    CalendarDaoJdbc calendarDaoJdbc;

    @Autowired
    public DemoREST(UserDaoJdbc userDaoJdbc, TrackDaoJdbc trackDaoJdbc,
                    SettingDaoJdbc settingDaoJdbc, CarDaoJdbc carDaoJdbc, TrackREST trackREST,
                    ErrorDaoJdbc errorDaoJdbc, NotificationDaoJdbc notificationDaoJdbc,
                    CalendarDaoJdbc calendarDaoJdbc) {
        this.userDaoJdbc = userDaoJdbc;
        this.carDaoJdbc = carDaoJdbc;
        this.settingDaoJdbc = settingDaoJdbc;
        this.trackREST = trackREST;
        this.errorDaoJdbc = errorDaoJdbc;
        this.notificationDaoJdbc = notificationDaoJdbc;
        this.calendarDaoJdbc = calendarDaoJdbc;
    }

    public static MockHttpServletRequest mockHttpServletRequest(User user, Car car) {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        Objects.requireNonNull(mockHttpServletRequest.getSession()).setAttribute(AttributeKey.CommonKey.USER, user);
        Objects.requireNonNull(mockHttpServletRequest.getSession()).setAttribute(AttributeKey.CommonKey.CAR, car);
        return mockHttpServletRequest;
    }

    public static String getTrackJson() {
        try {
            InputStream trackRatesStream = DemoREST.class.getClassLoader().getResourceAsStream("trackjson/trackRatesSample.json");
            assert trackRatesStream != null;
            return new String(trackRatesStream.readAllBytes());
        } catch (IOException ioException) {
            return "{}";
        }
    }

    public static String getStartTrackJson() {
        try {
            InputStream trackRatesStream = DemoREST.class.getClassLoader().getResourceAsStream("trackjson/startTrack.json");
            assert trackRatesStream != null;
            return new String(trackRatesStream.readAllBytes());
        } catch (IOException ioException) {
            return "{}";
        }
    }

    /**
     * WebMethod which adding example data.
     * <p>
     *
     * @return Returns the 201 status - OK.
     */
    @RequestMapping(value = "/addAll", method = RequestMethod.GET)
    public ResponseEntity<String> addAll() {
        addMockedUsers();
        addMockedCars();
        addMockedSettings();

        User user = userDaoJdbc.getAll().get(0);
        Car car = carDaoJdbc.getAll().get(0);
        addMockedErrors(user, car);

        addMockedNotifications(user, car);
        addMockedEvents(car.getId());
        return DefaultResponse.OK;
    }

    @RequestMapping(value = "/newNotification", method = RequestMethod.GET)
    public ResponseEntity<String> newNotification(HttpServletRequest request, HttpEntity<String> httpEntity) {
        Car car = carDaoJdbc.getAll().get(0);
        User user = userDaoJdbc.getAll().get(0);
        Notification build = new NotificationBuilder()
                .setNotificationType(NotificationType.SPEEDING)
                .setDisplayed(false)
                .setCar(car)
                .setValue(29)
                .setTimeStamp(System.currentTimeMillis() / 1000)
                .setLocation("52.4026280;16.889948")
                .setUser(user)
                .build();
        notificationDaoJdbc.save(build);
        return DefaultResponse.OK;
    }

    @RequestMapping(value = "/newNotificationForOtherUser", method = RequestMethod.GET)
    public ResponseEntity<String> newNotificationForOtherUser(HttpServletRequest request, HttpEntity<String> httpEntity) {
        Car car = carDaoJdbc.getAll().get(1);
        User user = userDaoJdbc.getAll().get(1);
        Notification build = new NotificationBuilder()
                .setNotificationType(NotificationType.LEAVING_THE_ZONE)
                .setDisplayed(false)
                .setCar(car)
                .setValue(50)
                .setTimeStamp(System.currentTimeMillis() / 1000)
                .setLocation("52.4026280;16.889948")
                .setUser(user)
                .build();
        notificationDaoJdbc.save(build);
        return DefaultResponse.OK;
    }

    @RequestMapping(value = "/getPdf", method = RequestMethod.GET)
    public ResponseEntity getPdf() {
        try {
            Document document = new Document();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();
            Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
            Chunk chunk = new Chunk("Hello World", font);
            document.add(chunk);
            document.close();
            byte[] inFileBytes = byteArrayOutputStream.toByteArray();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(inFileBytes);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return DefaultResponse.badRequest("something went wrong");
    }

    @RequestMapping(value = "/addTracks", method = RequestMethod.POST)
    public ResponseEntity addTracks() {
        User userSecond = mockSecondUserFromDatabase();
        Car carSecond = mockCarFromDatabase();
        HttpServletRequest httpServletRequestSecond = mockHttpServletRequest(userSecond, carSecond);

        String startTrackString = getStartTrackJson();
        String trackRatesString = getTrackJson();

        trackREST.startTrack(httpServletRequestSecond, new HttpEntity<>(startTrackString));
        trackREST.updateTrackData(httpServletRequestSecond, new HttpEntity<>(trackRatesString));
        trackREST.endOfTrack(httpServletRequestSecond, null);
        return DefaultResponse.OK;
    }

    private Car mockCarFromDatabase() {
        Car car = new CarBuilder()
                .setLicensePlate("DWL5636")
                .setBrand("Ford")
                .setModel("Focus")
                .setProductionDate(1990)
                .setImage(extractBase64Picture(7, USER_PHOTOS_DIRECTORY_NAME))
                .setPassword(DigestUtils.sha256Hex("safdsdsf"))
                .setTank(50)
                .setFuelNorm(7D)
                .build();
        carDaoJdbc.save(car);
        return car;
    }

    private User mockSecondUserFromDatabase() {
        User user = new UserBuilder()
                .setNick("Mocked")
                .setName("Mocked")
                .setSurname("Mocked")
                .setPassword(DigestUtils.sha256Hex("absx"))
                .setUserPrivileges(UserPrivileges.STANDARD_USER)
                .setImage(extractBase64Picture(7, USER_PHOTOS_DIRECTORY_NAME))
                .setPhoneNumber(12443134)
                .setNfcTag("AAC")
                .build();
        userDaoJdbc.save(user);
        return user;
    }

    public List<Car> getCarList() {
        return List.of(
                new CarBuilder()
                        .setLicensePlate("DWL5636")
                        .setBrand("Ford")
                        .setModel("Focus")
                        .setProductionDate(1990)
                        .setImage(extractBase64Picture(1, CAR_PHOTOS_DIRECTORY_NAME))
                        .setPassword(DigestUtils.sha256Hex("safdsdsf"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("EPI6395")
                        .setBrand("Renault")
                        .setModel("Laguna")
                        .setProductionDate(1993)
                        .setImage(extractBase64Picture(2, CAR_PHOTOS_DIRECTORY_NAME))
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("WA42613")
                        .setBrand("BMW")
                        .setModel("X6")
                        .setProductionDate(1993)
                        .setImage(extractBase64Picture(3, CAR_PHOTOS_DIRECTORY_NAME))
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("CW02838")
                        .setBrand("Kia")
                        .setModel("Picanto")
                        .setProductionDate(1993)
                        .setImage(extractBase64Picture(4, CAR_PHOTOS_DIRECTORY_NAME))
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("FZA5527")
                        .setBrand("Audi")
                        .setModel("A4")
                        .setProductionDate(1993)
                        .setImage(extractBase64Picture(5, CAR_PHOTOS_DIRECTORY_NAME))
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("GKW0134")
                        .setBrand("Ford")
                        .setModel("Fiesta")
                        .setProductionDate(1993)
                        .setImage(extractBase64Picture(6, CAR_PHOTOS_DIRECTORY_NAME))
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("WL85883")
                        .setBrand("Opel")
                        .setModel("Corsa")
                        .setProductionDate(1993)
                        .setImage(extractBase64Picture(7, CAR_PHOTOS_DIRECTORY_NAME))
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("EWE1751")
                        .setBrand("Volkswagen")
                        .setModel("Passat")
                        .setProductionDate(1993)
                        .setImage(extractBase64Picture(8, CAR_PHOTOS_DIRECTORY_NAME))
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("NEB1632")
                        .setBrand("Hyundai")
                        .setModel("I20")
                        .setProductionDate(1993)
                        .setImage(extractBase64Picture(9, CAR_PHOTOS_DIRECTORY_NAME))
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build()
        );
    }

    public String extractBase64Picture(int pictureNumber, String directoryName) {
        try {
            InputStream demoPhotosStream = this.getClass().getClassLoader().getResourceAsStream("/" + directoryName + "/" + pictureNumber);
            assert demoPhotosStream != null;
            return new String(demoPhotosStream.readAllBytes());
        } catch (IOException ioException) {
            return DEF_IMAGE;
        }
    }

    private void addMockedEvents(long deviceId) {
        List.of(
                new EventBuilder().setTitle("Event").setDraggable(true).setType(EventType.SERVICE.getType()).setDescription("Serwis Pojazdu").setStartTimestamp(getThreeDaysBeforeTimeStamp()).setEndTimestamp(getThreeDaysBeforeTimeStamp() + 3000).setDeviceId(deviceId).setRemind(true).build(),
                new EventBuilder().setTitle("Event").setDraggable(true).setType(EventType.SERVICE.getType()).setDescription("Serwis Pojazdu").setStartTimestamp(getThreeDaysBeforeTimeStamp()).setEndTimestamp(getThreeDaysBeforeTimeStamp() + 3000).setDeviceId(deviceId).setRemind(true).build(),
                new EventBuilder().setTitle("Event").setDraggable(true).setType(EventType.SERVICE.getType()).setDescription("Serwis Pojazdu").setStartTimestamp(getThreeDaysBeforeTimeStamp()).setEndTimestamp(getThreeDaysBeforeTimeStamp() + 3000).setDeviceId(deviceId).setRemind(true).build(),
                new EventBuilder().setTitle("Event").setDraggable(true).setType(EventType.SERVICE.getType()).setDescription("Serwis Pojazdu").setStartTimestamp(getThreeDaysBeforeTimeStamp()).setEndTimestamp(getThreeDaysBeforeTimeStamp() + 3000).setDeviceId(deviceId).setRemind(true).build(),
                new EventBuilder().setTitle("Event").setDraggable(true).setType(EventType.SERVICE.getType()).setDescription("Serwis Pojazdu").setStartTimestamp(getThreeDaysBeforeTimeStamp()).setEndTimestamp(getThreeDaysBeforeTimeStamp() + 3000).setDeviceId(deviceId).setRemind(true).build()
        ).forEach(calendarDaoJdbc::save);
    }

    private void addMockedNotifications(User user, Car car) {
        List.of(
                new NotificationBuilder().setNotificationType(NotificationType.LEAVING_THE_ZONE).setValue(10).setUser(user).setCar(car).setDisplayed(false).setTimeStamp(getThreeDaysBeforeTimeStamp()).setLocation("52.448235;16.907205").build(),
                new NotificationBuilder().setNotificationType(NotificationType.SPEEDING).setValue(50).setUser(user).setCar(car).setDisplayed(false).setTimeStamp(getThreeDaysBeforeTimeStamp()).setLocation("52.448235;16.907205").build(),
                new NotificationBuilder().setNotificationType(NotificationType.SPEEDING).setValue(30).setUser(user).setCar(car).setDisplayed(false).setTimeStamp(getThreeDaysBeforeTimeStamp()).setLocation("52.448235;16.907205").build(),
                new NotificationBuilder().setNotificationType(NotificationType.LEAVING_THE_ZONE).setValue(10).setUser(user).setCar(car).setDisplayed(false).setTimeStamp(getThreeDaysBeforeTimeStamp()).setLocation("52.448235;16.907205").build()
        ).forEach(notificationDaoJdbc::save);
    }

    private long getThreeDaysBeforeTimeStamp() {
        return System.currentTimeMillis() / 1000 - 432000;
    }

    private void addMockedErrors(User user, Car car) {
        List.of(
                new ErrorBuilder().setUser(user).setDeviceLicensePlate(car.getLicensePlate()).setLocation("52.448235;16.907205").setUserName(user.getNameAndSurname()).setCar(car).setDate(getThreeDaysBeforeTimeStamp()).setTimestamp(getThreeDaysBeforeTimeStamp()).setType("ErrorTypeOne").setValue("ErrorValueOne").build(),
                new ErrorBuilder().setUser(user).setDeviceLicensePlate(car.getLicensePlate()).setLocation("52.448235;16.907205").setUserName(user.getNameAndSurname()).setCar(car).setDate(getThreeDaysBeforeTimeStamp()).setTimestamp(getThreeDaysBeforeTimeStamp()).setType("ErrorTypetwo").setValue("ErrorValueTwo").build(),
                new ErrorBuilder().setUser(user).setDeviceLicensePlate(car.getLicensePlate()).setLocation("52.448235;16.907205").setUserName(user.getNameAndSurname()).setCar(car).setDate(getThreeDaysBeforeTimeStamp()).setTimestamp(getThreeDaysBeforeTimeStamp()).setType("ErrorTypethree").setValue("ErrorValueThree").build()
        ).forEach(errorDaoJdbc::save);
    }

    private void addMockedSettings() {
        List.of(
                new Setting("sendInterval", 15),
                new Setting("locationInterval", 15),
                new Setting("historyTimeout", 180)
        ).forEach(settingDaoJdbc::save);
    }

    private void addMockedCars() {
        getCarList().forEach(carDaoJdbc::save);
    }

    private void addMockedUsers() {
        List.of(
                new UserBuilder().setNick("admin").setName("Jan").setSurname("Kowalski").setPassword(DigestUtils.sha256Hex("absx")).setUserPrivileges(UserPrivileges.ADMINISTRATOR).setImage(extractBase64Picture(1, USER_PHOTOS_DIRECTORY_NAME)).setPhoneNumber(12443134).setNfcTag("AAA").build(),
                new UserBuilder().setNick("zenek").setName("Zenon").setSurname("Kolodziej").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage(extractBase64Picture(2, USER_PHOTOS_DIRECTORY_NAME)).setPhoneNumber(12378456).setNfcTag("AAB").build(),
                new UserBuilder().setNick("user3").setName("Maciej").setSurname("Jakubowski").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage(extractBase64Picture(3, USER_PHOTOS_DIRECTORY_NAME)).setPhoneNumber(12354316).setNfcTag("AAC").build(),
                new UserBuilder().setNick("user4").setName("Janina").setSurname("Zakrzewska").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage(extractBase64Picture(4, USER_PHOTOS_DIRECTORY_NAME)).setPhoneNumber(23455342).setNfcTag("ABB").build(),
                new UserBuilder().setNick("user5").setName("Piotr").setSurname("Blaszczyk").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage(extractBase64Picture(5, USER_PHOTOS_DIRECTORY_NAME)).setPhoneNumber(132213).setNfcTag("ACC").build(),
                new UserBuilder().setNick("user6").setName("Marian").setSurname("Ostrowski").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage(extractBase64Picture(6, USER_PHOTOS_DIRECTORY_NAME)).setPhoneNumber(12341).setNfcTag("BBA").build(),
                new UserBuilder().setNick("user7").setName("Kamil").setSurname("Cieaslak").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage(extractBase64Picture(7, USER_PHOTOS_DIRECTORY_NAME)).setPhoneNumber(123451).setNfcTag("BCA").build(),
                new UserBuilder().setNick("user8").setName("Aleksander").setSurname("Zielizxski").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage(extractBase64Picture(8, USER_PHOTOS_DIRECTORY_NAME)).setPhoneNumber(21344312).setNfcTag("ACA").build(),
                new UserBuilder().setNick("user9").setName("Jakub").setSurname("Szymczak").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage(extractBase64Picture(9, USER_PHOTOS_DIRECTORY_NAME)).setPhoneNumber(43656543).setNfcTag("CCC").build(),
                new UserBuilder().setNick("user10").setName("Agnieszka").setSurname("Wasilewska").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage(extractBase64Picture(10, USER_PHOTOS_DIRECTORY_NAME)).setPhoneNumber(34255342).setNfcTag("CDA").build()
        ).forEach(userDaoJdbc::save);
    }
}

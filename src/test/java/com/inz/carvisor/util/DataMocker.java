package com.inz.carvisor.util;

import com.inz.carvisor.entities.builders.CarBuilder;
import com.inz.carvisor.entities.builders.TrackBuilder;
import com.inz.carvisor.entities.builders.UserBuilder;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;
import java.util.Random;

public class DataMocker {

    private static final Random random = new Random();
    private static final int SECONDS_IN_ONE_DAY = 86_400;

    public static List<User> getUsers() {

        return List.of(
                new UserBuilder().setNick("admin").setName("Jan").setSurname("Kowalski").setPassword(DigestUtils.sha256Hex("absx")).setUserPrivileges(UserPrivileges.ADMINISTRATOR).setImage("Image").setPhoneNumber(12443134).setNfcTag("AAA").build(),
                new UserBuilder().setNick("zenek").setName("Zenon").setSurname("Kolodziej").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage("Image").setPhoneNumber(12378456).setNfcTag("AAB").build(),
                new UserBuilder().setNick("user3").setName("Maciej").setSurname("Jakubowski").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage("Image").setPhoneNumber(12354316).setNfcTag("AAC").build(),
                new UserBuilder().setNick("user4").setName("Janina").setSurname("Zakrzewska").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage("Image").setPhoneNumber(23455342).setNfcTag("ABB").build(),
                new UserBuilder().setNick("user5").setName("Piotr").setSurname("Blaszczyk").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage("Image").setPhoneNumber(132213).setNfcTag("ACC").build(),
                new UserBuilder().setNick("user6").setName("Marian").setSurname("Ostrowski").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage("Image").setPhoneNumber(12341).setNfcTag("BBA").build(),
                new UserBuilder().setNick("user7").setName("Kamil").setSurname("Cieaslak").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage("Image").setPhoneNumber(123451).setNfcTag("BCA").build(),
                new UserBuilder().setNick("user8").setName("Aleksander").setSurname("Zielizxski").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage("Image").setPhoneNumber(21344312).setNfcTag("ACA").build(),
                new UserBuilder().setNick("user9").setName("Jakub").setSurname("Szymczak").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage("Image").setPhoneNumber(43656543).setNfcTag("CCC").build(),
                new UserBuilder().setNick("user10").setName("Agnieszka").setSurname("Wasilewska").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage("Image").setPhoneNumber(34255342).setNfcTag("CDA").build()
        );
    }

    public static List<Car> getCars() {
        return List.of(
                new CarBuilder()
                        .setLicensePlate("DWL5636")
                        .setBrand("Ford")
                        .setModel("Focus")
                        .setProductionDate(1990)
                        .setImage("image")
                        .setPassword(DigestUtils.sha256Hex("safdsdsf"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("EPI6395")
                        .setBrand("Renault")
                        .setModel("Laguna")
                        .setProductionDate(1993)
                        .setImage("image")
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("WA42613")
                        .setBrand("BMW")
                        .setModel("X6")
                        .setProductionDate(1993)
                        .setImage("image")
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("CW02838")
                        .setBrand("Kia")
                        .setModel("Picanto")
                        .setProductionDate(1993)
                        .setImage("image")
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("FZA5527")
                        .setBrand("Audi")
                        .setModel("A4")
                        .setProductionDate(1993)
                        .setImage("image")
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("GKW0134")
                        .setBrand("Ford")
                        .setModel("Fiesta")
                        .setProductionDate(1993)
                        .setImage("image")
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("WL85883")
                        .setBrand("Opel")
                        .setModel("Corsa")
                        .setProductionDate(1993)
                        .setImage("image")
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("EWE1751")
                        .setBrand("Volkswagen")
                        .setModel("Passat")
                        .setProductionDate(1993)
                        .setImage("image")
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("NEB1632")
                        .setBrand("Hyundai")
                        .setModel("I20")
                        .setProductionDate(1993)
                        .setImage("image")
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build()
        );
    }
}

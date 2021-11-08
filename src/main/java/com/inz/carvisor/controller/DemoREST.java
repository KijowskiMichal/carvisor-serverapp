package com.inz.carvisor.controller;

import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.dao.CarDaoJdbc;
import com.inz.carvisor.dao.SettingDaoJdbc;
import com.inz.carvisor.dao.TrackDaoJdbc;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.builders.CarBuilder;
import com.inz.carvisor.entities.builders.UserBuilder;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.entities.model.Setting;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.List;

/**
 * REST com.inz.carvisor.controller responsible for demo data management.
 */
@RestController
@RequestMapping("/demo")
public class DemoREST {
    public static final String DEF_IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAgAAAAIACAIAAAB7GkOtAAAAAXNSR0IArs4c6QAHOqFJREFUeNrs4V2SbVuSHe";
    UserDaoJdbc userDaoJdbc;
    CarDaoJdbc carDaoJdbc;
    SettingDaoJdbc settingDaoJdbc;

    @Autowired
    public DemoREST(UserDaoJdbc userDaoJdbc, TrackDaoJdbc trackDaoJdbc,
                    SettingDaoJdbc settingDaoJdbc, CarDaoJdbc carDaoJdbc) {
        this.userDaoJdbc = userDaoJdbc;
        this.carDaoJdbc = carDaoJdbc;
        this.settingDaoJdbc = settingDaoJdbc;
    }

    /**
     * WebMethod which adding example data.
     * <p>
     *
     * @return Returns the 201 status - OK.
     */
    //todo zamiast linku do image do base64
    @RequestMapping(value = "/addAll", method = RequestMethod.GET)
    public ResponseEntity addAll() {
        List.of(
                new UserBuilder().setNick("admin").setName("Jaźn").setSurname("Kowalski").setPassword(DigestUtils.sha256Hex("absx")).setUserPrivileges(UserPrivileges.ADMINISTRATOR).setImage(extractBase64Picture(1)).setPhoneNumber(12443134).setNfcTag("AAA").build(),
                new UserBuilder().setNick("zenek").setName("Zenon").setSurname("Kolodziej").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage(extractBase64Picture(2)).setPhoneNumber(12378456).setNfcTag("AAB").build(),
                new UserBuilder().setNick("user3").setName("Maciej").setSurname("Jakubowski").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage(extractBase64Picture(3)).setPhoneNumber(12354316).setNfcTag("AAC").build(),
                new UserBuilder().setNick("user4").setName("Janina").setSurname("Zakrzewska").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage(extractBase64Picture(4)).setPhoneNumber(23455342).setNfcTag("ABB").build(),
                new UserBuilder().setNick("user5").setName("Piotr").setSurname("Blaszczyk").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage(extractBase64Picture(5)).setPhoneNumber(132213).setNfcTag("ACC").build(),
                new UserBuilder().setNick("user6").setName("Marian").setSurname("Ostrowski").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage(extractBase64Picture(6)).setPhoneNumber(12341).setNfcTag("BBA").build(),
                new UserBuilder().setNick("user7").setName("Kamil").setSurname("Cieaslak").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage(extractBase64Picture(7)).setPhoneNumber(123451).setNfcTag("BCA").build(),
                new UserBuilder().setNick("user8").setName("Aleksander").setSurname("Zielizxski").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage(extractBase64Picture(8)).setPhoneNumber(21344312).setNfcTag("ACA").build(),
                new UserBuilder().setNick("user9").setName("Jakub").setSurname("Szymczak").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage(extractBase64Picture(9)).setPhoneNumber(43656543).setNfcTag("CCC").build(),
                new UserBuilder().setNick("user10").setName("Agnieszka").setSurname("Wasilewska").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage(extractBase64Picture(10)).setPhoneNumber(34255342).setNfcTag("CDA").build()
        ).forEach(userDaoJdbc::save);

        getCarList().forEach(carDaoJdbc::save);

        List.of(
                new Setting("sendInterval", 15),
                new Setting("locationInterval", 15),
                new Setting("historyTimeout", 180)
        ).forEach(settingDaoJdbc::save);


        return DefaultResponse.OK;
    }

    public List<Car> getCarList() {
        return List.of(
                new CarBuilder()
                        .setLicensePlate("DWL5636")
                        .setBrand("Ford")
                        .setModel("Focus")
                        .setProductionDate(1990)
                        .setImage(DEF_IMAGE)
                        .setPassword(DigestUtils.sha256Hex("safdsdsf"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("EPI6395")
                        .setBrand("Renault")
                        .setModel("Laguna")
                        .setProductionDate(1993)
                        .setImage(DEF_IMAGE)
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("WA42613")
                        .setBrand("BMW")
                        .setModel("X6")
                        .setProductionDate(1993)
                        .setImage(DEF_IMAGE)
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("CW02838")
                        .setBrand("Kia")
                        .setModel("Picanto")
                        .setProductionDate(1993)
                        .setImage(DEF_IMAGE)
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("FZA5527")
                        .setBrand("Audi")
                        .setModel("A4")
                        .setProductionDate(1993)
                        .setImage(DEF_IMAGE)
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("GKW0134")
                        .setBrand("Ford")
                        .setModel("Fiesta")
                        .setProductionDate(1993)
                        .setImage(DEF_IMAGE)
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("WL85883")
                        .setBrand("Opel")
                        .setModel("Corsa")
                        .setProductionDate(1993)
                        .setImage(DEF_IMAGE)
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("EWE1751")
                        .setBrand("Volkswagen")
                        .setModel("Passat")
                        .setProductionDate(1993)
                        .setImage(DEF_IMAGE)
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build(),
                new CarBuilder()
                        .setLicensePlate("NEB1632")
                        .setBrand("Hyundai")
                        .setModel("I20")
                        .setProductionDate(1993)
                        .setImage(DEF_IMAGE)
                        .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                        .setTank(50)
                        .setFuelNorm(7D)
                        .build()
        );
    }

    public String extractBase64Picture(int pictureNumber) {
        URL url = this.getClass().getClassLoader().getResource("/demophotos");
        File file = new File(url.getPath() + "/" + pictureNumber);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            return bufferedReader.readLine();
        } catch (Exception e) {
            return DEF_IMAGE;
        }
    }
}

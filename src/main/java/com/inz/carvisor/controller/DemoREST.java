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

import java.util.List;

/**
 * REST com.inz.carvisor.controller responsible for demo data management.
 */
@RestController
@RequestMapping("/demo")
public class DemoREST {
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
  @RequestMapping(value = "/addAll", method = RequestMethod.GET)
  public ResponseEntity addAll() {
    List.of(
            new UserBuilder().setNick("admin").setName("Jaźn").setSurname("Kowalski").setPassword(DigestUtils.sha256Hex("absx")).setUserPrivileges(UserPrivileges.ADMINISTRATOR).setImage("https://upload.wikimedia.org/wikipedia/en/7/7d/Lenna_%28test_image%29.png").setPhoneNumber(12443134).setNfcTag("AAA").build(),
            new UserBuilder().setNick("zenek").setName("Zenon").setSurname("Kolodziej").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage("https://upload.wikimedia.org/wikipedia/en/thumb/7/72/Avatar_icon_green.svg/1024px-Avatar_icon_green.svg.png").setPhoneNumber(12378456).setNfcTag("AAB").build(),
            new UserBuilder().setNick("user3").setName("Maciej").setSurname("Jakubowski").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage("https://cdn.pixabay.com/photo/2020/06/30/10/23/icon-5355896_960_720.png").setPhoneNumber(12354316).setNfcTag("AAC").build(),
            new UserBuilder().setNick("user4").setName("Janina").setSurname("Zakrzewska").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage("https://cdn.pixabay.com/photo/2016/04/15/18/05/computer-1331579_960_720.png").setPhoneNumber(23455342).setNfcTag("ABB").build(),
            new UserBuilder().setNick("user5").setName("Piotr").setSurname("Blaszczyk").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage("https://cdn4.iconfinder.com/data/icons/avatars-xmas-giveaway/128/batman_hero_avatar_comics-512.png").setPhoneNumber(132213).setNfcTag("ACC").build(),
            new UserBuilder().setNick("user6").setName("Marian").setSurname("Ostrowski").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage("https://cdn2.iconfinder.com/data/icons/scenarium-vol-4/128/016_avatar_woman_female_user_account_profile_girl-512.png").setPhoneNumber(12341).setNfcTag("BBA").build(),
            new UserBuilder().setNick("user7").setName("Kamil").setSurname("Cieaslak").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage("https://cdn2.iconfinder.com/data/icons/scenarium-vol-4/128/040_cat_kitty_pussy_pussycat_sleep_animal_meow-512.png").setPhoneNumber(123451).setNfcTag("BCA").build(),
            new UserBuilder().setNick("user8").setName("Aleksander").setSurname("Zielizxski").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage("https://cdn3.iconfinder.com/data/icons/animal-emoji/50/Octopus-512.png").setPhoneNumber(21344312).setNfcTag("ACA").build(),
            new UserBuilder().setNick("user9").setName("Jakub").setSurname("Szymczak").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage("https://cdn2.iconfinder.com/data/icons/animals-nature-2/50/1F984-unicorn-256.png").setPhoneNumber(43656543).setNfcTag("CCC").build(),
            new UserBuilder().setNick("user10").setName("Agnieszka").setSurname("Wasilewska").setPassword(DigestUtils.sha256Hex("xsba")).setUserPrivileges(UserPrivileges.STANDARD_USER).setImage("https://cdn4.iconfinder.com/data/icons/people-avatar-filled-outline/64/girl_ginger_curly__people_woman_teenager_avatar-256.png").setPhoneNumber(34255342).setNfcTag("CDA").build()
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
                    .setImage("https://cdn3.iconfinder.com/data/icons/airport-scenes/64/transfer_confirmation-256.png")
                    .setPassword(DigestUtils.sha256Hex("safdsdsf"))
                    .setTank(50)
                    .setFuelNorm(7D)
                    .build(),
            new CarBuilder()
                    .setLicensePlate("EPI6395")
                    .setBrand("Renault")
                    .setModel("Laguna")
                    .setProductionDate(1993)
                    .setImage("https://cdn4.iconfinder.com/data/icons/city-life/500/traffic-256.png")
                    .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                    .setTank(50)
                    .setFuelNorm(7D)
                    .build(),
            new CarBuilder()
                    .setLicensePlate("WA42613")
                    .setBrand("BMW")
                    .setModel("X6")
                    .setProductionDate(1993)
                    .setImage("https://cdn0.iconfinder.com/data/icons/kameleon-free-pack-rounded/110/Old-Car-2-256.png")
                    .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                    .setTank(50)
                    .setFuelNorm(7D)
                    .build(),
            new CarBuilder()
                    .setLicensePlate("CW02838")
                    .setBrand("Kia")
                    .setModel("Picanto")
                    .setProductionDate(1993)
                    .setImage("https://cdn1.iconfinder.com/data/icons/family-life-flat/340/travel_car_summer_vacation_trip_road_drive_vehicle_journey_adventure_holiday-256.png")
                    .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                    .setTank(50)
                    .setFuelNorm(7D)
                    .build(),
            new CarBuilder()
                    .setLicensePlate("FZA5527")
                    .setBrand("Audi")
                    .setModel("A4")
                    .setProductionDate(1993)
                    .setImage("https://cdn2.iconfinder.com/data/icons/outdoors-people-scenes/64/roadtrip-256.png")
                    .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                    .setTank(50)
                    .setFuelNorm(7D)
                    .build(),
            new CarBuilder()
                    .setLicensePlate("GKW0134")
                    .setBrand("Ford")
                    .setModel("Fiesta")
                    .setProductionDate(1993)
                    .setImage("https://cdn0.iconfinder.com/data/icons/videographer-filmmaker-and-cameraman/339/filming-005-256.png")
                    .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                    .setTank(50)
                    .setFuelNorm(7D)
                    .build(),
            new CarBuilder()
                    .setLicensePlate("WL85883")
                    .setBrand("Opel")
                    .setModel("Corsa")
                    .setProductionDate(1993)
                    .setImage("https://www.bmw-frankcars.pl/www/media/mediapool/homepage_bmw5_limusine_lci2020.jpg")
                    .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                    .setTank(50)
                    .setFuelNorm(7D)
                    .build(),
            new CarBuilder()
                    .setLicensePlate("EWE1751")
                    .setBrand("Volkswagen")
                    .setModel("Passat")
                    .setProductionDate(1993)
                    .setImage("https://cdn3.iconfinder.com/data/icons/transportation-road/112/33-transportation-road_vintage-car-8-256.png")
                    .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                    .setTank(50)
                    .setFuelNorm(7D)
                    .build(),
            new CarBuilder()
                    .setLicensePlate("NEB1632")
                    .setBrand("Hyundai")
                    .setModel("I20")
                    .setProductionDate(1993)
                    .setImage("https://cdn3.iconfinder.com/data/icons/man-daily-routine-people/221/routine-002-512.png")
                    .setPassword(DigestUtils.sha256Hex("dfsdfds"))
                    .setTank(50)
                    .setFuelNorm(7D)
                    .build()
    );
  }
}

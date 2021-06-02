package RestPackage;

import Entities.*;
import HibernatePackage.HibernateRequests;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * REST controller responsible for demo data management.
 */
@RestController
@RequestMapping("/demo")
public class DemoREST
{
    HibernateRequests hibernateRequests;
    @Autowired
    public DemoREST(HibernateRequests hibernateRequests) {
        this.hibernateRequests = hibernateRequests;
    }

    /**
     * @return Returns the 201 status - OK.
     *
     * WebMethod which adding example data.
     */
    @RequestMapping(value = "/addAll", method = RequestMethod.GET)
    public ResponseEntity addAll()
    {
        User user1 = new User("admin", "Jaźn", "Kowalski", DigestUtils.sha256Hex("absx"), UserPrivileges.ADMINISTRATOR, "https://upload.wikimedia.org/wikipedia/en/7/7d/Lenna_%28test_image%29.png", 12443134, "AAA");
        hibernateRequests.addUser(user1);
        User user2 = new User("zenek", "Zenon", "Kolodziej", DigestUtils.sha256Hex("xsba"),UserPrivileges.STANDARD_USER, "https://upload.wikimedia.org/wikipedia/en/thumb/7/72/Avatar_icon_green.svg/1024px-Avatar_icon_green.svg.png", 12378456, "AAB");
        hibernateRequests.addUser(user2);
        User user3 = new User("user3", "Maciej", "Jakubowski", DigestUtils.sha256Hex("xsba"),UserPrivileges.STANDARD_USER, "https://cdn.pixabay.com/photo/2020/06/30/10/23/icon-5355896_960_720.png", 12354316, "AAC");
        hibernateRequests.addUser(user3);
        User user4 = new User("user4", "Janina", "Zakrzewska", DigestUtils.sha256Hex("xsba"),UserPrivileges.STANDARD_USER, "https://cdn.pixabay.com/photo/2016/04/15/18/05/computer-1331579_960_720.png",23455342, "ABB");
        hibernateRequests.addUser(user4);
        User user5 = new User("user5", "Piotr", "Blaszczyk", DigestUtils.sha256Hex("xsba"),UserPrivileges.STANDARD_USER, "https://cdn4.iconfinder.com/data/icons/avatars-xmas-giveaway/128/batman_hero_avatar_comics-512.png", 132213, "ACC");
        hibernateRequests.addUser(user5);
        User user6 = new User("user6", "Marian", "Ostrowski", DigestUtils.sha256Hex("xsba"),UserPrivileges.STANDARD_USER, "https://cdn2.iconfinder.com/data/icons/scenarium-vol-4/128/016_avatar_woman_female_user_account_profile_girl-512.png", 12341, "BBA");
        hibernateRequests.addUser(user6);
        User user7 = new User("user7", "Kamil", "Cieaslak", DigestUtils.sha256Hex("xsba"),UserPrivileges.STANDARD_USER, "https://cdn2.iconfinder.com/data/icons/scenarium-vol-4/128/040_cat_kitty_pussy_pussycat_sleep_animal_meow-512.png",123451,"BCA");
        hibernateRequests.addUser(user7);
        User user8 = new User("user8", "Aleksander", "Zielizxski", DigestUtils.sha256Hex("xsba"),UserPrivileges.STANDARD_USER, "https://cdn3.iconfinder.com/data/icons/animal-emoji/50/Octopus-512.png", 21344312,"ACA");
        hibernateRequests.addUser(user8);
        User user9 = new User("user9", "Jakub", "Szymczak", DigestUtils.sha256Hex("xsba"),UserPrivileges.STANDARD_USER, "https://cdn2.iconfinder.com/data/icons/animals-nature-2/50/1F984-unicorn-256.png",43656543,"CCC");
        hibernateRequests.addUser(user9);
        User user10 = new User("user10", "Agnieszka", "Wasilewska", DigestUtils.sha256Hex("xsba"),UserPrivileges.STANDARD_USER, "https://cdn4.iconfinder.com/data/icons/people-avatar-filled-outline/64/girl_ginger_curly__people_woman_teenager_avatar-256.png",34255342,"CDA");
        hibernateRequests.addUser(user10);

        Car car1 = new Car("DWL5636", "Ford", "Focus", LocalDate.of(1990,12,4), LocalDate.of(2000,1,18), "https://cdn3.iconfinder.com/data/icons/airport-scenes/64/transfer_confirmation-256.png", DigestUtils.sha256Hex("safdsdsf"));
        Car car2 = new Car("EPI6395", "Renault", "Laguna", LocalDate.of(1993,4,18), LocalDate.of(1994,1,14), "https://cdn4.iconfinder.com/data/icons/city-life/500/traffic-256.png", DigestUtils.sha256Hex("dfsdfds"));
        Car car3 = new Car("WA42613", "BMW", "X6", LocalDate.of(1993,4,18), LocalDate.of(1994,1,14), "https://cdn0.iconfinder.com/data/icons/kameleon-free-pack-rounded/110/Old-Car-2-256.png", DigestUtils.sha256Hex("dfsdfds"));
        Car car4 = new Car("CW02838", "Kia", "Picanto", LocalDate.of(1993,4,18), LocalDate.of(1994,1,14), "https://cdn1.iconfinder.com/data/icons/family-life-flat/340/travel_car_summer_vacation_trip_road_drive_vehicle_journey_adventure_holiday-256.png", DigestUtils.sha256Hex("dfsdfds"));
        Car car5 = new Car("FZA5527", "Audi", "A4", LocalDate.of(1993,4,18), LocalDate.of(1994,1,14), "https://cdn2.iconfinder.com/data/icons/outdoors-people-scenes/64/roadtrip-256.png", DigestUtils.sha256Hex("dfsdfds"));
        Car car6 = new Car("GKW0134", "Ford", "Fiesta", LocalDate.of(1993,4,18), LocalDate.of(1994,1,14), "https://cdn0.iconfinder.com/data/icons/videographer-filmmaker-and-cameraman/339/filming-005-256.png", DigestUtils.sha256Hex("dfsdfds"));
        Car car7 = new Car("WL85883", "Opel", "Corsa", LocalDate.of(1993,4,18), LocalDate.of(1994,1,14), "https://www.bmw-frankcars.pl/www/media/mediapool/homepage_bmw5_limusine_lci2020.jpg", DigestUtils.sha256Hex("dfsdfds"));
        Car car8 = new Car("EWE1751", "Volkswagen", "Passat", LocalDate.of(1993,4,18), LocalDate.of(1994,1,14), "https://cdn3.iconfinder.com/data/icons/transportation-road/112/33-transportation-road_vintage-car-8-256.png", DigestUtils.sha256Hex("dfsdfds"));
        Car car9 = new Car("NEB1632", "Hyundai", "I20", LocalDate.of(1993,4,18), LocalDate.of(1994,1,14), "https://cdn3.iconfinder.com/data/icons/man-daily-routine-people/221/routine-002-512.png", DigestUtils.sha256Hex("dfsdfds"));

        hibernateRequests.addCar(car1);
        hibernateRequests.addCar(car2);
        hibernateRequests.addCar(car3);
        hibernateRequests.addCar(car4);
        hibernateRequests.addCar(car5);
        hibernateRequests.addCar(car6);
        hibernateRequests.addCar(car7);
        hibernateRequests.addCar(car8);
        hibernateRequests.addCar(car9);

        Settings set1 = new Settings("sendInterval",Integer.valueOf(15));
        Settings set2 = new Settings("locationInterval", Integer.valueOf(15));
        Settings set3 = new Settings("historyTimeout", Integer.valueOf(180));

        hibernateRequests.addSetting(set1);
        hibernateRequests.addSetting(set2);
        hibernateRequests.addSetting(set3);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/addNfcToAll", method = RequestMethod.GET)
    public ResponseEntity addNfcToAll() {
        Session session = hibernateRequests.getSession();
        Transaction transaction = session.beginTransaction();

        String getAllQuery = "select u FROM User u";
        Query query = session.createQuery(getAllQuery);
        List<User> userList = query.getResultList();

        String a = "A";
        char c = 'A';

        for (User u : userList) {
            u.setNfcTag(a + c);
            session.update(u);
            c++;
        }

        transaction.commit();
        session.close();
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @RequestMapping(value = "/recalculateEcoForAllTracks", method = RequestMethod.POST)
    public ResponseEntity recalculateEcoForAllTrack() {
        Session session = null;
        Transaction tx = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            String getAllQuery = "select t FROM Track t";
            Query query = session.createQuery(getAllQuery);
            List<Track> userList = query.getResultList();



            Random random = new Random();
            for (Track t : userList) {
                t.setEcoPoints(random.nextInt(10));
                session.update(t);
            }
            tx.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (tx != null) tx.rollback();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
        finally {
            if (session != null) session.close();
        }
        return ResponseEntity.status(HttpStatus.OK).body("");
    }
}

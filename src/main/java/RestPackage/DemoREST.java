package RestPackage;

import Entities.Car;
import Entities.Track;
import Entities.User;
import Entities.UserPrivileges;
import HibernatePackage.HibernateRequests;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
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
        User user1 = new User("admin", "Jan", "Kowalski", DigestUtils.sha256Hex("absx"), UserPrivileges.ADMINISTRATOR, "https://upload.wikimedia.org/wikipedia/en/7/7d/Lenna_%28test_image%29.png");
        hibernateRequests.addUser(user1);
        User user2 = new User("zenek", "Zenon", "Marksista", DigestUtils.sha256Hex("xsba"),UserPrivileges.STANDARD_USER, "https://upload.wikimedia.org/wikipedia/en/7/7d/Lenna_%28test_image%29.png");
        hibernateRequests.addUser(user2);
        Car car1 = new Car("DWL5636", "Ford", "Focus", LocalDate.of(1990,12,4), LocalDate.of(2000,1,18), "https://upload.wikimedia.org/wikipedia/en/7/7d/Lenna_%28test_image%29.png", DigestUtils.sha256Hex("safdsdsf"));
        hibernateRequests.addCar(car1);
        Car car2 = new Car("EPI6395", "Renault", "Laguna", LocalDate.of(1993,4,18), LocalDate.of(1994,1,14), "https://upload.wikimedia.org/wikipedia/en/7/7d/Lenna_%28test_image%29.png", DigestUtils.sha256Hex("dfsdfds"));
        hibernateRequests.addCar(car2);
        Track track1 = new Track(car1, user2, LocalDateTime.of(2021, 4,29, 12,18), LocalDateTime.of(2021, 4,29, 13,3));
        hibernateRequests.addTrack(track1);
        Track track2 = new Track(car1, user2, LocalDateTime.of(2021, 4,30, 4,37), null);
        hibernateRequests.addTrack(track2);
        Session session = hibernateRequests.getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            user1.setTrack(track2);
            session.update(user1);
            tx.commit();
            session.close();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}

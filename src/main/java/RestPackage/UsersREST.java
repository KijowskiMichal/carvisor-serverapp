package RestPackage;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersREST
{
    @RequestMapping("/hello")
    @ResponseBody
    public String hello()
    {
        Session session = Hibernate.EntityFactory.getFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return "hello";
    }
}

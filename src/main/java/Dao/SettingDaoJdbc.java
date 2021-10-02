package Dao;

import Entities.Car;
import Entities.Setting;
import HibernatePackage.HibernateRequests;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Class for operating on Setting data from database
 */
@Repository
public class SettingDaoJdbc extends HibernateDaoJdbc<Setting> {

    @Autowired
    public SettingDaoJdbc(HibernateRequests hibernateRequests, OtherClasses.Logger logger) {
        super(hibernateRequests, logger);
    }

    @Override
    public Optional<Setting> get(long id){
        Session session = null;
        Transaction tx = null;
        Setting setting = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            Query query = session.createQuery("SELECT s FROM Setting s WHERE s.id=" + id);
            setting = (Setting) query.getResultList().stream().findFirst().orElse(null);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return Optional.ofNullable(setting);
    }

    @Override
    public List<Setting> getAll() {
        Session session = null;
        Transaction tx = null;
        List<Setting> settings = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            Query query = session.createQuery("SELECT s FROM Setting s");
            settings = query.getResultList();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return settings;
    }

    @Override
    public Optional<Setting> delete(long id) {
        Transaction tx = null;
        Optional<Setting> setting = Optional.empty();
        try (Session session = hibernateRequests.getSession()) {
            tx = session.beginTransaction();
            setting = get(id);
            if (setting.isEmpty())
                throw new HibernateException("");
            session.delete(setting.get());
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return setting;
    }
}

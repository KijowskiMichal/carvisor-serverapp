package HibernatePackage;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class EntityFactory
{
    /**
     * Global object of Hibernate session
     */
    private static SessionFactory factory;

    /**
     * This method creates a session object from the data contained in the configuration file.
     * The method should be executed when starting the server.
     */
    public static void startHibernate()
    {
        try {
            factory = new Configuration().configure().buildSessionFactory();
        }
        catch (Throwable ex)
        {
            System.err.println("Failed to create sessionFactory object." + ex);
        }
    }

    public static SessionFactory getFactory() {
        return factory;
    }

    public static void setFactory(SessionFactory factory) {
        EntityFactory.factory = factory;
    }
}

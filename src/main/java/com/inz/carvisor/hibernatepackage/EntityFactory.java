package com.inz.carvisor.hibernatepackage;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Service;

@Service
public class EntityFactory {

    public SessionFactory getFactory() {
        return new Configuration().configure().buildSessionFactory();
    }
}

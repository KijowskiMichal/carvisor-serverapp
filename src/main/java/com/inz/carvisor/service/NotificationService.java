package com.inz.carvisor.service;

import com.inz.carvisor.hibernatepackage.HibernateRequests;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    HibernateRequests hibernateRequests;
    Logger logger;

    public NotificationService() {

    }

    @Autowired
    public NotificationService(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger) {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
    }
}

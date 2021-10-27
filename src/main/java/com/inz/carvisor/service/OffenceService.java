package com.inz.carvisor.service;

import com.inz.carvisor.dao.OffenceDaoJdbc;
import com.inz.carvisor.entities.model.TrackRate;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.service.offence.OffenceStrategy;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Collection;


@Service
public class OffenceService {

    HibernateRequests hibernateRequests;
    Logger logger;
    OffenceDaoJdbc offenceDaoJdbc;
    OffenceStrategy offenceStrategy;

    @Autowired
    public OffenceService(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger, OffenceDaoJdbc offenceDaoJdbc) {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
        this.offenceDaoJdbc = offenceDaoJdbc;
    }

    public void checkForOffences(Collection<TrackRate> trackRates) {
        trackRates.forEach(this::checkForOffences);
    }

    private void checkForOffences(TrackRate trackRates) {
        //
    }

}

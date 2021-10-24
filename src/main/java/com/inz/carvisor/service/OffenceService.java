package com.inz.carvisor.service;

import com.inz.carvisor.dao.OffenceJdbc;
import com.inz.carvisor.entities.TrackRate;
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
    OffenceJdbc offenceJdbc;
    OffenceStrategy offenceStrategy;

    @Autowired
    public OffenceService(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger, OffenceJdbc offenceJdbc) {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
        this.offenceJdbc = offenceJdbc;
    }

    public void checkForOffences(Collection<TrackRate> trackRates) {
        trackRates.forEach(this::checkForOffences);
    }

    private void checkForOffences(TrackRate trackRates) {
        //
    }

}

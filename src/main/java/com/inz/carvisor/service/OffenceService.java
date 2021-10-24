package com.inz.carvisor.service;

import com.inz.carvisor.dao.OffenceJdbc;
import com.inz.carvisor.entities.Offence;
import com.inz.carvisor.entities.OffenceType;
import com.inz.carvisor.entities.Track;
import com.inz.carvisor.entities.TrackRate;
import com.inz.carvisor.entities.builders.OffenceBuilder;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.service.offence.OffenceStrategy;
import org.apache.logging.log4j.Logger;
import org.hibernate.type.LocalDateTimeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.TimeZone;

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

package Service;

import HibernatePackage.HibernateRequests;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EcoPointsService {

    HibernateRequests hibernateRequests;
    Logger logger;

    @Autowired
    public EcoPointsService(HibernateRequests hibernateRequests, OtherClasses.Logger logger)
    {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
    }

}

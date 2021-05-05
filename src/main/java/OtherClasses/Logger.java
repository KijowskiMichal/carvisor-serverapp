package OtherClasses;

import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Service;

@Service
public class Logger {
    private org.apache.logging.log4j.Logger LOG;

    public Logger()
    {
        LOG = LogManager.getLogger();
    }

    public org.apache.logging.log4j.Logger getLOG() {
        return LOG;
    }
}

package com.inz.carvisor.service.offence;

import com.inz.carvisor.dao.OffenceJdbc;
import com.inz.carvisor.entities.Offence;
import com.inz.carvisor.entities.Track;
import com.inz.carvisor.entities.TrackRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public abstract class OffenceStrategy {

    @Autowired
    OffenceJdbc offenceJdbc;

    public List<OffenceStrategy> getAllStrategies() {
        return List.of(
                new SpeedOffence()
        );
    }

    private void saveOffence(Offence offence) {
        offenceJdbc.save(offence);
    }

    abstract Optional<Offence> createOffenceIfExists(TrackRate trackRate);
}

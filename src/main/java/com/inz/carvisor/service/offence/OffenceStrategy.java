package com.inz.carvisor.service.offence;

import com.inz.carvisor.dao.OffenceDaoJdbc;
import com.inz.carvisor.entities.model.Offence;
import com.inz.carvisor.entities.model.TrackRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public abstract class OffenceStrategy {

    @Autowired
    OffenceDaoJdbc offenceDaoJdbc;

    public List<OffenceStrategy> getAllStrategies() {
        return List.of(
                new SpeedOffence()
        );
    }

    private void saveOffence(Offence offence) {
        offenceDaoJdbc.save(offence);
    }

    abstract Optional<Offence> createOffenceIfExists(TrackRate trackRate);
}

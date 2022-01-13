package com.inz.carvisor.otherclasses;

import com.inz.carvisor.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EndOfTrackRequestEveryEightSecondsOfWorkingCarvisor {
    @Autowired
    private TrackService trackService;

    @Scheduled(fixedRate = 8000)
    public void endOfTrackRequestEveryEightSecondsOfWorkingCarvisor() throws InterruptedException {
        trackService.endOfTrack(null, null);
    }
}

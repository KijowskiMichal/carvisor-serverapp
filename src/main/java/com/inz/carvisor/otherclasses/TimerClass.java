package com.inz.carvisor.otherclasses;

import com.inz.carvisor.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TimerClass {
    @Autowired
    private TrackService trackService;

    @Scheduled(fixedRate = 60000)
    public void endOfTrackRequestEvery60SecondsOfWorkingCarvisor() throws InterruptedException {
        trackService.endOfTrack(null, null);
    }
}

package com.inz.carvisor.otherclasses;

import com.inz.carvisor.service.TrackService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;

@EnableAsync
public class EndOfTrackRequestEveryEightSecondsOfWorkingCarvisor {
    private TrackService trackService;

    @Async
    @Scheduled(fixedRate = 8000)
    public void endOfTrackRequestEveryEightSecondsOfWorkingCarvisor() throws InterruptedException {
        trackService.endOfTrack(null, null);
    }
}

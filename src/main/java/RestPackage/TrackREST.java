package RestPackage;

import Service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * REST controller responsible for Track Managment.
 */
@RestController
@RequestMapping("/track")
public class TrackREST {
    private final TrackService trackService;

    @Autowired
    public TrackREST(TrackService trackService) {
        this.trackService = trackService;
    }

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public ResponseEntity startTrack(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return trackService.startTrack(request, httpEntity);
    }

    @RequestMapping(value = "/updateTrackData/", method = RequestMethod.POST)
    public ResponseEntity updateTrackData(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return trackService.updateTrackData(request, httpEntity);
    }

    @RequestMapping(value = "/updateTrack/", method = RequestMethod.POST)
    public ResponseEntity updateTrack(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return trackService.updateTrack(request, httpEntity);
    }

    @RequestMapping(value = "/endOfTrack/", method = RequestMethod.GET)
    public ResponseEntity endOfTrack(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return trackService.endOfTrack(request, httpEntity);
    }

    @RequestMapping(value = "/getTrackData/{id}/", method = RequestMethod.GET)
    public ResponseEntity getTrackData(HttpServletRequest request, HttpEntity<String> httpEntity, @PathVariable("id") int userID) {
        return trackService.getTrackData(request,httpEntity,userID);
    }

}

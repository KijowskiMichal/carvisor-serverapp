package com.inz.carvisor.controller;

import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.service.SecurityService;
import com.inz.carvisor.service.TrackService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * REST com.inz.carvisor.controller responsible for Track Managment.
 */
@RestController
@RequestMapping("/track")
public class TrackREST {

    private final TrackService trackService;
    private final SecurityService securityService;

    @Autowired
    public TrackREST(TrackService trackService, SecurityService securityService) {
        this.trackService = trackService;
        this.securityService = securityService;
    }

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public ResponseEntity startTrack(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return trackService.startTrack(request, httpEntity);
    }

    @RequestMapping(value = "/updateTrackDataOLD/", method = RequestMethod.POST)
    public ResponseEntity updateTrackDataOLD(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return trackService.updateTrackDataOLD(request, httpEntity);
    }

    @RequestMapping(value = "/updateTrackData/", method = RequestMethod.POST)
    public ResponseEntity updateTrackData(HttpServletRequest request, HttpEntity<String> httpEntity) {
        Car car = (Car) request.getSession().getAttribute("car");
        return trackService.updateTrackData(car, new JSONObject(httpEntity.getBody()));
    }

    @RequestMapping(value = "/updateTrack/", method = RequestMethod.POST)
    public ResponseEntity updateTrack(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return trackService.updateTrack(request, httpEntity);
    }

    @RequestMapping(value = "/endOfTrack/", method = RequestMethod.GET)
    public ResponseEntity endOfTrack(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return trackService.endOfTrack(request, httpEntity);
    }

    @RequestMapping(value = "/getTrackData/{id}/{date}/", method = RequestMethod.GET)
    public ResponseEntity getTrackData(HttpServletRequest request, HttpEntity<String> httpEntity, @PathVariable("id") int userID, @PathVariable("date") long dateTimeStamp) {
        return trackService.getTrackData(request, httpEntity, userID, dateTimeStamp);
    }

    @RequestMapping(value = "/getTrackDataForDevice/{id}/{date}/", method = RequestMethod.GET)
    public ResponseEntity getTrackDataForDevice(HttpServletRequest request, HttpEntity<String> httpEntity, @PathVariable("id") int userID, @PathVariable("date") long date) {
        return trackService.getTrackDataForDevice(request, httpEntity, userID, date);
    }

    @RequestMapping(value = "/list/{id}/{page}/{pagesize}/{dateFrom}/{dateTo}/", method = RequestMethod.GET)
    public ResponseEntity list(HttpServletRequest request, HttpEntity<String> httpEntity,
                               @PathVariable("id") int userID, @PathVariable("page") int page, @PathVariable("pagesize") int pageSize, @PathVariable("dateFrom") long dateFrom, @PathVariable("dateTo") long dateTo) {
        return trackService.list(request, userID, page, pageSize, dateFrom, dateTo);
    }

    @RequestMapping(value = "/reverseGeocoding/{lon}/{lat}/", method = RequestMethod.GET)
    public ResponseEntity reverseGeocoding(HttpServletRequest request, HttpEntity<String> httpEntity, @PathVariable("lon") String lon, @PathVariable("lat") String lat) {
        return trackService.reverseGeocoding(lon, lat);
    }
}

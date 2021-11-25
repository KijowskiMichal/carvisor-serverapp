package com.inz.carvisor.controller;

import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.dao.TrackDaoJdbc;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Zone;
import com.inz.carvisor.service.SecurityService;
import com.inz.carvisor.service.ZoneService;
import com.inz.carvisor.util.jsonparser.ZoneJsonParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/zones")
public class ZoneController {

    private final TrackDaoJdbc trackDaoJdbc;
    private final UserDaoJdbc userDaoJdbc;
    private final SecurityService securityService;
    private final ZoneService zoneService;

    @Autowired
    public ZoneController(TrackDaoJdbc trackDaoJdbc, UserDaoJdbc userDaoJdbc,
                          SecurityService securityService, ZoneService zoneService) {
        this.trackDaoJdbc = trackDaoJdbc;
        this.userDaoJdbc = userDaoJdbc;
        this.securityService = securityService;
        this.zoneService = zoneService;
    }

    @RequestMapping(value = "/updateZone/{id}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<String> updateZone(HttpServletRequest request, HttpEntity<String> httpEntity, @PathVariable("id") int id) {
        if (!securityService.securityProtocolPassed(UserPrivileges.MODERATOR,request)) {
            return DefaultResponse.UNAUTHORIZED;
        }
        Optional<Zone> zone = zoneService.updateZone(id, new JSONObject(httpEntity.getBody()));
        if (zone.isEmpty()) return DefaultResponse.BAD_REQUEST;
        else return DefaultResponse.OK;
    }

    @RequestMapping(value = "/list/{regex}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> list(HttpServletRequest request, @PathVariable("regex") String regex) {
        if (!securityService.securityProtocolPassed(UserPrivileges.MODERATOR,request)) {
            return DefaultResponse.UNAUTHORIZED;
        }
        List<Zone> list = zoneService.list(regex);
        JSONArray zones = new JSONArray();
        list.stream().map(ZoneJsonParser::parse).forEach(zones::put);
        return DefaultResponse.ok(zones.toString());
    }

    @RequestMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<String> add(HttpServletRequest request, HttpEntity<String> httpEntity) {
        if (!securityService.securityProtocolPassed(UserPrivileges.MODERATOR,request)) {
            return DefaultResponse.UNAUTHORIZED;
        }
        JSONObject jsonObject = new JSONObject(httpEntity.getBody());
        Zone parse = ZoneJsonParser.parse(jsonObject);
        if (zoneService.add(parse).isPresent()) return DefaultResponse.OK;
        else return DefaultResponse.BAD_REQUEST;
    }

    @RequestMapping(value = "/remove/{id}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public ResponseEntity<String> remove(HttpServletRequest request, @PathVariable("id") int id) {
        if (!securityService.securityProtocolPassed(UserPrivileges.MODERATOR,request)) {
            return DefaultResponse.UNAUTHORIZED;
        }
        if (zoneService.remove(id).isPresent()) return DefaultResponse.OK;
        else return DefaultResponse.BAD_REQUEST;
    }
}

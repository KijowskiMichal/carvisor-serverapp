package com.inz.carvisor.controller;

import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.service.DevicesService;
import com.inz.carvisor.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * REST com.inz.carvisor.controller responsible for user management.
 */
@RestController
@RequestMapping("/devices")
public class DevicesREST {

    private final DevicesService devicesService;
    private final SecurityService securityService;

    @Autowired
    public DevicesREST(DevicesService devicesService, SecurityService securityService) {
        this.devicesService = devicesService;
        this.securityService = securityService;
    }

    @RequestMapping(value = "/list/{page}/{pageSize}/{regex}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> list(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("pageSize") int pageSize, @PathVariable("regex") String regex) {
        return devicesService.list(request, page, pageSize, regex);
    }

    @RequestMapping(value = "/listFixed/{page}/{pageSize}/{regex}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> listFixed(HttpServletRequest request, @PathVariable("page") int page,
                                            @PathVariable("pageSize") int pageSize, @PathVariable("regex") String regex) {
        return devicesService.listFixed(request, page, pageSize, regex);
    }

    @RequestMapping(value = "/getDeviceData/{id}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity getDeviceData(HttpServletRequest request, HttpEntity<String> httpEntity, @PathVariable("id") int id) {
        return devicesService.getDeviceData(request, httpEntity, id);
    }

    @RequestMapping(value = "/removeDevice/{id}", method = RequestMethod.DELETE)
    public ResponseEntity removeDevice(HttpServletRequest request, @PathVariable("id") int id) {
        if (!securityService.securityProtocolPassed(UserPrivileges.MODERATOR, request)) {
            return DefaultResponse.UNAUTHORIZED;
        }

        Optional<Car> car = devicesService.removeDevice(id);
        if (car.isPresent()) return DefaultResponse.OK;
        else return DefaultResponse.BAD_REQUEST;
    }

    @RequestMapping(value = "/changeDeviceData/{id}/", method = RequestMethod.POST)
    public ResponseEntity changeDeviceData(HttpServletRequest request, HttpEntity<String> httpEntity, @PathVariable("id") int id) {
        return devicesService.changeDeviceData(request, httpEntity, id);
    }

    @RequestMapping(value = "/changeDeviceImage/{id}/", method = RequestMethod.POST)
    public ResponseEntity changeDeviceImage(HttpServletRequest request, HttpEntity<String> httpEntity, @PathVariable("id") int id) {
        return devicesService.changeDeviceImage(request, httpEntity, id);
    }

    @RequestMapping(value = "/addDevice", method = RequestMethod.POST)
    public ResponseEntity addDevice(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return devicesService.addDevice(request, httpEntity);
    }

    @RequestMapping(value = "/listDevicesNames/{regex}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> listDevicesNames(HttpServletRequest request, @PathVariable("regex") String regex) {
        return devicesService.listDevicesNames(request, regex);
    }
}

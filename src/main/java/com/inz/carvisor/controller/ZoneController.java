package com.inz.carvisor.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/zones")
public class ZoneController {

    @RequestMapping(value = "/updateZone/{id}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<String> updateZone(HttpServletRequest request, @PathVariable("id") int id) {
        return null; //todo - to implement
    }

    @RequestMapping(value = "/list/{regex}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> list(HttpServletRequest request, @PathVariable("regex") String regex) {
        return null; //todo - to implement
    }

    @RequestMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<String> add(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return null; //todo - to implement
    }

    @RequestMapping(value = "/remove/{id}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public ResponseEntity<String> remove(HttpServletRequest request, @PathVariable("id") int id) {
        return null; //todo - to implement
    }
}

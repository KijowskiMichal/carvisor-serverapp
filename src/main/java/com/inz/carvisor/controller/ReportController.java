package com.inz.carvisor.controller;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Report;
import com.inz.carvisor.service.report.service.ReportService;
import com.inz.carvisor.service.SecurityService;
import com.inz.carvisor.util.jsonparser.ReportJsonParser;
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
@RequestMapping("/raports")
public class ReportController {

    private final SecurityService securityService;
    private final ReportService reportService;

    @Autowired
    public ReportController(SecurityService securityService, ReportService reportService) {
        this.reportService = reportService;
        this.securityService = securityService;
    }

    @RequestMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<String> add(HttpServletRequest request, HttpEntity<String> httpEntity) {
        if (!securityService.securityProtocolPassed(UserPrivileges.MODERATOR,request)) {
            return DefaultResponse.UNAUTHORIZED;
        }
        JSONObject jsonObject = new JSONObject(httpEntity.getBody());
        Optional<Report> add = reportService.add(ReportJsonParser.parse(jsonObject));

        if (add.isEmpty()) return DefaultResponse.BAD_REQUEST;
        else return DefaultResponse.OK;
    }

    @RequestMapping(value = "/remove/{id}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public ResponseEntity<String> remove(HttpServletRequest request, HttpEntity<String> httpEntity,
                                         @PathVariable("id") int id) {
        if (!securityService.securityProtocolPassed(UserPrivileges.MODERATOR,request)) {
            return DefaultResponse.UNAUTHORIZED;
        }

        Optional<Report> remove = reportService.remove(id);
        if (remove.isPresent()) return DefaultResponse.OK;
        else return DefaultResponse.BAD_REQUEST;
    }

    @RequestMapping(value = "/get/{id}",method = RequestMethod.GET)
    public ResponseEntity<byte[]> get(HttpServletRequest request, HttpEntity<String> httpEntity, @PathVariable("id") int reportId) {

        if (!securityService.securityProtocolPassed(UserPrivileges.MODERATOR,request)) {
            return DefaultResponse.unauthorized();
        }

        Optional<Report> report = reportService.get(reportId);

        if (report.isEmpty()) return DefaultResponse.badRequest();
        else return DefaultResponse.okByte(report.get().getBody());
    }

    @RequestMapping(value = "/list/{page}/{pagesize}/{regex}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> list(HttpServletRequest request, HttpEntity<String> httpEntity,
                                       @PathVariable("page") int page, @PathVariable("pagesize") int pageSize,
                                       @PathVariable("regex") String regex) {
        if (!securityService.securityProtocolPassed(UserPrivileges.MODERATOR,request)) {
            return DefaultResponse.UNAUTHORIZED;
        }
        List<Report> list = reportService.list(page, pageSize, regex);
        JSONArray jsonArray = ReportJsonParser.parse(list);
        JSONObject jsonResponse = new JSONObject()
                .put(AttributeKey.Util.PAGE,page)
                .put(AttributeKey.Util.PAGE_MAX,reportService.getMaxPage(pageSize,regex))
                .put(AttributeKey.Report.LIST_OF_RAPORTS,jsonArray);
        return DefaultResponse.ok(jsonResponse.toString());
    }
}

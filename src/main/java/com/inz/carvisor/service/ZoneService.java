package com.inz.carvisor.service;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.dao.ZoneDaoJdbc;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.entities.model.Zone;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ZoneService {

    private final ZoneDaoJdbc zoneDaoJdbc;

    @Autowired
    public ZoneService(ZoneDaoJdbc zoneDaoJdbc) {
        this.zoneDaoJdbc = zoneDaoJdbc;
    }

    public Optional<Zone> updateZone(int zoneId, JSONObject newZoneValues) {
        Optional<Zone> wrappedZone = zoneDaoJdbc.get(zoneId);
        if (wrappedZone.isEmpty()) return Optional.empty();

        Zone zone = wrappedZone.get();
        zone.setName(newZoneValues.getString(AttributeKey.Zone.NAME));
        zone.setPointX(newZoneValues.getString(AttributeKey.Zone.POINT_X));
        zone.setPointY(newZoneValues.getString(AttributeKey.Zone.POINT_Y));
        zone.setRadius(newZoneValues.getFloat(AttributeKey.Zone.RADIUS));
        return zoneDaoJdbc.update(zone);
    }

    public List<Zone> list(String regex) {
        return zoneDaoJdbc.getListWithName(regex);
    }

    public List<Zone> list(String regex, int page, int pageMax) {
        return zoneDaoJdbc.get(regex, page, pageMax);
    }

    public Optional<Zone> add(Zone zone) {
        return zoneDaoJdbc.save(zone);
    }

    public Optional<Zone> remove(int id) {
        return zoneDaoJdbc.delete(id);
    }

    public Optional<Zone> getZone(int id) {
        return zoneDaoJdbc.get(id);
    }

    public List<Zone> getUserZones(User user) {
        return zoneDaoJdbc.get(user);
    }

    public int checkMaxPage(String regex, int pageSize) {
        return zoneDaoJdbc.checkMaxPageWithRegex(regex, pageSize);
    }


    public List<Zone> getZones(int page, int pagesize) {
        return zoneDaoJdbc.getList(page,pagesize);

    }
}

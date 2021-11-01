package com.inz.carvisor.service;

import com.inz.carvisor.dao.CarDaoJdbc;
import com.inz.carvisor.entities.builders.CarBuilder;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.entities.model.Setting;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.util.jsonparser.CarJsonParser;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Class representing device com.inz.carvisor.service
 */
@Service
public class DevicesService {

  HibernateRequests hibernateRequests;
  Logger logger;
  CarDaoJdbc carDaoJdbc;

  @Autowired
  public DevicesService(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger, CarDaoJdbc carDaoJdbc) {
    this.hibernateRequests = hibernateRequests;
    this.logger = logger.getLOG();
    this.carDaoJdbc = carDaoJdbc;
  }

  /**
   * WebMethod which returns a list of devices.
   * <p>
   *
   * @param request  Object of HttpServletRequest represents our request;
   * @param page     Page of users list. Parameter associated with pageSize.
   * @param pageSize Number of record we want to get
   * @param regex    Part of name or surname we want to display
   * @return Returns the contents of the page that contains a list of devices in the JSON format.
   */
  public ResponseEntity<String> list(HttpServletRequest request, int page, int pageSize, String regex) {
    // authorization
    if (request.getSession().getAttribute("user") == null) {
      logger.info("DevicesREST.list cannot list device's (session not found)");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    } else if ((((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.ADMINISTRATOR) && (((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.MODERATOR)) {
      logger.info("DevicesREST.list cannot list device's because rbac (user: " + ((User) request.getSession().getAttribute("user")).getNick() + ")");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }
    //listing
    List<Object> devices = new ArrayList<>();
    int lastPageNumber;
    Session session = hibernateRequests.getSession();
    Transaction tx = null;
    try {
      if (regex.equals("$")) regex = "";
      tx = session.beginTransaction();
      String countQ = "Select count (c.id) from Car c WHERE c.licensePlate  like '%" + regex + "%' OR c.brand  like '%" + regex + "%'  OR c.model  like '%" + regex + "%' ";
      Query countQuery = session.createQuery(countQ);
      Long countResults = (Long) countQuery.uniqueResult();
      lastPageNumber = (int) (Math.ceil(countResults / (double) pageSize));

      Query selectQuery = session.createQuery("SELECT c from Car c WHERE c.licensePlate  like '%" + regex + "%' OR c.brand  like '%" + regex + "%'  OR c.model  like '%" + regex + "%' ");
      selectQuery.setFirstResult((page - 1) * pageSize);
      selectQuery.setMaxResults(pageSize);
      devices = selectQuery.list();
      tx.commit();
      session.close();
    } catch (HibernateException e) {
      if (tx != null) tx.rollback();
      session.close();
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
    }
    JSONObject jsonOut = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    for (Object tmp : devices) {
      JSONObject jsonObject = CarJsonParser.parseBasic((Car) tmp);
      try {
        session = hibernateRequests.getSession();
        tx = session.beginTransaction();
        Date now = new Date();
        LocalDateTime before = LocalDateTime.ofInstant(Instant.ofEpochMilli(now.getTime()), TimeZone.getDefault().toZoneId()).with(LocalTime.MIN);
        Timestamp timestampBefore = Timestamp.valueOf(before);
        LocalDateTime after = LocalDateTime.ofInstant(Instant.ofEpochMilli(now.getTime()), TimeZone.getDefault().toZoneId()).with(LocalTime.MAX);
        Timestamp timestampAfter = Timestamp.valueOf(after);
        Query countQ = session.createQuery("Select sum (t.distance) from TrackRate t WHERE t.timestamp > " + timestampBefore.getTime() / 1000 + " AND  t.timestamp < " + timestampAfter.getTime() / 1000 + " AND t.track.car.id = " + ((Car) tmp).getId());
        Long lonk = (Long) countQ.getSingleResult();
        jsonObject.put("distance", String.valueOf(lonk == null ? 0 : lonk));
        Query selectQuery = session.createQuery("SELECT t FROM Track t WHERE t.isActive = true AND t.car.id = " + ((Car) tmp).getId());
        List<Track> tracks = selectQuery.list();
        if (tracks.size() > 0) {
          jsonObject.put("status", "Aktywny");
        } else {
          jsonObject.put("status", "Nieaktywny");
        }
        tx.commit();
        session.close();
      } catch (HibernateException e) {
        if (tx != null) tx.rollback();
        session.close();
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
      }
      jsonArray.put(jsonObject);
    }

    jsonOut.put("page", page)
            .put("pageMax", lastPageNumber)
            .put("listOfDevices", jsonArray);

    logger.info("DevicesREST.list returns list of devices (user: " + ((User) request.getSession().getAttribute("user")).getNick() + ")");
    return ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
  }

  /**
   * WebMethod which returns a list of devices
   * <p>
   *
   * @param request Object of HttpServletRequest represents our request.
   * @param regex   Part of name or surname we want to display.
   * @return HttpStatus 200 Returns the contents of the page that contains a list of devices in the JSON format.
   */
  public ResponseEntity<String> listDevicesNames(HttpServletRequest request, String regex) {
    // authorization
    if (request.getSession().getAttribute("user") == null) {
      logger.info("DevicesREST.listDevicesNames cannot list user's (session not found)");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    } else if ((((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.ADMINISTRATOR) && (((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.MODERATOR)) {
      logger.info("DevicesREST.listDevicesNames cannot list devices's because rbac (user: " + ((User) request.getSession().getAttribute("user")).getNick() + ")");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }
    //listing
    List<Object> cars = new ArrayList<>();
    int lastPageNumber;
    Session session = hibernateRequests.getSession();
    Transaction tx = null;
    try {
      if (regex.equals("$")) regex = "";
      tx = session.beginTransaction();

      Query selectQuery = session.createQuery("SELECT c FROM Car c WHERE c.model  like '%" + regex + "%' OR c.brand  like '%" + regex + "%' OR c.licensePlate  like '%" + regex + "%'");
      cars = selectQuery.list();
      tx.commit();
      session.close();
    } catch (HibernateException e) {
      if (tx != null) tx.rollback();
      session.close();
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
    }
    JSONArray jsonArray = new JSONArray();
    for (Object tmp : cars) {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("id", ((Car) tmp).getId());
      jsonObject.put("name", ((Car) tmp).getBrand() + " " + ((Car) tmp).getModel() + " (" + ((Car) tmp).getLicensePlate() + ")");
      jsonObject.put("image", ((Car) tmp).getImage());
      jsonArray.put(jsonObject);
    }
    logger.info("DevicesREST.listDevicesNames returns list of devices (user: " + ((User) request.getSession().getAttribute("user")).getNick() + ")");
    return ResponseEntity.status(HttpStatus.OK).body(jsonArray.toString());
  }

  /**
   * WebMethod which create device with given body and save it into database
   * <p>
   *
   * @param request    Object of HttpServletRequest represents our request
   * @param httpEntity Object of httpEntity
   * @return HttpStatus 201
   */
  public ResponseEntity addDevice(HttpServletRequest request, HttpEntity<String> httpEntity) {
    // authorization
    if (request.getSession().getAttribute("user") == null) {
      logger.info("DevicesRest.addDevice cannot change device data (session not found)");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }

    Session session = null;
    Transaction tx = null;
    ResponseEntity responseEntity;
    try {
      JSONObject inJSON = new JSONObject(httpEntity.getBody());

      CarBuilder carBuilder = new CarBuilder();
      try {
        carBuilder
                .setLicensePlate(inJSON.getString("licensePlate"))
                .setBrand(inJSON.getString("brand"))
                .setModel(inJSON.getString("model"))
                .setEngine(inJSON.getString("engine"))
                .setFuelType(inJSON.getString("fuel"))
                .setProductionDate(inJSON.getInt("yearOfProduction"))
                .setFuelNorm(Double.parseDouble(inJSON.getString("norm")))
                .setTank(Integer.parseInt(inJSON.getString("tank")))
                .setPassword(DigestUtils.sha256Hex(String.valueOf(inJSON.get("password"))));
      } catch (JSONException jsonException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad body");
      }
      Car car = carBuilder.build();
      session = hibernateRequests.getSession();
      tx = session.beginTransaction();


      String getQuery1 = "SELECT s FROM Setting s WHERE s.nameOfSetting like 'sendInterval'";
      String getQuery2 = "SELECT s FROM Setting s WHERE s.nameOfSetting like 'locationInterval'";
      Query query1 = session.createQuery(getQuery1);
      Query query2 = session.createQuery(getQuery2);
      Setting set1 = (Setting) query1.getSingleResult();
      Setting set2 = (Setting) query2.getSingleResult();
      car.setSendInterval(set1.getValue());
      car.setLocationInterval(set2.getValue());
      session.save(car);
      tx.commit();
      logger.log(Level.INFO, "Device id: " + car.getId() + " licence plate: " + car.getLicensePlate() + " | successfully saved to database.");
      responseEntity = ResponseEntity.status(HttpStatus.CREATED).body("");
    } catch (HibernateException e) {
      if (tx != null) tx.rollback();//TODO
      logger.log(Level.ERROR, "Hibernate Exception: " + e);
      e.printStackTrace();
      responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
    } finally {
      if (session != null) session.close();
    }
    return responseEntity;
  }

  /**
   * WebMethod which get device with given id
   * <p>
   *
   * @param request    Object of HttpServletRequest represents our request
   * @param httpEntity Object of httpEntity
   * @param id         the id of the device
   * @return HttpStatus 200, Json String representing device data
   */
  public ResponseEntity getDeviceData(HttpServletRequest request, HttpEntity<String> httpEntity, int id) {
    // authorization
    if (request.getSession().getAttribute("user") == null) {
      logger.info("DevicesRest.getDeviceData cannot send data (session not found)");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }

    Session session = null;
    Transaction tx = null;
    ResponseEntity responseEntity;

    try {
      session = hibernateRequests.getSession();
      tx = session.beginTransaction();

      String getQuery = "SELECT c FROM Car c WHERE c.id like " + id;
      Query query = session.createQuery(getQuery);
      Car car = (Car) query.getSingleResult();
      tx.commit();
      JSONObject jsonObject = new JSONObject()
              .put("image", car.getImage())
              .put("licensePlate", car.getLicensePlate())
              .put("brand", car.getBrand())
              .put("model", car.getModel())
              .put("engine", car.getEngine())
              .put("fuel", car.getFuelType())
              .put("tank", car.getTank())
              .put("norm", car.getFuelNorm())
              .put("a", "a"); //todo


      try {
        jsonObject
                .put("timeFrom", "---")
                .put("timeTo", "---");
      } catch (NullPointerException nullPointerException) {
        jsonObject
                .put("timeFrom", "---")
                .put("timeTo", "---");
      }
      jsonObject.put("yearOfProduction", car.getProductionYear());
      responseEntity = ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    } catch (HibernateException e) {
      if (tx != null) tx.rollback();
      e.printStackTrace();
      responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
    } finally {
      if (session != null) session.close();
    }
    return responseEntity;
  }

  /**
   * WebMethod which change device data with given body
   * <p>
   *
   * @param request    Object of HttpServletRequest represents our request
   * @param httpEntity Object of httpEntity
   * @param carID      id of the device that we want to change
   * @return HttpStatus 200
   */
  public ResponseEntity changeDeviceData(HttpServletRequest request, HttpEntity<String> httpEntity, int carID) {
    // authorization
    if (request.getSession().getAttribute("user") == null) {
      logger.info("DevicesRest.changeDeviceData cannot change device data (session not found)");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }

    Session session = null;
    Transaction tx = null;
    ResponseEntity responseEntity;

    try {
      JSONObject inJSON = new JSONObject(httpEntity.getBody());
      String licensePlate;
      String brand;
      String model;
      String engine;
      String fuelType;
      int tank;
      int yearOfProduction;
      double norm;
      try {
        licensePlate = inJSON.getString("licensePlate");
        brand = inJSON.getString("brand");
        model = inJSON.getString("model");
        engine = inJSON.getString("engine");
        fuelType = inJSON.getString("fuel");
        yearOfProduction = inJSON.getInt("yearOfProduction");
        tank = Integer.parseInt(inJSON.getString("tank"));
        norm = Double.parseDouble(inJSON.getString("norm"));
      } catch (JSONException jsonException) {
        responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        return responseEntity;
      }

      session = hibernateRequests.getSession();
      tx = session.beginTransaction();

      String getQuery = "SELECT c FROM Car c WHERE c.id like " + carID;
      Query query = session.createQuery(getQuery);
      Car car = (Car) query.getSingleResult();
      car.setLicensePlate(licensePlate);
      car.setBrand(brand);
      car.setModel(model);
      car.setEngine(engine);
      car.setFuelType(fuelType);
      car.setTank(tank);
      car.setFuelNorm(norm);
      car.setProductionYear(yearOfProduction);
      session.update(car);
      tx.commit();
      responseEntity = ResponseEntity.status(HttpStatus.OK).body("");
      logger.log(Level.INFO, "Car data changed (car id=" + carID + ") data changed to " +
              "(licensePlate=" + licensePlate +
              " brand=" + brand + " model=" + model + " engine=" + engine +
              "fuelType=" + fuelType + "tank=" + tank + "norm=" + norm + ")");
    } catch (HibernateException e) {
      if (tx != null) tx.rollback();
      e.printStackTrace();
      responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
    } finally {
      if (session != null) session.close();
    }

    return responseEntity;
  }

  /**
   * WebMethod which change device image
   * <p>
   *
   * @param request    Object of HttpServletRequest represents our request
   * @param httpEntity Object of httpEntity
   * @param carID      id of the device that we want to change
   * @return HttpStatus 200
   */
  public ResponseEntity changeDeviceImage(HttpServletRequest request, HttpEntity<String> httpEntity, int carID) {
    // authorization
    if (request.getSession().getAttribute("user") == null) {
      logger.info("DevicesRest.changeDeviceImage cannot change device image (session not found)");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }

    Session session = null;
    Transaction tx = null;
    ResponseEntity responseEntity;

    try {
      JSONObject inJSON = new JSONObject(httpEntity.getBody());
      if (!inJSON.has("image")) {
        responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        return responseEntity;
      }
      String image = inJSON.getString("image");
      session = hibernateRequests.getSession();
      tx = session.beginTransaction();
      String getQuery = "SELECT c FROM Car c WHERE c.id like " + carID;
      Query query = session.createQuery(getQuery);
      Car car = (Car) query.getSingleResult();
      car.setImage(image);
      session.update(car);
      tx.commit();
      responseEntity = ResponseEntity.status(HttpStatus.OK).body("");
    } catch (HibernateException e) {
      if (tx != null) tx.rollback();
      e.printStackTrace();
      responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
    } finally {
      if (session != null) session.close();
    }

    return responseEntity;
  }

  public Optional<Car> removeDevice(int id) {
    return carDaoJdbc.delete(id);
  }
}

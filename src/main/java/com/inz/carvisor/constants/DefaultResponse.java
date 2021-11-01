package com.inz.carvisor.constants;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class DefaultResponse {

  public static final ResponseEntity<String> UNAUTHORIZED = ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .body(error("Unauthorized"));

  public static final ResponseEntity<String> UNAUTHORIZED_JSON = ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .body(error("Unauthorized"));

  public static final ResponseEntity<String> BAD_REQUEST = ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(error("bad request"));

  public static final ResponseEntity<String> OK = ResponseEntity
          .status(HttpStatus.OK)
          .body("everything went great");

  public static final ResponseEntity<String> EMPTY_BODY = ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(error("empty body"));


  public static ResponseEntity<String> badBody(String responseBody) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
  }

  public static ResponseEntity<String> ok(String responseBody) {
    return ResponseEntity.status(HttpStatus.OK).body(responseBody);
  }

  public static String error(String msg) {
    return new JSONObject().put("errorMessage:", msg).toString();
  }

}

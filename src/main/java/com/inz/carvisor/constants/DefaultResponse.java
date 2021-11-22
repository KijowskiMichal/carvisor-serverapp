package com.inz.carvisor.constants;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class DefaultResponse {

    public static final ResponseEntity<String> UNAUTHORIZED = ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(jsonMessage("Unauthorized"));

    public static final ResponseEntity<String> BAD_REQUEST = ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(jsonMessage("bad request"));

    public static final ResponseEntity<String> OK = ResponseEntity
            .status(HttpStatus.OK)
            .body(jsonMessage("everything went great"));

    public static final ResponseEntity<String> EMPTY_BODY = ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(jsonMessage("empty body"));

    public static ResponseEntity<String> custom(HttpStatus httpStatus, String message) {
        return ResponseEntity.status(httpStatus).body(jsonMessage(message));
    }

    public static ResponseEntity<String> badRequest(String responseBody) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonMessage(responseBody));
    }

    public static ResponseEntity<String> badRequestCantFindUer(Number userId) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(jsonMessage("Can't find user witch id=" + userId));
    }

    public static ResponseEntity<String> ok(String responseBody) {
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    public static String jsonMessage(String msg) {
        return new JSONObject().put("message:", msg).toString();
    }

}

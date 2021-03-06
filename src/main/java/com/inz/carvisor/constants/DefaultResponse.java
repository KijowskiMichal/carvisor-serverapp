package com.inz.carvisor.constants;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;

public class DefaultResponse {

    public static final ResponseEntity<String> UNAUTHORIZED = ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(jsonMessage("Unauthorized"));

    public static final ResponseEntity<String> BAD_REQUEST = ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(jsonMessage("bad request"));

    public static final ResponseEntity<String> OK = ResponseEntity
            .status(HttpStatus.OK)
            .body(jsonMessage("ok"));

    public static final ResponseEntity<String> EMPTY_BODY = ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(jsonMessage("empty body"));

    public static final ResponseEntity<String> NOT_ACCEPTABLE = ResponseEntity
            .status(HttpStatus.NOT_ACCEPTABLE)
            .body(jsonMessage("not acceptable"));

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

    public static ResponseEntity<byte[]> okByte(byte[] responseBody) {
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    public static ResponseEntity<byte[]> unauthorizedBytes() {
        return ResponseEntity.status(HttpStatus.OK).body("Unauthorized".getBytes(StandardCharsets.UTF_8));
    }

    public static ResponseEntity<byte[]> badRequestBytes() {
        return ResponseEntity.status(HttpStatus.OK).body("Bad Request".getBytes(StandardCharsets.UTF_8));
    }

    private static String jsonMessage(String msg) {
        return new JSONObject().put("message:", msg).toString();
    }
}

package constants;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class DefaultResponses {

    public static final ResponseEntity<String> UNAUTHORIZED = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized");
    public static final ResponseEntity<String> BAD_REQUEST = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("bad request");
    public static final ResponseEntity<String> OK = ResponseEntity.status(HttpStatus.OK).body("everything went great");
}

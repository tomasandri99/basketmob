package is.hi.basketmob.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Minimal redirect so folks can type /swagger without exposing any HTML templates.
 */
@RestController
public class RootController {

    @GetMapping("/swagger")
    public ResponseEntity<Void> swagger() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "/swagger-ui/index.html")
                .build();
    }
}

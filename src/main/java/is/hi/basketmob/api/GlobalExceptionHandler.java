package is.hi.basketmob.api;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleRse(ResponseStatusException ex){
        HttpStatus s = ex.getStatus();
        String code = s == HttpStatus.BAD_REQUEST ? "BAD_REQUEST" :
                s == HttpStatus.NOT_FOUND  ? "NOT_FOUND"  :
                        s == HttpStatus.FORBIDDEN  ? "FORBIDDEN"  : s.name();
        return ResponseEntity.status(s).body(ApiError.of(code, ex.getReason()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex){
        String msg = ex.getBindingResult().getAllErrors().stream()
                .findFirst().map(e -> e.getDefaultMessage()).orElse("Validation failed");
        return ResponseEntity.badRequest().body(ApiError.of("VALIDATION_ERROR", msg));
    }
}

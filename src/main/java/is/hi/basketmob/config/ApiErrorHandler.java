package is.hi.basketmob.config;

import is.hi.basketmob.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice
public class ApiErrorHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> notFound(EntityNotFoundException ex) {
        String code = ex.getMessage() != null ? ex.getMessage() : "NOT_FOUND";
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDto(code, "Resource not found", null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> badRequest(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorDto("VALIDATION_ERROR", "Invalid request", ex.getMessage()));
    }
}

package io.github.dscatalog.resources.exceptions;

import io.github.dscatalog.services.exceptions.DataBaseException;
import io.github.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@ControllerAdvice
public class DataBaseExceptionHandler {

    @ExceptionHandler(DataBaseException.class)
    public ResponseEntity<StandardError> databaseIntegrityViolation(DataBaseException e, HttpServletRequest request) {
        HttpStatus statusBadRequest = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError();
        err.setTimestamp(Instant.now());
        err.setStatus(statusBadRequest.value());
        err.setError("Database exception!");
        err.setMessage(e.getMessage());
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(statusBadRequest).body(err);
    }

}

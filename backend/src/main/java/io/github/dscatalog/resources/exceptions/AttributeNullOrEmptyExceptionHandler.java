package io.github.dscatalog.resources.exceptions;

import io.github.dscatalog.services.exceptions.AttributeNullOrEmptyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@ControllerAdvice
public class AttributeNullOrEmptyExceptionHandler {

    @ExceptionHandler(AttributeNullOrEmptyException.class)
    public ResponseEntity<StandardError> attributeNullOrEmptyException(AttributeNullOrEmptyException e, HttpServletRequest request) {
        StandardError err = StandardError
                .builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Attributes can't be empty or null!")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

}

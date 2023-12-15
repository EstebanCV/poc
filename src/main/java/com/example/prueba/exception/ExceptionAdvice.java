package com.example.prueba.exception;

import com.example.prueba.dto.Response;
import jakarta.validation.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ExceptionAdvice {
    private static final Logger LOG = LogManager.getLogger(ExceptionAdvice.class);

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exceptionHandler(Exception ex) {
        LOG.error("Error interno, {}", ex.getMessage());
        Response<String> res = new Response(ex.getMessage(), null);

        if(null != ex.getCause() && ex.getCause().toString().contains("ValidationException"))
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        else
            return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> validationHandler(Exception ex) {
        LOG.error("Error de validacion, {}", ex.getMessage());
        Response<String> res = new Response(ex.getMessage(), null);

        if(null != ex.getMessage() && ex.getMessage().equals("Token sin usuario asociado"))
            return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
        else
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) ->
            errors.add(error.getDefaultMessage())
        );

        LOG.error("Error de validacion, {}", errors);
        Response<String> res = new Response(errors.get(0), null);

        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleValidationCredentials(BadCredentialsException ex) {
        LOG.error("Error de validacion credenciales, {}", ex.getMessage());

        Response<String> res = new Response("credenciales incorrectas", null);

        return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
    }
}
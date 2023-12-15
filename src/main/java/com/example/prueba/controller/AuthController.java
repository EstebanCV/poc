package com.example.prueba.controller;

import com.example.prueba.dto.AuthRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(path = "/auth")
public interface AuthController {

    @PostMapping(path = "/token", consumes="application/json", produces="application/json")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<?> getToken(@Valid @RequestBody AuthRequest request);

    /*@PostMapping(path = "/logout", produces="application/json")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<?> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String headerToken);*/
}
package com.example.prueba.controller;

import com.example.prueba.dto.Contacts;
import com.example.prueba.dto.Users;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping(path = "/demo/users")
public interface UserController {

    @PostMapping(path = "/registro", consumes="application/json", produces="application/json")
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<?> createUser(@Valid @RequestBody Users users);

    @GetMapping(path="/{id}",produces="application/json")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<?> getUser(@PathVariable("id") String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String headerToken);

    @DeleteMapping(path="/{id}", produces="application/json")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<?> deleteUser(@PathVariable("id") String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String headerToken);

    @PutMapping(path="/{id}", consumes="application/json", produces="application/json")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<?> updateUser(@Valid @RequestBody Users users, @PathVariable("id") String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String headerToken);

    @PostMapping(path="/{id}/contacts", consumes="application/json", produces="application/json")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<?> addContactUser(@Valid @RequestBody Contacts contact, @PathVariable("id") String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String headerToken);

    @GetMapping(path="/{id}/contacts",produces="application/json")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<?> findContactUser(@PathVariable("id") String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String headerToken);
}

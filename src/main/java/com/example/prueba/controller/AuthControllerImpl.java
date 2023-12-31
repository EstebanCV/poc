package com.example.prueba.controller;

import com.example.prueba.dto.AuthRequest;
import com.example.prueba.dto.AuthResponse;
import com.example.prueba.dto.Response;
import com.example.prueba.dto.Users;
import com.example.prueba.helper.Utils;
import com.example.prueba.repository.UsersRepository;
import com.example.prueba.security.JwtTokenConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthControllerImpl implements AuthController {
    private static final Logger LOG = LoggerFactory.getLogger(AuthControllerImpl.class);

    private final JwtTokenConfig jwtTokenConfig;
    private final AuthenticationManager authenticationManager;
    private final UsersRepository userRepo;
    private final Utils util;

    @Autowired
    public AuthControllerImpl(JwtTokenConfig jwtTokenConfig, AuthenticationManager authenticationManager, UsersRepository userRepo, Utils util){
        this.jwtTokenConfig = jwtTokenConfig;
        this.authenticationManager = authenticationManager;
        this.userRepo = userRepo;
        this.util = util;
    }

    @Override
    public ResponseEntity<?> getToken(AuthRequest request) {
        LOG.info("Inicio generacion token");
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        Users u = (Users) authentication.getPrincipal();
        LOG.info("Generando token!!");
        String token = jwtTokenConfig.generateAccessToken(u);
        userRepo.updateToken(token, util.getDate(), u.getId());

        LOG.info("Fin generacion token");
        return ResponseEntity.ok().body(new Response<>("Token exitoso", new AuthResponse(u.getEmail(), token)));
    }
}

package com.example.prueba.controller;

import com.example.prueba.dto.Contacts;
import com.example.prueba.dto.Response;
import com.example.prueba.dto.Users;
import com.example.prueba.helper.Utils;
import com.example.prueba.repository.ContactsRepository;
import com.example.prueba.repository.UsersRepository;
import com.example.prueba.security.JwtTokenConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class UserControllerImpl implements UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserControllerImpl.class);

    private final UsersRepository userRepo;
    private final ContactsRepository contactRepo;
    private final Utils util;

    private final JwtTokenConfig jwtTokenConfig;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserControllerImpl(UsersRepository userRepo, ContactsRepository contactRepo, Utils util, JwtTokenConfig jwtTokenConfig, AuthenticationManager authenticationManager){
        this.userRepo = userRepo;
        this.contactRepo = contactRepo;
        this.util = util;
        this.jwtTokenConfig = jwtTokenConfig;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public ResponseEntity<?> createUser(Users users) {
        LOG.info("Inicio createUser");

        LOG.info("validando email usuario");
        util.validateEmail(userRepo.validateEmailExist(users.getEmail()), null);

        LOG.info("validando contactos duplicados");
        util.validateContact(users.getContacts(), null, contactRepo, null);

        LOG.info("mapeo e insercion de usuarios");
        util.completeCreateUser(users);
        Users u = userRepo.save(users);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    users.getEmail(),
                    users.getPassword()
                )
        );

        Users sessionUsers = (Users) authentication.getPrincipal();
        LOG.info("Generando token!!");
        String token = jwtTokenConfig.generateAccessToken(sessionUsers);
        u.setToken(token);
        u.setActive("Y");
        userRepo.updateToken(token, util.getDate(), sessionUsers.getId());

        LOG.info("Fin createUser");
        return ResponseEntity.ok().body(new Response<>("Ejecucion exitosa", u));
    }

    @Override
    public ResponseEntity<?> getUser(String id, String headerToken) {
        LOG.info("Inicio getUser");
        Optional<Users> user = userRepo.findById(util.validateID(id));

        if(user.isPresent()) {
            LOG.info("validando token!!");
            util.validateToken(user.get().getToken(), headerToken);
            LOG.info("Fin getUser");
            return ResponseEntity.ok().body(new Response<>("Ejecucion exitosa", user.get()));
        } else {
            return new ResponseEntity<>(new Response<>("Usuario no existe", null), HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> deleteUser(String id, String headerToken) {
        LOG.info("Inicio deleteUser");
        Long idTmp = util.validateID(id);
        Optional<Users> user = userRepo.findById(idTmp);

        if(user.isPresent()) {
            LOG.info("validando token!!");
            util.validateToken(user.get().getToken(), headerToken);
            LOG.info("eliminando user");
            userRepo.deleteById(idTmp);
            LOG.info("Fin deleteUser");
            return ResponseEntity.ok().body(new Response<>("Ejecucion exitosa", null));
        } else {
            return new ResponseEntity<>(new Response<>("Usuario no existe", null), HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> updateUser(Users users, String id, String headerToken) {
        LOG.info("Inicio updateUser");
        Long idTmp = util.validateID(id);
        Optional<Users> user = userRepo.findById(idTmp);

        if(user.isPresent()) {
            LOG.info("validando token!!");
            util.validateToken(user.get().getToken(), headerToken);
            LOG.info("validando contactos duplicados");
            util.validateContact(users.getContacts(), null, contactRepo, id);
            LOG.info("validando email usuario");
            util.validateEmail(userRepo.validateEmailExist(users.getEmail()), id);
            LOG.info("eliminando numeros registrados");
            contactRepo.deleteByIdUser(idTmp);
            util.completeUpdateUser(user.get(), users);
            LOG.info("actualizando usuarios");
            userRepo.save(user.get());
            LOG.info("Fin updateUser");
            return ResponseEntity.ok().body(new Response<>("Ejecucion exitosa", null));
        } else {
            return new ResponseEntity<>(new Response<>("Usuario no existe", null), HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> addContactUser(Contacts contact, String id, String headerToken) {
        LOG.info("Inicio addContactUser");
        Long idTmp = util.validateID(id);
        Optional<Users> user = userRepo.findById(idTmp);

        if(user.isPresent()) {
            LOG.info("validando token!!");
            util.validateToken(user.get().getToken(), headerToken);

            LOG.info("validando contactos duplicados");
            util.validateContact(user.get().getContacts(), contact, contactRepo, null);

            user.get().setModified(util.getDate());
            user.get().setLastLogin(util.getDate());

            LOG.info("actualizando user");
            userRepo.save(user.get());

            LOG.info("guardando contacto");
            contact.setIdUser(idTmp);
            contactRepo.save(contact);

            LOG.info("Fin addContactUser");
            return ResponseEntity.ok().body(new Response<>("Ejecucion exitosa", null));
        } else {
            return new ResponseEntity<>(new Response<>("Usuario no existe", null), HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> findContactUser(String id, String headerToken) {
        LOG.info("Inicio findContactUser");
        Optional<Users> user = userRepo.findById(util.validateID(id));

        if(user.isPresent()) {
            LOG.info("validando token!!");
            util.validateToken(user.get().getToken(), headerToken);
            LOG.info("Fin findContactUser");
            return ResponseEntity.ok().body(new Response<>("Ejecucion exitosa", user.get().getContacts()));
        } else {
            return new ResponseEntity<>(new Response<>("Usuario no existe", null), HttpStatus.NOT_FOUND);
        }
    }
}

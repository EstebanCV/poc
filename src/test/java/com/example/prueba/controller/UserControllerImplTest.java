package com.example.prueba.controller;

import com.example.prueba.dto.Response;
import com.example.prueba.dto.Users;
import com.example.prueba.helper.Utils;
import com.example.prueba.repository.UsersRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//extension para evitar utilizar mockito.open mocks 'busca instancias de mocks para inicializarlas'
@ExtendWith(MockitoExtension.class)
class UserControllerImplTest {

    @Mock
    Utils utils;
    @Mock
    UsersRepository usersRepository;
    @InjectMocks
    UserControllerImpl userController;

    @Test
    @DisplayName("Get User OK")
    void getUserOK() {
        Users user = new Users(1L, "prueba@gmail.com", "Prueba12");
        user.setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4Y2ZkMzZ");

        Mockito.when(usersRepository.findById(any())).thenReturn(Optional.of(user));
        doCallRealMethod().when(utils).validateToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4Y2ZkMzZ", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4Y2ZkMzZ");

        ResponseEntity<Response<Users>> respuesta = (ResponseEntity<Response<Users>>) userController.getUser("1", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4Y2ZkMzZ");

        assertAll(
            () -> assertEquals(HttpStatus.OK, respuesta.getStatusCode(), () -> "fallo status code peticion"),
            () -> assertEquals("prueba@gmail.com", respuesta.getBody().getData().getEmail(), () -> "fallo en email de respuesta")
        );
    }

    @Test
    @DisplayName("Get User No Existe")
    void getUserNoExiste() {
        Mockito.when(usersRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<Response<Users>> respuesta = (ResponseEntity<Response<Users>>) userController.getUser("1", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4Y2ZkMzZ");

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode(), () -> "fallo status code peticion"),
            () -> assertEquals("Usuario no existe", respuesta.getBody().getMessage(), () -> "fallo en mensaje user no existe")
        );
    }

    @Test
    @DisplayName("Get User Err Utils Validate ID")
    void getUserErrValidateID() {
        doCallRealMethod().when(utils).validateID("aa");

        Exception ex = assertThrows(ValidationException.class, () -> {
            utils.validateID("aa");
        });

        assertEquals("ID ingresado incorrecto", ex.getMessage());
        assertEquals(ValidationException.class, ex.getClass());
    }

    @Test
    @DisplayName("Get User OK Utils Validate ID")
    void getUserOKValidateID() {
        doCallRealMethod().when(utils).validateID("2");

        Long dato = utils.validateID("2");

        assertEquals("2", dato.toString());
    }

    @Test
    @DisplayName("Get User Err Utils Validate token")
    void getUserErrValidateToken() {
        doCallRealMethod().when(utils).validateToken("eyJhbGciOiJIUzI1NiJ9","Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4Y2ZkMzZ");

        Exception ex = assertThrows(ValidationException.class, () -> {
            utils.validateToken("eyJhbGciOiJIUzI1NiJ9","Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4Y2ZkMzZ");
        });

        assertEquals("Token sin usuario asociado", ex.getMessage());
        assertEquals(ValidationException.class, ex.getClass());
    }

    @Test
    @DisplayName("Get User OK Utils Validate token")
    void getUserOKValidateToken() {
        doCallRealMethod().when(utils).validateToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4Y2ZkMzZ","Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4Y2ZkMzZ");

        utils.validateToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4Y2ZkMzZ","Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4Y2ZkMzZ");
        utils.validateToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4Y2ZkMzZ","Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4Y2ZkMzZ");

        verify(utils, times(2)).validateToken(any(), any());
    }
}
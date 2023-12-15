package com.example.prueba.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class AuthRequest {
    @Email(message = "Email incorrecto", regexp = "^[\\w-\\.]+@([a-z]+\\.)+[a-z]{2,4}$", flags = Pattern.Flag.CASE_INSENSITIVE)
    @NotEmpty(message = "Email no puede estar vacio")
    private String email;

    @Pattern(message = "Clave debe contener una mayuscula, letras minusculas y 2 numeros", regexp = "^(?=^[^A-Z]*[A-Z][^A-Z]*$)(?=^(?:\\D*\\d\\D*){2}$)[A-Za-z\\d]*$")
    @NotEmpty(message = "Clave no puede estar vacio")
    private String password;

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
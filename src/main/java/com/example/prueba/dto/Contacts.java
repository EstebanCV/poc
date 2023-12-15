package com.example.prueba.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "contact")
public class Contacts {
    @Id
    //incremento automatico mediante la bd
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotEmpty(message = "Numero de telefono es requerido")
    @Pattern(message = "Numero de telefono solo acepta numeros", regexp = "^\\d+$")
    @Column(name = "number_user")
    private String number;

    @NotEmpty(message = "Codigo de ciudad es requerido")
    @Pattern(message = "Codigo de ciudad solo acepta numeros", regexp = "^\\d+$")
    @Column(name = "city_code")
    private String cityCode;
    @NotEmpty(message = "Codigo de pais es requerido")
    @Pattern(message = "Codigo de ciudad solo acepta numeros", regexp = "^\\d+$")
    @Column(name = "country_code")
    private String countryCode;

    @JsonIgnore
    @Column(name = "id_user")
    private Long idUser;

    public Contacts() {}

    public Contacts(String number, String cityCode, String countryCode) {
        this.number = number;
        this.cityCode = cityCode;
        this.countryCode = countryCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }
}

package com.example.prueba.helper;

import com.example.prueba.dto.Contacts;
import com.example.prueba.dto.Users;
import com.example.prueba.repository.ContactsRepository;
import com.example.prueba.repository.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class Utils {

    //BCrypt es utilizado por spring security para validar, codificar y decodificar pass
    public String encodePassword(String pass){
        BCryptPasswordEncoder passEncode = new BCryptPasswordEncoder();
        return passEncode.encode(pass);
    }

    public Long validateID(String id){
        try {
            return Long.valueOf(id);
        } catch (Exception ex){
            throw new ValidationException("ID ingresado incorrecto");
        }
    }

    public String getDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new Date());
    }

    public void completeCreateUser(Users users){
        users.setCreated(getDate());
        users.setModified(getDate());
        users.setLastLogin(getDate());
        users.setToken("");
    }

    public void completeUpdateUser(Users userDB, Users u){
        userDB.setName(u.getName());
        userDB.setEmail(u.getEmail());
        userDB.setPassword(u.getPassword());
        userDB.setModified(getDate());

        List<Contacts> tmp = new ArrayList<>();
        u.getContacts().forEach((c) -> {
            tmp.add(new Contacts(c.getNumber(), c.getCityCode(), c.getCountryCode()));
        });
        userDB.getContacts().addAll(tmp);
    }

    public void validateEmail(String idDB, String id) {
        if(null != idDB && !idDB.isEmpty() && (null == id || !idDB.equals(id)))
            throw new ValidationException("Email ya registrado");
    }

    public void validateToken(String token, String headerToken) {
        if(null == token || token.isEmpty() || !token.equals(headerToken.substring(7)))
            throw new ValidationException("Token sin usuario asociado");
    }

    public void validateContact(List<Contacts> contacts, Contacts newContact, ContactsRepository contactRepo, String idReq) {
        Collection<String> data = new ArrayList<>();
        contacts.forEach(x -> data.add(x.getNumber()));

        //evalua si al agregar contacto ya se encuentra ingresado
        if(null != newContact && data.stream().filter(x -> newContact.getNumber().equals(x)).count() > 0)
            throw new ValidationException("Telefono ingresado ya se encuentra asociado a tu usuario");

        //filtra la lista existente para saber q no hay duplicados
        if(data.stream().distinct().toList().size() != contacts.size())
            throw new ValidationException("Telefonos duplicados en el mismo registro");

        //crea nueva lista para validar el contacto nuevo
        if(null != newContact){
            data.clear();
            data.add(newContact.getNumber());
        }

        //trae lista de uuid q tienen asociado el numero del contacto ingresado
        List<String> uuid = contactRepo.validateContactExist(data);

        //valida PUT - verifica q contactos no esten asociado a otros id
        if (uuid.size() > 0 && null != idReq && uuid.stream().filter(x -> !x.equals(idReq)).count() > 0)
            throw new ValidationException("Telefono se encuentra registrado a otro usuario");

        //valida resto de metodos, si la lista tiene datos el contacto ya esta registrado
        if(uuid.size() > 0 && null == idReq)
            throw new ValidationException("Telefono se encuentra registrado a otro usuario");
    }

    //Valida expiracion del token cada vez q se realiza una peticion
    public void validateTokenExpired(HttpServletRequest request, UsersRepository userRepo) {
        if(null != request.getHeader("authorization") && !request.getHeader("authorization").isEmpty()){
            Optional<Users> users = userRepo.findByToken(
                request.getHeader("authorization").substring(7)
            );

            if(users.isPresent() && !users.get().getToken().isEmpty())
                userRepo.updateTokenExpired(users.get().getId(), getDate());
        }
    }
}
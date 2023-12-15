package com.example.prueba.repository;

import com.example.prueba.dto.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    @Query("SELECT u.id FROM Users u WHERE u.email = ?1")
    String validateEmailExist(String email);

    @Transactional //para confirmar o revertir la transaccion en casode error
    @Modifying  //para manejar insert, update, delete
    @Query("UPDATE Users u SET u.token = ?1, u.active = 'Y', u.lastLogin = ?2 WHERE u.id = ?3")
    void updateToken(String token, String fecha, Long id);

    @Transactional //para confirmar o revertir la transaccion en casode error
    @Modifying  //para manejar insert, update, delete
    @Query("UPDATE Users u SET u.token = '', u.active = 'N', u.modified = ?2 WHERE u.id = ?1")
    void updateTokenExpired(Long id, String fecha);

    Optional<Users> findByToken(String token);
}

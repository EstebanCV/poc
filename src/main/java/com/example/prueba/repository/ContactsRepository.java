package com.example.prueba.repository;

import com.example.prueba.dto.Contacts;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ContactsRepository extends JpaRepository<Contacts, Long> {

    @Transactional
    @Modifying
    void deleteByIdUser(Long id);

    @Query("SELECT c.idUser FROM Contacts c WHERE c.number IN :contacts")
    List<String> validateContactExist(@Param("contacts") Collection<String> contacts);
}

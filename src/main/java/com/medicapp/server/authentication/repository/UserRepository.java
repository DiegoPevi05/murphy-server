package com.medicapp.server.authentication.repository;

import com.medicapp.server.authentication.model.Role;
import com.medicapp.server.authentication.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.enabled = false")
    Optional<User> findByEmailDisable(String email);
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.enabled = true")
    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.recoverCode = :recoverCode AND u.enabled = true")
    Optional<User> findByRecoverCodeAndEmail(String recoverCode,String email);


}

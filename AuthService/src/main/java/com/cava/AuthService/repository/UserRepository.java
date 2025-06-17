package com.cava.AuthService.repository;

import com.cava.AuthService.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<Users, UUID> {
    Users findByEmail(String email);
}

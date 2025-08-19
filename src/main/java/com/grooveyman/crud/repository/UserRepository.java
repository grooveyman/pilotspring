package com.grooveyman.crud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grooveyman.crud.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // Optional<User> findByEmail(String email);
}

package com.blitz.springboot4.dao;

import com.blitz.springboot4.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NewUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByName(String name);
}


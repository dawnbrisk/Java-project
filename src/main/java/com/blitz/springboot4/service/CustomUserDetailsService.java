package com.blitz.springboot4.service;

import com.blitz.springboot4.entity.UserEntity;
import com.blitz.springboot4.dao.NewUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private NewUserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserEntity user = userRepo.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("the user is not found"));
        return new org.springframework.security.core.userdetails.User(
                user.getName(), user.getPassword(), List.of());
    }
}


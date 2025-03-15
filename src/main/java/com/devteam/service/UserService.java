package com.devteam.service;

import com.devteam.entity.User;

public interface UserService {
    User findByUsername(String username);
    User findByEmail(String email);
    User registerNewUser(User user);
} 
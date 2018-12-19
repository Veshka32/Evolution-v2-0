package com.services;

import com.model.UserDTO;

import java.util.Optional;

public interface UserServiceI {
    Optional<String> createUser(UserDTO dto);
}

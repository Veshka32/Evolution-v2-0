package com.services.implementations;

import com.helpers.Role;
import com.model.User;
import com.model.UserDTO;
import com.repositories.UserRepository;
import com.services.UserServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@EnableTransactionManagement
@Transactional
public class UserService implements UserServiceI {

    @Autowired
    UserRepository repository;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public Optional<String> createUser(UserDTO dto) {

        //check if user with such a login already exists
        User user = repository.findByLogin(dto.getLogin());
        if (user != null) return Optional.of("This login is reserved");

        //check email
        user = repository.findByEmail(dto.getEmail());
        if (user != null) return Optional.of("This email is reserved");

        user = new User();
        user.setEmail(dto.getEmail());
        user.setLogin(dto.getLogin());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.addRole(Role.ROLE_USER);
        repository.save(user);
        return Optional.empty();
    }
}

package com.agilewildcats.chattyCatty.service;

import com.agilewildcats.chattyCatty.model.Role;
import com.agilewildcats.chattyCatty.model.User;
import com.agilewildcats.chattyCatty.repo.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String username, String rawPassword, boolean asAdmin) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("username exists");
        }
        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        var roles = new HashSet<Role>();
        roles.add(Role.ROLE_USER);
        if (asAdmin) roles.add(Role.ROLE_ADMIN);
        u.setRoles(roles);
        return userRepository.save(u);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }

    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean deleteUser(String username) {
        var u = userRepository.findByUsername(username);
        if (u.isPresent()) {
            userRepository.delete(u.get());
            return true;
        }
        return false;
    }
}

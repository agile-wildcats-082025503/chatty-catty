package com.agilewildcats.chattyCatty.controller;

import com.agilewildcats.chattyCatty.model.Role;
import com.agilewildcats.chattyCatty.model.User;
import com.agilewildcats.chattyCatty.security.JwtUtil;
import com.agilewildcats.chattyCatty.service.UserService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            User u = userService.register(req.username, req.password, req.admin == Boolean.TRUE);
            return ResponseEntity.ok(Map.of("username", u.getUsername()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        User u = userService.findByUsername(req.username);
        if (u == null || !userService.checkPassword(u, req.password)) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid credentials"));
        }
        String token = jwtUtil.generateToken(u.getUsername(), u.getRoles());
        return ResponseEntity.ok(Map.of("token", token, "username", u.getUsername()));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/promote/{username}")
    public ResponseEntity<?> promote(@PathVariable String username) {
        User u = userService.findByUsername(username);
        if (u == null) return ResponseEntity.notFound().build();
        u.getRoles().add(Role.ROLE_ADMIN);
        userService.updateUser(u);
        return ResponseEntity.ok(Map.of("username", u.getUsername(), "roles", u.getRoles()));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/demote/{username}")
    public ResponseEntity<?> demote(@PathVariable String username) {
        User u = userService.findByUsername(username);
        if (u == null) return ResponseEntity.notFound().build();
        u.getRoles().remove(Role.ROLE_ADMIN);
        userService.updateUser(u);
        return ResponseEntity.ok(Map.of("username", u.getUsername(), "roles", u.getRoles()));
    }

    @Data
    public static class RegisterRequest {
        public String username;
        public String password;
        public Boolean admin;
    }

    @Data
    public static class LoginRequest {
        public String username;
        public String password;
    }

    // List all users (admin-only)
    @GetMapping("/users")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> listUsers() {
        return ResponseEntity.ok(userService.getAllUsers()
            .stream()
            .map(u -> Map.of("username", u.getUsername(), "roles", u.getRoles()))
            .toList());
    }

    @PostMapping("/delete/{username}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        boolean deleted = userService.deleteUser(username);
        if (deleted) {
            return ResponseEntity.ok(Map.of("deleted", username));
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "user not found"));
        }
    }
}


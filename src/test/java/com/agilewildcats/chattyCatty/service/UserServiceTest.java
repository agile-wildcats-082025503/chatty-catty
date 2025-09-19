package com.agilewildcats.chattyCatty.service;

import com.agilewildcats.chattyCatty.model.Role;
import com.agilewildcats.chattyCatty.model.User;
import com.agilewildcats.chattyCatty.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(UserService.class)
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterUser() {
        User u = userService.register("alice", "password", false);
        assertThat(u.getId()).isNotNull();
        assertThat(u.getRoles()).contains(Role.ROLE_USER);
        assertThat(u.getRoles()).doesNotContain(Role.ROLE_ADMIN);
        assertThat(userService.checkPassword(u, "password")).isTrue();
    }

    @Test
    void testRegisterDuplicateThrows() {
        userService.register("bob", "pw", false);
        assertThrows(IllegalArgumentException.class, () ->
                userService.register("bob", "pw2", false));
    }

    @Test
    void testRegisterAdmin() {
        User u = userService.register("admin", "pw", true);
        assertThat(u.getRoles()).contains(Role.ROLE_ADMIN);
    }

    @Test
    void testFindByUsername() {
        userService.register("carol", "pw", false);
        User u = userService.findByUsername("carol");
        assertThat(u).isNotNull();
        assertThat(u.getUsername()).isEqualTo("carol");
    }

    @Test
    void testDeleteUser() {
        userService.register("dave", "pw", false);
        boolean deleted = userService.deleteUser("dave");
        assertThat(deleted).isTrue();
        assertThat(userService.findByUsername("dave")).isNull();
    }

    @Test
    void testGetAllUsers() {
        userService.register("eve", "pw", false);
        userService.register("frank", "pw", false);
        assertThat(userService.getAllUsers()).hasSize(2);
    }
}

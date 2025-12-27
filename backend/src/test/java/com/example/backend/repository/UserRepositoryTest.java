package com.example.backend.repository;

import com.example.backend.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByUsername_true_whenUserExists() {
        User user = new User("alice", "hashed_pw");
        userRepository.save(user);

        boolean exists = userRepository.existsByUsername("alice");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByUsername_false_whenUserDoesNotExist() {
        boolean exists = userRepository.existsByUsername("bob");

        assertThat(exists).isFalse();
    }

    @Test
    void findByUsername_returnsUser_whenExists() {
        User user = new User("charlie", "hashed_pw");
        userRepository.save(user);

        User found = userRepository.findByUsername("charlie").orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("charlie");
    }

    @Test
    void findByUsername_empty_whenNotExists() {
        assertThat(userRepository.findByUsername("nobody")).isEmpty();
    }
}

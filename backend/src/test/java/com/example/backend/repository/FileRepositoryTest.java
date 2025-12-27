package com.example.backend.repository;

import com.example.backend.model.entity.File;
import com.example.backend.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FileRepositoryTest {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByExternalId_returnsFile() {
        File file = new File();
        UUID externalId = UUID.randomUUID();
        file.setExternalId(externalId);

        fileRepository.save(file);

        File found = fileRepository.findByExternalId(externalId);

        assertThat(found).isNotNull();
        assertThat(found.getExternalId()).isEqualTo(externalId);
    }

    @Test
    void findAllByUser_returnsOnlyUsersFiles() {
        User user = new User("user1", "pw");
        userRepository.save(user);

        File f1 = new File();
        f1.setUser(user);

        File f2 = new File();
        f2.setUser(user);

        File other = new File();

        fileRepository.saveAll(List.of(f1, f2, other));

        List<File> results = fileRepository.findAllByUser(user);

        assertThat(results).hasSize(2);
        assertThat(results).allMatch(f -> f.getUser().equals(user));
    }

    @Test
    void findAllByUser_empty_whenNoFiles() {
        User user = new User("empty", "pw");
        userRepository.save(user);

        List<File> results = fileRepository.findAllByUser(user);

        assertThat(results).isEmpty();
    }
}

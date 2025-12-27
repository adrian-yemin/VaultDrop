package com.example.backend.repository;

import com.example.backend.model.entity.File;
import com.example.backend.model.entity.ShareLink;
import com.example.backend.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ShareLinkRepositoryTest {

    @Autowired
    private ShareLinkRepository shareLinkRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByExternalId_returnsShareLink() {
        File file = new File();
        fileRepository.save(file);

        ShareLink link = new ShareLink(file, Instant.now().plusSeconds(3600), false, 5);
        shareLinkRepository.save(link);

        ShareLink found = shareLinkRepository.findByExternalId(link.getExternalId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getExternalId()).isEqualTo(link.getExternalId());
    }

    @Test
    void findAllByUser_returnsUsersLinks() {
        User user = new User("owner", "pw");
        userRepository.save(user);

        File file = new File();
        fileRepository.save(file);

        ShareLink l1 = new ShareLink(file, null, false, 1, user);
        ShareLink l2 = new ShareLink(file, null, false, 1, user);

        shareLinkRepository.saveAll(List.of(l1, l2));

        List<ShareLink> results = shareLinkRepository.findAllByUser(user);

        assertThat(results).hasSize(2);
    }

    @Test
    void findAllByUser_empty_whenNone() {
        User user = new User("nobody", "pw");
        userRepository.save(user);

        List<ShareLink> results = shareLinkRepository.findAllByUser(user);

        assertThat(results).isEmpty();
    }
}

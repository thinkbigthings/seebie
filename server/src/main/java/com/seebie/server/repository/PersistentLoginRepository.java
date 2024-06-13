package com.seebie.server.repository;

import com.seebie.server.entity.PersistentLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface PersistentLoginRepository extends JpaRepository<com.seebie.server.entity.PersistentLogin, String> {

    long countAllByUsername(String username);

    List<PersistentLogin> findAllByUsername(String username);

    void deleteByLastUsedBefore(Instant lastUsed);
}

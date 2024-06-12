package com.seebie.server.repository;


import com.seebie.server.dto.PersistentLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface PersistentLoginRepository extends JpaRepository<com.seebie.server.entity.PersistentLogin, String> {

    @Query( """
            SELECT new com.seebie.server.dto.PersistentLogin(p.series, p.token, p.username, p.lastUsed)
            FROM PersistentLogin p
            WHERE p.username = :username
            ORDER BY p.lastUsed DESC 
            """)
    List<PersistentLogin> findAllPersistentLogins(String username);

    void findAllByUsername(String username);

    void findAllByLastUsedBefore(Instant lastUsed);

    void deleteByLastUsedBefore(Instant lastUsed);
}

package com.seebie.server.repository;


import com.seebie.server.dto.UserSummary;
import com.seebie.server.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String name);

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.username=?1")
    Optional<User> loadUserWithRoles(String name);

    @Query("""
            SELECT new com.seebie.server.dto.UserSummary(u.username, u.displayName)
            FROM User u
            ORDER BY u.username ASC
            """)
    Page<UserSummary> loadSummaries(Pageable page);

    Optional<User> findByUsername(String name);

}

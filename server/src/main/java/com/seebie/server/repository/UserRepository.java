package com.seebie.server.repository;


import com.seebie.server.dto.UserSummary;
import com.seebie.server.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.email=?1")
    Optional<User> loadUserWithRoles(String email);

    @Query("""
            SELECT new com.seebie.server.dto.UserSummary(u.publicId, u.displayName)
            FROM User u
            ORDER BY u.publicId ASC
            """)
    Page<UserSummary> loadSummaries(Pageable page);

    Optional<User> findByPublicId(UUID publicId);

    default Optional<User> findByPublicId(String publicId) {
        return findByPublicId(UUID.fromString(publicId));
    }

    Optional<User> findByEmail(String email);
}

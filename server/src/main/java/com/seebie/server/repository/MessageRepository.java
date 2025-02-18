package com.seebie.server.repository;

import com.seebie.server.dto.MessageDto;
import com.seebie.server.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    @Query("""
            SELECT new com.seebie.server.dto.MessageDto(m.text, m.type)
            FROM MessageEntity m
            WHERE m.user.publicId=:publicId
            AND m.time >= :earliest
            ORDER BY m.time ASC
            """)
    List<MessageDto> findSince(UUID publicId, Instant earliest);

    @Query("""
            DELETE FROM MessageEntity m
            WHERE m.user.publicId=:publicId
            """)
    void deleteAllByUserPublicId(UUID publicId);

}

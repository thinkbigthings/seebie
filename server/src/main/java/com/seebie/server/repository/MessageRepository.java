package com.seebie.server.repository;

import com.seebie.server.dto.MessageDto;
import com.seebie.server.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;


public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("""
            SELECT new com.seebie.server.dto.MessageDto(m.content, m.type)
            FROM Message m
            WHERE m.user.publicId=:publicId
            ORDER BY m.time ASC
            """)
    List<MessageDto> findAllByUserPublicId(UUID publicId);
}


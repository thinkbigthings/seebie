package com.seebie.server.repository;


import com.seebie.server.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

    // TODO this needs to find by user AND text
    Tag findByText(String text);
}

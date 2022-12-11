package com.seebie.server.repository;


import com.seebie.server.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.seebie.dto.StoreRecord;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    Optional<Store> findByName(String name);

    @Query("SELECT new org.thinkbigthings.zdd.dto.StoreRecord" +
            "(s.name, s.website) " +
            "FROM Store s " +
            "ORDER BY s.name ASC ")
    Page<StoreRecord> loadSummaries(Pageable page);
}

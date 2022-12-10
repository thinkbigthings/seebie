package org.thinkbigthings.zdd.server.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.thinkbigthings.zdd.dto.StoreRecord;
import org.thinkbigthings.zdd.server.entity.Store;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public interface StoreRepository extends JpaRepository<Store, Long> {

    Optional<Store> findByName(String name);

    @Query("SELECT new org.thinkbigthings.zdd.dto.StoreRecord" +
            "(s.name, s.website) " +
            "FROM Store s " +
            "ORDER BY s.name ASC ")
    Page<StoreRecord> loadSummaries(Pageable page);
}

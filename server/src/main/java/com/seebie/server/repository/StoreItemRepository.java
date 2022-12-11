package com.seebie.server.repository;


import com.seebie.server.entity.StoreItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreItemRepository extends JpaRepository<StoreItem, Long>, JpaSpecificationExecutor<StoreItem> {

    @Query(value = "SELECT t FROM StoreItem t " +
            "WHERE t.store.name=:storename " +
            "ORDER BY t.added DESC",
            countQuery = "SELECT COUNT(t) FROM StoreItem t WHERE t.store.name=:storename")
    Page<StoreItem> findByStoreName(@Param("storename") String storename, Pageable page);

}

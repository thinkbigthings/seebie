package org.thinkbigthings.zdd.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.thinkbigthings.zdd.server.entity.SearchConfig;
import org.thinkbigthings.zdd.server.entity.Store;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SearchConfigRepository extends JpaRepository<SearchConfig, Long> {

    @Query("SELECT s FROM Store s WHERE s.name IN ?1")
    List<Store> findByNames(Collection<String> names);

    // use LEFT JOIN FETCH here, otherwise all results could come back empty if the collection is empty
    @Query("SELECT s FROM SearchConfig s " +
            "LEFT JOIN FETCH s.searchStores " +
            "LEFT JOIN FETCH s.subSearches " +
            "WHERE s.user.username=?1 ")
    Optional<SearchConfig> findByUsername(String name);

    @Query("SELECT s FROM SearchConfig s " +
            "LEFT JOIN FETCH s.searchStores " +
            "LEFT JOIN FETCH s.subSearches " +
            "WHERE s.user.username=?1 " +
            "AND s.active=TRUE ")
    Optional<SearchConfig> findActiveByUsername(String name);

}

package org.thinkbigthings.boot.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.thinkbigthings.boot.domain.Sleep;

@Repository
public interface SleepSessionRepository extends JpaRepository<Sleep, Long>, JpaSpecificationExecutor<Sleep> {

    // http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.special-parameters

    Sleep findOneByUserIdAndId(Long userId, Long sleepId);
    Page<Sleep> findByUserId(Long userId, Pageable pageable);
    List<Sleep> findAllByUserId(Long userId);

}

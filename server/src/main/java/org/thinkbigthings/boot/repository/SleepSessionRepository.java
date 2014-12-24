package org.thinkbigthings.boot.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.thinkbigthings.boot.domain.Sleep;
import org.thinkbigthings.boot.domain.User;

@Repository
public interface SleepSessionRepository extends JpaRepository<Sleep, Long> {
    
   // http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.special-parameters

    // TODO 2 pass user id instead of user object, so won't need extra lookup to get user object inside sleep service
   List<Sleep> findByUserAndId(User user, Long sleepId);
   Page<Sleep> findByUser(User user, Pageable pageable);

}

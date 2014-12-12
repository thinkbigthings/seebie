package org.thinkbigthings.boot.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.thinkbigthings.boot.domain.Sleep;
import org.thinkbigthings.boot.domain.User;

@Repository
public interface SleepSessionRepository extends JpaRepository<Sleep, Long> 
{
   List<Sleep> findByUser(User user);

}

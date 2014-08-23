package org.thinkbigthings.boot.repository;

import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.thinkbigthings.boot.domain.SleepSession;

@Repository
public interface SleepSessionRepository extends JpaRepository<SleepSession, Long> 
{
   SleepSession findByTimeOutOfBed(Date time);

}

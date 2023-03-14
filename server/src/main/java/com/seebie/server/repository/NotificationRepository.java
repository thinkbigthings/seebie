package com.seebie.server.repository;

import com.seebie.server.entity.Notification;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n " +
            "WHERE n.user.username=:username ")
    Optional<Notification> findBy(String username);

    /**
     *
     * @param notificationTrigger If a notification hasn't been sent since this time, then one should be sent.
     * @return
     */
//    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT n FROM Notification n " +
            "WHERE n.lastSent < :notificationTrigger ")
    List<Notification> findNotificationsBefore(Instant notificationTrigger);

}

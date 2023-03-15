package com.seebie.server.repository;

import com.seebie.server.entity.Notification;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n " +
            "WHERE n.user.username=:username ")
    Optional<Notification> findBy(String username);

    /**
     *SELECT COUNT(u)
     * FROM User u
     * WHERE u IN (
     *    SELECT DISTINCT u
     *    FROM User u
     *    JOIN u.roles r
     *    WHERE r.id IN (1)
     *
     *    SELECT u FROM User u LEFT JOIN u.addresses a WHERE u.id = a.user.
     * )
     * @param lastNotificationSentBefore If a notification hasn't been sent since this time, then one should be sent.
     * @param lastSleepLoggedBefore If sleep has not been logged since this time, then notification should be sent
     * @return
     */
//    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT n FROM Notification n, SleepSession s " +
            "WHERE n.lastSent < :lastNotificationSentBefore " +
            "AND EXISTS ( SELECT s FROM SleepSession s WHERE s.user = n.user AND s.stopTime < :lastSleepLoggedBefore)")
    List<Notification> findNotificationsBy(Instant lastNotificationSentBefore, ZonedDateTime lastSleepLoggedBefore);


}

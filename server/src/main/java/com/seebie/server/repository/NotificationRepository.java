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
     * @param lastNotificationSentBefore If a notification hasn't been sent since this time, then one should be sent.
     * @param lastSleepLoggedBefore If sleep has not been logged since this time, then notification should be sent
     * @return
     */
    // TODO see if we should use query hints
//    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT n FROM Notification n, SleepSession s " +
            "WHERE n.lastSent < :lastNotificationSentBefore " +
            "AND EXISTS " +
                "(SELECT s FROM SleepSession s WHERE s.user = n.user AND s.stopTimeInstant < :lastSleepLoggedBefore)")
    List<Notification> findNotificationsBy(Instant lastNotificationSentBefore, Instant lastSleepLoggedBefore);

}

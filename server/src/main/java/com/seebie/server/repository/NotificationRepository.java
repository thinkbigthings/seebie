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
     * This is a native query because the JPA types were different but the native database types were the same.
     * Note that the lock needs to be native too ("FOR NO KEY UPDATE")
     *
     * @param lastNotificationSentBefore If a notification hasn't been sent since this time, then one should be sent.
     * @param lastSleepLoggedBefore If sleep has not been logged since this time, then notification should be sent
     * @return Notification records that have expired and need to be sent.
     */
//    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("SELECT n FROM Notification n, SleepSession s " +
//            "WHERE n.lastSent < :lastNotificationSentBefore " +
//            "AND EXISTS " +
//                "(SELECT s FROM SleepSession s WHERE s.user = n.user AND s.stopTimeInstant < :lastSleepLoggedBefore)")
    @Query(nativeQuery = true, value =
            "SELECT n.user_id, n.last_sent FROM notification n WHERE n.last_sent < ?1 " +
            "AND EXISTS " +
            "(SELECT s.id " +
                    "FROM sleep_session s, app_user u " +
                    "WHERE s.user_id = u.id AND s.stop_time < ?2 AND u.id = n.user_id) " +
            "FOR NO KEY UPDATE" )
    List<Notification> findNotificationsBy(Instant lastNotificationSentBefore, Instant lastSleepLoggedBefore);

}

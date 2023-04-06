package com.seebie.server.repository;

import com.seebie.server.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.user.username=:username")
    Optional<Notification> findBy(String username);

    /**
     * This is a native query because the method parameters comparing against the JPA types were different
     * but the corresponding native database types were the same.
     * Note that the lock needs to be native too ("FOR NO KEY UPDATE")
     *
     * About database locks: This can be tested with separate shells into the database.
     * Observe manually starting a transaction in one shell, doing a select,
     * then starting a transaction in another shell and trying the same select.
     * The second select will block (default timeout is "0" which means block indefinitely)
     * until you commit the first transaction.
     *
     * START TRANSACTION;
     * SELECT * FROM NOTIFICATION FOR NO KEY UPDATE;
     * COMMIT;
     *
     * This default behavior is fine: a second query will just find nothing after the last_sent time is updated.
     * If we WANT another transactional attempt to cause an error,
     * An error can be triggered if a lock timeout is set e.g.:
     * SET lock_timeout TO '2s';
     *
     * @param lastNotificationSentBefore If a notification hasn't been sent since this time, then one should be sent.
     * @param noSleepLoggedSince If sleep has not been logged since this time, then notification should be sent
     *
     * @return Notification records that have expired and need to be sent.
     */
    @Query(nativeQuery = true, value = """
            SELECT n.*
            FROM notification n
            JOIN app_user u ON u.id = n.user_id
            WHERE n.last_sent < ?1
            AND u.notifications_enabled = TRUE
            AND NOT EXISTS (
              SELECT 1
              FROM sleep_session s
              WHERE s.user_id = u.id
              AND s.stop_time > ?2
            )
            FOR NO KEY UPDATE
        
            """)
    List<Notification> findNotificationsBy(Instant lastNotificationSentBefore, Instant noSleepLoggedSince);

}

package com.seebie.server.service;

import com.seebie.server.repository.PersistentLoginRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class PersistentLoginExpirationService {

    private PersistentLoginRepository persistentLoginRepository;

    public PersistentLoginExpirationService(PersistentLoginRepository repo) {
        this.persistentLoginRepository = repo;
    }

    /**
     * Transactional method must be called from a separate class from the Scheduled method.
     */
    @Transactional
    public void deleteExpiredRememberMeTokens(Instant timedOut) {
        persistentLoginRepository.deleteByLastUsedBefore(timedOut);
    }
}

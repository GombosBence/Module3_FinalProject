package com.example.springcore_module_3.util;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginAttemptTracker {

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(5);

    private final ConcurrentHashMap<String, AttemptRecord> attempts = new ConcurrentHashMap<>();

    public void recordFailure(String username){
        attempts.compute(username, (u, record) -> {
            if (record == null) return new AttemptRecord(1, null);
            int count = record.failedCount() + 1;
            Instant lockedUntil = count >= MAX_ATTEMPTS ? Instant.now().plus(LOCKOUT_DURATION) : null;
            return new AttemptRecord(count, lockedUntil);
        });
    }

    public void recordSuccess(String username){
        attempts.remove(username);
    }

    public boolean isLocked(String username) {
        AttemptRecord record = attempts.get(username);
        return record != null && record.lockedUntil() != null && Instant.now().isBefore(record.lockedUntil());
    }

    private record AttemptRecord(int failedCount, Instant lockedUntil){}
}

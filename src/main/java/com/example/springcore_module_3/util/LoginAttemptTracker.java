package com.example.springcore_module_3.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginAttemptTracker {

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(5);
    private static final Duration STALE_THRESHOLD = Duration.ofHours(1);

    private final ConcurrentHashMap<String, AttemptRecord> attempts = new ConcurrentHashMap<>();

    public void recordFailure(String username){
        attempts.compute(username, (u, record) -> {
            if (record == null) return new AttemptRecord(1, Instant.now(), null);
            int count = record.failedCount() + 1;
            Instant lockedUntil = count >= MAX_ATTEMPTS ? Instant.now().plus(LOCKOUT_DURATION) : null;
            return new AttemptRecord(count, Instant.now(), lockedUntil);
        });
    }

    public int size(){
        return attempts.size();
    }

    public void recordSuccess(String username){
        attempts.remove(username);
    }

    public boolean isLocked(String username) {
        AttemptRecord record = attempts.get(username);
        return record != null && record.lockedUntil() != null && Instant.now().isBefore(record.lockedUntil());
    }

    @Scheduled(fixedRate = 60000)
    public void pruneStaleAttempts() {
        Instant now = Instant.now();
        attempts.entrySet().removeIf(entry -> {
            AttemptRecord record = entry.getValue();
            boolean lockoutExpired = record.lockedUntil() != null && now.isAfter(record.lockedUntil());
            boolean staleUnlocked =
                    record.lockedUntil() == null && Duration.between(record.lastFailure(), now).compareTo(STALE_THRESHOLD) > 0;
            return lockoutExpired || staleUnlocked;
        });
    }

    private record AttemptRecord(int failedCount, Instant lastFailure ,Instant lockedUntil){}
}

package com.jessetong.notemanager.service;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public class TaskSchedulerWithCancel extends ThreadPoolTaskScheduler {

    private final Map<String, ScheduledFuture<?>> scheduledTasks = new IdentityHashMap<>();

    public void scheduleAtFixedRate(Runnable task, Duration period, String id) {
        ScheduledFuture<?> future = super.scheduleAtFixedRate(task, period);
        scheduledTasks.put(id, future);
    }

    /** one‐off scheduling that tracks the task under the given key **/
    public ScheduledFuture<?> schedule(Runnable task, Instant startTime, String id) {
        cancelScheduledTask(id);
        ScheduledFuture<?> f = super.schedule(task, startTime);
        scheduledTasks.put(id, f);
        return f;
    }

    /** override the Instant‐only method so you don’t see deprecation warnings */
    @Override
    public ScheduledFuture<?> schedule(Runnable task, Instant startTime) {
        return super.schedule(task, startTime);
    }

    public void cancelScheduledTask(String id) {
        ScheduledFuture<?> future = scheduledTasks.remove(id);
        if (future != null) {
            future.cancel(true);
        }
    }

    /** expose an unmodifiable view of id→future map */
    public Map<String, ScheduledFuture<?>> getScheduledTasks() {
        return Collections.unmodifiableMap(scheduledTasks);
    }
}

package com.jessetong.notemanager.service;

import com.jessetong.notemanager.entity.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public void send(Schedule schedule, String message) {
        // replace with real email/push/etc.
        log.info("[{}] for schedule {} at {}",
                 message, schedule.getTitle(), schedule.getStartTime());
    }
}

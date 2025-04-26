package com.jessetong.notemanager.service;

import com.jessetong.notemanager.entity.Schedule;
import com.jessetong.notemanager.service.TaskSchedulerWithCancel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;

@Service
public class ScheduleNotificationService {

    private final TaskSchedulerWithCancel taskScheduler;
    private final NotificationService notificationService;

    @Autowired
    public ScheduleNotificationService(TaskSchedulerWithCancel taskScheduler, NotificationService notificationService) {
        this.taskScheduler = taskScheduler;
        this.notificationService = notificationService;
    }

    public void scheduleAll(Schedule s) {
        removeSchedule(s);
        schedule(s, s.getStartTime().minusMinutes(30), String.format("Schedule with ID %d starts in 30 minutes.", s.getId()));
        schedule(s, s.getStartTime().minusMinutes(10), String.format("Schedule with ID %d starts in 10 minutes.", s.getId()));
        schedule(s, s.getStartTime(), String.format("Schedule with ID %d starts.", s.getId()));
        schedule(s, s.getEndTime(), String.format("Schedule with ID %d ends.", s.getId()));
    }

    private void schedule(Schedule s, java.time.LocalDateTime when, String message){
        if (when.isAfter(java.time.LocalDateTime.now())) {
            java.time.Instant instant = when.atZone(ZoneId.systemDefault()).toInstant();
            taskScheduler.schedule(() -> notificationService.send(s, message), instant, message);
        }
    }

    public void removeSchedule(Schedule s){
        taskScheduler.cancelScheduledTask(String.format("Schedule with ID %d starts in 30 minutes.", s.getId()));
        taskScheduler.cancelScheduledTask(String.format("Schedule with ID %d starts in 10 minutes.", s.getId()));
        taskScheduler.cancelScheduledTask(String.format("Schedule with ID %d starts.", s.getId()));
        taskScheduler.cancelScheduledTask(String.format("Schedule with ID %d ends.", s.getId()));
    }
}

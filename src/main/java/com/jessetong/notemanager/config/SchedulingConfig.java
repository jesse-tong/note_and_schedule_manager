package com.jessetong.notemanager.config;

import com.jessetong.notemanager.service.TaskSchedulerWithCancel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

// You need to expose your custom TaskSchedulerWithCancel as a Spring bean so it can be injected.
@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Bean
    public TaskSchedulerWithCancel taskSchedulerWithCancel() {
        TaskSchedulerWithCancel scheduler = new TaskSchedulerWithCancel();
        scheduler.setPoolSize(5);
        scheduler.initialize();         // important: must initialize underlying executor
        return scheduler;
    }
}

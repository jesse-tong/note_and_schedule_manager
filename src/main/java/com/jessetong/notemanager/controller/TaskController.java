package com.jessetong.notemanager.controller;

import com.jessetong.notemanager.service.TaskSchedulerWithCancel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TaskController {
    private final TaskSchedulerWithCancel scheduler;

    public TaskController(TaskSchedulerWithCancel scheduler) {
        this.scheduler = scheduler;
    }

    @GetMapping("/tasks")
    public String listTasks(Model model) {
        List<Map<String,Object>> tasks = scheduler.getScheduledTasks()
        .entrySet().stream()
        .map(e -> Map.<String,Object>of(
            "id",         e.getKey(),
            "done",       e.getValue().isDone(),
            "cancelled",  e.getValue().isCancelled()
        ))
        .collect(Collectors.toList());

        model.addAttribute("tasks", tasks);
        return "task-list";
    }
}
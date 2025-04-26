package com.jessetong.notemanager.controller;

import com.jessetong.notemanager.entity.Schedule;
import com.jessetong.notemanager.entity.User;
import com.jessetong.notemanager.repository.ScheduleRepository;
import com.jessetong.notemanager.repository.UserRepository; // Import UserRepository
import com.jessetong.notemanager.service.ScheduleNotificationService;

//import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


@Controller
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository; // Inject UserRepository
    private final ScheduleNotificationService scheduleNotificationService; // Inject ScheduleNotificationService

    @Autowired
    public ScheduleController(ScheduleRepository scheduleRepository, UserRepository userRepository, ScheduleNotificationService scheduleNotificationService) {
        this.scheduleNotificationService = scheduleNotificationService; // Initialize ScheduleNotificationService
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository; // Initialize UserRepository
    }

    @GetMapping
    public String listSchedules(
        @RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="10") int size,
        Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null); // Get the current user
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").descending()); // Create a pageable object
        Page<Schedule> schedules = scheduleRepository.findByUserId(currentUser.getId(), pageable); // Fetch schedules for the current user
        model.addAttribute("schedules", schedules);
        return "schedule-list";
    }


    @GetMapping("/view/{id}")
    public String viewSchedule(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null); // Get the current user
        Schedule schedule = scheduleRepository.findById(id).orElse(null);
        if (schedule != null && schedule.getUser().equals(currentUser)) {
            model.addAttribute("schedule", schedule);
            return "schedule-view"; // Return the view template for the schedule
        } else {
            return "redirect:/schedules"; // Redirect if the schedule does not belong to the user
        }
    }

    @GetMapping("/search")
    public String searchSchedules(@RequestParam("query") String query, 
    @RequestParam("start") LocalDateTime start, 
    @RequestParam("end") LocalDateTime end,
    Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null); // Get the current user
        if (query == null || query.isEmpty()) {
            query = ""; // Default to empty string if no query is provided
        }
        List<Schedule> schedules;
        if (start == null && end == null){
            schedules = scheduleRepository.findByUserIdAndTitleContaining(currentUser.getId(), query); // Fetch schedules for the current user
        }else if (start != null && end != null){
            schedules = scheduleRepository.findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(currentUser.getId(), start, end); // Fetch schedules for the current user
        }else if (start != null && end == null){
            schedules = scheduleRepository.findByUserIdAndStartTimeGreaterThanEqual(currentUser.getId(), start); // Fetch schedules for the current user
        }else{
            schedules = scheduleRepository.findByUserIdAndEndTimeLessThanEqual(currentUser.getId(), end); // Fetch schedules for the current user
        }
        model.addAttribute("schedules", schedules);
        return "schedule-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("schedule", new Schedule());
        return "schedule-form";
    }

    @PostMapping("/save")
    public String saveSchedule(@ModelAttribute Schedule schedule) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null);
        schedule.setCreatedAt(LocalDateTime.now());
        schedule.setUpdatedAt(LocalDateTime.now());
        schedule.setUser(currentUser); // Set the user for the schedule
        Schedule saved = scheduleRepository.save(schedule);
        scheduleNotificationService.scheduleAll(saved);
        return "redirect:/schedules";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null);
        Schedule schedule = scheduleRepository.findById(id).orElse(null);
        if (schedule != null && schedule.getUser().equals(currentUser)) {
            model.addAttribute("schedule", schedule);
            return "schedule-form";
        } else {
            return "redirect:/schedules"; // Redirect if the schedule does not belong to the user
        }
    }

    @PostMapping("/edit/{id}")
    public String editSchedule(@PathVariable Long id, @ModelAttribute Schedule schedule, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null);
        Schedule existingSchedule = scheduleRepository.findById(id).orElse(null);
        if (existingSchedule == null || !existingSchedule.getUser().equals(currentUser)) {
            model.addAttribute("error", "Schedule not found or does not belong to the user.");
            return "redirect:/schedules"; // Redirect if schedule not found or does not belong to the user
        }
        existingSchedule.setTitle(schedule.getTitle());
        existingSchedule.setDescription(schedule.getDescription());
        existingSchedule.setStartTime(schedule.getStartTime());
        existingSchedule.setEndTime(schedule.getEndTime());
        existingSchedule.setUpdatedAt(LocalDateTime.now());
        existingSchedule.setUser(currentUser); // Set the user for the schedule
        Schedule saved = scheduleRepository.save(existingSchedule);
        scheduleNotificationService.removeSchedule(existingSchedule); // Remove the old schedule notification
        scheduleNotificationService.scheduleAll(saved);
        return "redirect:/schedules";
    }

    @PostMapping("/delete/{id}")
    public String deleteSchedule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null);
        Schedule schedule = scheduleRepository.findById(id).orElse(null);
        if (schedule != null && schedule.getUser().equals(currentUser)) {
            scheduleNotificationService.removeSchedule(schedule);
            scheduleRepository.delete(schedule);
            return "redirect:/schedules"; // Redirect if the schedule belongs to the user
        }else if (schedule == null || !schedule.getUser().equals(currentUser)){
            redirectAttributes.addFlashAttribute("error", "Schedule not found or does not belong to the user.");
        }
        return "redirect:/schedules";
    }

    @GetMapping("/calendar")
    public String showCalendar(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userRepository.findByUsername(auth.getName()).get().getId();
        List<Schedule> schedules = scheduleRepository.findByUserId(userId, Pageable.unpaged()).getContent();
        List<Map<String,Object>> events = schedules.stream().map(s -> {
            Map<String,Object> evt = new HashMap<>();
            evt.put("id", s.getId());
            evt.put("title", s.getTitle());
            evt.put("start", s.getStartTime().toString());
            evt.put("end", s.getEndTime().toString());
            return evt;
        }).toList();
        model.addAttribute("events", events);
        return "calendar";
    }
}



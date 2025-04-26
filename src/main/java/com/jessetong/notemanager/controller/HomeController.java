package com.jessetong.notemanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        // redirect users hitting the root URL to the schedule list (or notes, whichever you prefer)
        return "redirect:/notes";
    }
}
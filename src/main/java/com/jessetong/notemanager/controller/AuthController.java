package com.jessetong.notemanager.controller;
import com.jessetong.notemanager.entity.User;
import com.jessetong.notemanager.repository.UserRepository; // Import UserRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository; // Inject UserRepository

    @Autowired
    public AuthController(UserRepository userRepository,
                          BCryptPasswordEncoder passwordEncoder) { // ← inject
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Username already exists.");
            return "redirect:/register";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encode password
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Registration successful. You can now log in.");
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String showLoginForm() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/notes"; // Redirect to notes if already logged in (only non-anonymous users)
            // If we don't check for anonymous, it will redirect to login page repeatedly
        }
        return "login"; // Show login form
    }

}

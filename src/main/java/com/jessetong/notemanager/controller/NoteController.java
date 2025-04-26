package com.jessetong.notemanager.controller;

import com.jessetong.notemanager.entity.Note;
import com.jessetong.notemanager.entity.User;
import com.jessetong.notemanager.repository.NoteRepository;
import com.jessetong.notemanager.repository.UserRepository; // Import UserRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/notes")
public class NoteController {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository; // Inject UserRepository

    @Autowired
    public NoteController(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository; // Initialize UserRepository
    }
    
    @GetMapping
    public String listNotes(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        if (currentUsername == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null); // Get the current user
        List<Note> notes = noteRepository.findByUser(currentUser); // Fetch notes for the current user
        model.addAttribute("notes", notes);
        return "note-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("note", new Note());
        return "note-form";
    }

    @PostMapping("/save")
    public String saveNote(@ModelAttribute Note note) {
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         String currentUsername = authentication.getName();
         User currentUser = userRepository.findByUsername(currentUsername).orElse(null);
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());
        note.setUser(currentUser); // Set the user for the note
        noteRepository.save(note);
        return "redirect:/notes";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Note note = noteRepository.findById(id).orElse(null);
        if (note == null) {
            return "redirect:/notes"; // Redirect if note not found
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null); // Get the current user
        if (!note.getUser().equals(currentUser)) {
            return "redirect:/notes"; // Redirect if note does not belong to the user
        }
        model.addAttribute("note", note);
        return "note-form";
    }

    @PostMapping("/edit/{id}")
    public String editNote(@PathVariable("id") Long id, @ModelAttribute Note note) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null); // Get the current user
        Note existingNote = noteRepository.findById(id).orElse(null);
        if (existingNote == null || !existingNote.getUser().equals(currentUser)) {
            return "redirect:/notes"; // Redirect if note not found or does not belong to the user
        }
        existingNote.setUpdatedAt(LocalDateTime.now());
        existingNote.setTitle(note.getTitle());
        existingNote.setContent(note.getContent());
        existingNote.setUser(currentUser); // Set the user for the note
        noteRepository.save(existingNote);
        return "redirect:/notes";
    }

    @PostMapping("/delete/{id}")
    public String deleteNote(@PathVariable("id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null); // Get the current user
        Note note = noteRepository.findById(id).orElse(null);
        if (note != null && note.getUser().equals(currentUser)) {
            noteRepository.delete(note); // Delete the note if it belongs to the user
        }
        return "redirect:/notes";
    }

    @GetMapping("/view/{id}")
    public String viewNote(@PathVariable("id") Long id, Model model) {
        Note note = noteRepository.findById(id).orElse(null);
        if (note == null) {
            return "redirect:/notes"; // Redirect if note not found
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null); // Get the current user
        if (!note.getUser().equals(currentUser)) {
            return "redirect:/notes"; // Redirect if note does not belong to the user
        }
        model.addAttribute("note", note);
        return "note-view";
    }

    @GetMapping("/search")
    public String searchNotes(@RequestParam("query") String query, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null); // Get the current user
        List<Note> notes = noteRepository.findByUserAndTitleContaining(currentUser, query); // Fetch notes for the current user
        model.addAttribute("notes", notes);
        return "note-list";
    }
    // Add more methods for editing, deleting notes, ensuring user association
}

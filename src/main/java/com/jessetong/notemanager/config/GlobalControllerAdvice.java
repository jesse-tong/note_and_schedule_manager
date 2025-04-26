package com.jessetong.notemanager.config;

import com.jessetong.notemanager.entity.User;
import com.jessetong.notemanager.repository.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// ControllerAdvice can be used to define global variables that can be accessed in all controllers
// and views. In this case, we are using it to populate the current user in the model.
@ControllerAdvice
public class GlobalControllerAdvice {

  private final UserRepository userRepository;

  public GlobalControllerAdvice(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @ModelAttribute("currentUser")
  public User populateCurrentUser(Authentication authentication) {
    if (authentication != null
        && authentication.isAuthenticated()
        && !(authentication instanceof AnonymousAuthenticationToken)) {
      return userRepository.findByUsername(authentication.getName())
                           .orElse(null);
    }
    return null;
  }
}

package com.example.animalshelter.controller;

import com.example.animalshelter.model.User;
import com.example.animalshelter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class UserProfileController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal User user, Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("password", user.getPassword());
        model.addAttribute("role", user.getRole());
        model.addAttribute("users", users);
        return "/fragments/userProfile";
    }
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal User user, @ModelAttribute("updatedUser") User updatedUser) {

        updatedUser.setId(user.getId()); // Set the ID of the updated user
        updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        userRepository.save(updatedUser);
        return "redirect:/dashboard";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/profile/add")
    public String addProfile(@AuthenticationPrincipal User user, @ModelAttribute("addUser") User addUser) {

        addUser.setPassword(passwordEncoder.encode(addUser.getPassword()));
        userRepository.save(addUser);
        return "redirect:/dashboard";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/profile/delete/{id}")
    public String deleteProfile(@PathVariable("id") Long id) {
        userRepository.deleteById(id);
        return "redirect:/dashboard";
    }
}
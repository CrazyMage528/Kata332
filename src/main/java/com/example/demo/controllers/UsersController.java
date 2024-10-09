package com.example.demo.controllers;

import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Controller
public class UsersController {

    private final UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("users", usersService.findAll());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
            return "register";
        }

        User existingUser = usersService.findByUsername(user.getName());
        if (existingUser != null) {
            bindingResult.rejectValue("name", "error.user", "A user with this username already exists.");
            model.addAttribute("users", usersService.findAll());
            return "register";
        }

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            bindingResult.rejectValue("roles", "error.user", "At least one role must be selected.");
            model.addAttribute("users", usersService.findAll());
            return "register";
        }

        Role userRole = usersService.findRoleByName("ROLE_USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            usersService.saveRole(userRole);
        }

        Role adminRole = usersService.findRoleByName("ROLE_ADMIN");
        if (adminRole == null) {
            adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            usersService.saveRole(adminRole);
        }

        Set<Role> selectedRoles = new HashSet<>();
        for (Role role : user.getRoles()) {
            if (role.getName().equals("ROLE_ADMIN")) {
                selectedRoles.add(adminRole);
            } else if (role.getName().equals("ROLE_USER")) {
                selectedRoles.add(userRole);
            }
        }
        user.setRoles(selectedRoles);
        usersService.saveUserWithRoles(user);

        model.addAttribute("users", usersService.findAll());
        model.addAttribute("user", new User());
        return "register";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/users")
    public String showAllUsers(Model model) {
        model.addAttribute("users", usersService.findAll());
        return "users";
    }
}

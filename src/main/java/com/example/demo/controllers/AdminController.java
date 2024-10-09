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
@RequestMapping("/admin")
public class AdminController {

    private final UsersService usersService;

    @Autowired
    public AdminController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping
    public String adminHome() {
        return "adminHome";
    }

    @GetMapping("/users")
    public String showAllUsers(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("users", usersService.findAll());
        return "users";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "createUser";
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

        usersService.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", usersService.findOne(id));
        return "editUser";
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable("id") Long id, @ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "editUser";
        }

        User existingUser = usersService.findOne(id);

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
        existingUser.setRoles(selectedRoles);
        existingUser.setName(user.getName());
        existingUser.setLastName(user.getLastName());
        existingUser.setAge(user.getAge());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
            if (user.getOriginalPassword() == null || user.getOriginalPassword().isEmpty()) {
                existingUser.setOriginalPassword(existingUser.getPassword());
            } else {
                existingUser.setOriginalPassword(user.getOriginalPassword());
            }
        }

        usersService.update(id, existingUser);
        return "redirect:/admin/users";
    }





}

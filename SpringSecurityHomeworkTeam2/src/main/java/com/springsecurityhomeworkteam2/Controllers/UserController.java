package com.springsecurityhomeworkteam2.Controllers;

import com.springsecurityhomeworkteam2.Entities.Role;
import com.springsecurityhomeworkteam2.Entities.User;
import com.springsecurityhomeworkteam2.Repository.RoleRepository;
import com.springsecurityhomeworkteam2.Services.UserService;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;


@Controller
public class UserController {
    private final UserService userService;
    private final RoleRepository roleRepository;

    public UserController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleRepository.findAll());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user,
                           @RequestParam Set<Long> roleIds,
                           Model model) {
        try {

            Set<Role> selectedRoles = new HashSet<>(roleRepository.findAllById(roleIds));
            user.setRoles(selectedRoles);

            userService.save(user);
            return "redirect:/login?success";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка регистрации: " + e.getMessage());
            model.addAttribute("allRoles", roleRepository.findAll());
            return "register";
        }
    }

    @GetMapping("/login")
    public String loginForm(
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "success", required = false) String success,
            Model model
    ) {

        if (error != null) {
            model.addAttribute("loginError", "Неверные учетные данные");
        }
        if (success != null) {
            model.addAttribute("registerSuccess", "Регистрация прошла успешно!");
        }
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        model.addAttribute("user", user);
        return "dashboard";
    }


}
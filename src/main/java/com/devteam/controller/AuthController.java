package com.devteam.controller;

import com.devteam.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.Valid;

@Controller
public class AuthController {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/login-page")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                              BindingResult result, 
                              @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
                              Model model) {
        
        // Check if passwords match
        if (!user.getPassword().equals(confirmPassword)) {
            result.rejectValue("password", "error.user", "Mật khẩu và xác nhận mật khẩu không khớp");
        }
        
        // Check if username already exists
        TypedQuery<User> usernameQuery = entityManager.createQuery(
            "SELECT u FROM User u WHERE u.username = :username", User.class);
        usernameQuery.setParameter("username", user.getUsername());
        if (!usernameQuery.getResultList().isEmpty()) {
            result.rejectValue("username", "error.user", "Tên đăng nhập đã tồn tại");
        }
        
        // Check if email already exists
        TypedQuery<User> emailQuery = entityManager.createQuery(
            "SELECT u FROM User u WHERE u.email = :email", User.class);
        emailQuery.setParameter("email", user.getEmail());
        if (!emailQuery.getResultList().isEmpty()) {
            result.rejectValue("email", "error.user", "Email đã được sử dụng");
        }
        
        if (result.hasErrors()) {
            return "register";
        }
        
        // Register the user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        user.setActive(true);
        entityManager.persist(user);
        
        return "redirect:/register?success";
    }
} 
package com.elearning.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import java.util.Random;

@Controller
public class RegistrationController {

    // Show registration form with CAPTCHA question
    @GetMapping("/register")
    public String showRegistrationForm(HttpSession session, Model model) {
        String[] captcha = generateCaptcha();
        String question = captcha[0];
        String answer = captcha[1];

        // Store answer in session
        session.setAttribute("captchaAnswer", answer);
        model.addAttribute("captchaQuestion", question);
        return "register";
    }

    // Handle registration form submission
    @PostMapping("/register")
    public String handleRegistration(@RequestParam String username,
                                     @RequestParam String password,
                                     @RequestParam String captchaResponse,
                                     HttpSession session,
                                     Model model) {
        String expectedAnswer = (String) session.getAttribute("captchaAnswer");
        if (expectedAnswer == null || !captchaResponse.trim().equals(expectedAnswer)) {
            model.addAttribute("error", "Captcha incorrect. Please try again.");
            // Re-generate CAPTCHA for retry
            String[] captcha = generateCaptcha();
            String question = captcha[0];
            String answer = captcha[1];
            session.setAttribute("captchaAnswer", answer);
            model.addAttribute("captchaQuestion", question);
            return "register";
        }

        // Proceed with registration (e.g., save user to database)
        // For simplicity, just show success message
        model.addAttribute("message", "Registration successful!");
        return "register";
    }

    // Generate simple math CAPTCHA
    private String[] generateCaptcha() {
        Random rand = new Random();
        int a = rand.nextInt(10); // 0-9
        int b = rand.nextInt(10);
        String question = "What is " + a + " + " + b + "?";
        String answer = String.valueOf(a + b);
        return new String[]{question, answer};
    }
}
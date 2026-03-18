package com.groupe.controller;

import com.groupe.service.AuthService;
import com.groupe.util.SessionUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController extends BaseController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "target", required = false) String target,
                            Model model,
                            HttpSession session) {
        SessionUser user = currentUser(session);
        if (user != null) {
            return redirectForRole(user, target);
        }
        model.addAttribute("target", target);
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "target", required = false) String target,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        try {
            SessionUser user = authService.login(username, password);
            session.setAttribute("currentUser", user);
            redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + user.getFullName() + ".");
            return redirectForRole(user, target);
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return target == null || target.isBlank()
                    ? "redirect:/auth/login"
                    : "redirect:/auth/login?target=" + target;
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "You have been logged out.");
        return "redirect:/home";
    }

    @GetMapping("/teacher-register")
    public String teacherRegistrationPage() {
        return "auth/register-teacher";
    }

    @PostMapping("/teacher-register")
    public String teacherRegistration(@RequestParam("fullName") String fullName,
                                      @RequestParam("username") String username,
                                      @RequestParam("email") String email,
                                      @RequestParam("password") String password,
                                      RedirectAttributes redirectAttributes) {
        try {
            authService.registerTeacher(fullName, username, email, password);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Registration submitted. An administrator must approve your teacher account before you can log in.");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/auth/teacher-register";
        }
    }

    private String redirectForRole(SessionUser user, String target) {
        if (target != null && !target.isBlank()) {
            return "redirect:" + target;
        }
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/admin";
        }
        if ("SURVEY_INITIATOR".equalsIgnoreCase(user.getRole())) {
            return "redirect:/initiator";
        }
        if ("TEACHER".equalsIgnoreCase(user.getRole())) {
            return "redirect:/teacher";
        }
        return "redirect:/surveys";
    }
}

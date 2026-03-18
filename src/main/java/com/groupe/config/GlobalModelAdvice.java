package com.groupe.config;

import com.groupe.util.SessionUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute
    public void addCommonAttributes(Model model, HttpSession session) {
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser instanceof SessionUser sessionUser) {
            model.addAttribute("currentUser", sessionUser);
        }
        model.addAttribute("applicationName", "Course Evaluation Survey System");
    }
}

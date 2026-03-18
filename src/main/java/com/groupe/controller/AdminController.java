package com.groupe.controller;

import com.groupe.model.Course;
import com.groupe.util.SessionUser;
import com.groupe.service.AdminService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public String dashboard(Model model, HttpSession session) {
        SessionUser user = currentUser(session);
        if (!hasRole(user, "ADMIN")) {
            return "redirect:/auth/login";
        }

        model.addAttribute("pendingTeachers", adminService.getPendingTeachers().size());
        model.addAttribute("totalCourses", adminService.getCourses().size());
        model.addAttribute("totalUsers", adminService.getAllUsers().size());
        model.addAttribute("totalSurveys", adminService.getAllSurveys().size());
        return "admin/dashboard";
    }

    @GetMapping("/teachers")
    public String pendingTeachers(Model model, HttpSession session) {
        SessionUser user = currentUser(session);
        if (!hasRole(user, "ADMIN")) {
            return "redirect:/auth/login";
        }

        model.addAttribute("pendingTeachers", adminService.getPendingTeachers());
        return "admin/pending-teachers";
    }

    @PostMapping("/teachers/{userId}/approve")
    public String approveTeacher(@PathVariable("userId") Long userId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!hasRole(currentUser(session), "ADMIN")) {
            return "redirect:/auth/login";
        }
        adminService.approveTeacher(userId);
        redirectAttributes.addFlashAttribute("successMessage", "Teacher approved successfully.");
        return "redirect:/admin/teachers";
    }

    @PostMapping("/teachers/{userId}/reject")
    public String rejectTeacher(@PathVariable("userId") Long userId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (!hasRole(currentUser(session), "ADMIN")) {
            return "redirect:/auth/login";
        }
        adminService.rejectTeacher(userId);
        redirectAttributes.addFlashAttribute("successMessage", "Teacher registration rejected.");
        return "redirect:/admin/teachers";
    }

    @GetMapping("/courses")
    public String courses(Model model, HttpSession session) {
        if (!hasRole(currentUser(session), "ADMIN")) {
            return "redirect:/auth/login";
        }
        model.addAttribute("courses", adminService.getCourses());
        return "admin/courses";
    }

    @GetMapping("/courses/new")
    public String newCourse(Model model, HttpSession session) {
        if (!hasRole(currentUser(session), "ADMIN")) {
            return "redirect:/auth/login";
        }
        model.addAttribute("course", new Course());
        return "admin/course-form";
    }

    @GetMapping("/courses/{courseId}/edit")
    public String editCourse(@PathVariable("courseId") Long courseId, Model model, HttpSession session) {
        if (!hasRole(currentUser(session), "ADMIN")) {
            return "redirect:/auth/login";
        }
        model.addAttribute("course", adminService.getCourse(courseId).orElseThrow());
        return "admin/course-form";
    }

    @PostMapping("/courses/save")
    public String saveCourse(@RequestParam(value = "id", required = false) Long courseId,
                             @RequestParam("code") String code,
                             @RequestParam("name") String name,
                             @RequestParam(value = "description", required = false) String description,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (!hasRole(currentUser(session), "ADMIN")) {
            return "redirect:/auth/login";
        }
        try {
            adminService.saveCourse(courseId, code, name, description);
            redirectAttributes.addFlashAttribute("successMessage", "Course saved successfully.");
            return "redirect:/admin/courses";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return courseId == null
                    ? "redirect:/admin/courses/new"
                    : "redirect:/admin/courses/" + courseId + "/edit";
        }
    }

    @PostMapping("/courses/{courseId}/delete")
    public String deleteCourse(@PathVariable("courseId") Long courseId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!hasRole(currentUser(session), "ADMIN")) {
            return "redirect:/auth/login";
        }
        adminService.deleteCourse(courseId);
        redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully.");
        return "redirect:/admin/courses";
    }

    @GetMapping("/courses/{courseId}/assign")
    public String assignTeachersPage(@PathVariable("courseId") Long courseId, Model model, HttpSession session) {
        if (!hasRole(currentUser(session), "ADMIN")) {
            return "redirect:/auth/login";
        }
        model.addAttribute("course", adminService.getCourse(courseId).orElseThrow());
        model.addAttribute("teachers", adminService.getApprovedTeachers());
        model.addAttribute("assignedTeacherIds", adminService.getAssignedTeacherIds(courseId));
        return "admin/assign-teachers";
    }

    @PostMapping("/courses/{courseId}/assign")
    public String assignTeachers(@PathVariable("courseId") Long courseId,
                                 @RequestParam(value = "teacherIds", required = false) List<Long> teacherIds,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!hasRole(currentUser(session), "ADMIN")) {
            return "redirect:/auth/login";
        }
        adminService.assignTeachers(courseId, teacherIds);
        redirectAttributes.addFlashAttribute("successMessage", "Teacher assignments updated.");
        return "redirect:/admin/courses";
    }

    @GetMapping("/users")
    public String users(Model model, HttpSession session) {
        if (!hasRole(currentUser(session), "ADMIN")) {
            return "redirect:/auth/login";
        }
        model.addAttribute("users", adminService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/surveys")
    public String surveys(Model model, HttpSession session) {
        if (!hasRole(currentUser(session), "ADMIN")) {
            return "redirect:/auth/login";
        }
        model.addAttribute("surveys", adminService.getAllSurveys());
        return "admin/surveys";
    }
}

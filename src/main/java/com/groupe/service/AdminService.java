package com.groupe.service;

import com.groupe.dao.CourseDao;
import com.groupe.dao.SurveyDao;
import com.groupe.dao.UserDao;
import com.groupe.model.Course;
import com.groupe.model.Survey;
import com.groupe.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserDao userDao;
    private final CourseDao courseDao;
    private final SurveyDao surveyDao;

    public AdminService(UserDao userDao, CourseDao courseDao, SurveyDao surveyDao) {
        this.userDao = userDao;
        this.courseDao = courseDao;
        this.surveyDao = surveyDao;
    }

    public List<User> getPendingTeachers() {
        return userDao.findPendingTeachers();
    }

    public List<User> getApprovedTeachers() {
        return userDao.findApprovedTeachers();
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public void approveTeacher(Long userId) {
        userDao.updateTeacherStatus(userId, "APPROVED");
    }

    public void rejectTeacher(Long userId) {
        userDao.updateTeacherStatus(userId, "REJECTED");
    }

    public List<Course> getCourses() {
        return courseDao.findAll();
    }

    public Optional<Course> getCourse(Long courseId) {
        return courseDao.findById(courseId);
    }

    public void saveCourse(Long courseId, String code, String name, String description) {
        if (isBlank(code) || isBlank(name)) {
            throw new IllegalArgumentException("Course code and name are required.");
        }

        Course course = new Course();
        course.setCode(code.trim());
        course.setName(name.trim());
        course.setDescription(description == null ? "" : description.trim());

        if (courseId == null) {
            courseDao.create(course);
            return;
        }

        course.setId(courseId);
        courseDao.update(course);
    }

    public void deleteCourse(Long courseId) {
        courseDao.delete(courseId);
    }

    public void assignTeachers(Long courseId, List<Long> teacherIds) {
        courseDao.replaceTeacherAssignments(courseId, teacherIds);
    }

    public List<Long> getAssignedTeacherIds(Long courseId) {
        return courseDao.findAssignedTeacherIds(courseId);
    }

    public List<Survey> getAllSurveys() {
        return surveyDao.findAll();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

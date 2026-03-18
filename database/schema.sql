CREATE DATABASE IF NOT EXISTS course_evaluation;
USE course_evaluation;

DROP TABLE IF EXISTS response_answers;
DROP TABLE IF EXISTS survey_responses;
DROP TABLE IF EXISTS respondents;
DROP TABLE IF EXISTS survey_options;
DROP TABLE IF EXISTS survey_questions;
DROP TABLE IF EXISTS surveys;
DROP TABLE IF EXISTS course_teachers;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(120) NOT NULL,
    username VARCHAR(60) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(40) NOT NULL,
    approval_status VARCHAR(20) NOT NULL DEFAULT 'APPROVED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE courses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT
);

CREATE TABLE course_teachers (
    course_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    PRIMARY KEY (course_id, teacher_id),
    CONSTRAINT fk_course_teachers_course
        FOREIGN KEY (course_id) REFERENCES courses(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_course_teachers_teacher
        FOREIGN KEY (teacher_id) REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE surveys (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    initiator_id BIGINT NOT NULL,
    title VARCHAR(180) NOT NULL,
    description TEXT,
    access_mode VARCHAR(30) NOT NULL,
    published BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_surveys_course
        FOREIGN KEY (course_id) REFERENCES courses(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_surveys_initiator
        FOREIGN KEY (initiator_id) REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE survey_questions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    survey_id BIGINT NOT NULL,
    question_text VARCHAR(255) NOT NULL,
    display_order INT NOT NULL,
    CONSTRAINT fk_survey_questions_survey
        FOREIGN KEY (survey_id) REFERENCES surveys(id)
        ON DELETE CASCADE
);

CREATE TABLE survey_options (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question_id BIGINT NOT NULL,
    option_text VARCHAR(255) NOT NULL,
    display_order INT NOT NULL,
    CONSTRAINT fk_survey_options_question
        FOREIGN KEY (question_id) REFERENCES survey_questions(id)
        ON DELETE CASCADE
);

CREATE TABLE respondents (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(120) NOT NULL,
    guest BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_respondents_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE SET NULL
);

CREATE TABLE survey_responses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    survey_id BIGINT NOT NULL,
    respondent_id BIGINT NOT NULL,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_survey_respondent UNIQUE (survey_id, respondent_id),
    CONSTRAINT fk_survey_responses_survey
        FOREIGN KEY (survey_id) REFERENCES surveys(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_survey_responses_respondent
        FOREIGN KEY (respondent_id) REFERENCES respondents(id)
        ON DELETE CASCADE
);

CREATE TABLE response_answers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    response_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    option_id BIGINT NOT NULL,
    CONSTRAINT fk_response_answers_response
        FOREIGN KEY (response_id) REFERENCES survey_responses(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_response_answers_question
        FOREIGN KEY (question_id) REFERENCES survey_questions(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_response_answers_option
        FOREIGN KEY (option_id) REFERENCES survey_options(id)
        ON DELETE CASCADE
);

INSERT INTO users (id, full_name, username, email, password_hash, role, approval_status) VALUES
    (1, 'Admin User', 'admin', 'admin@course.local', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN', 'APPROVED'),
    (2, 'Grace Initiator', 'initiator', 'initiator@course.local', '479cd2acb4728d078c0f03e4bbaae706cd92abff23c28fbe6bdcb717565931ea', 'SURVEY_INITIATOR', 'APPROVED'),
    (3, 'Peter Teacher', 'teacher', 'teacher@course.local', 'cde383eee8ee7a4400adf7a15f716f179a2eb97646b37e089eb8d6d04e663416', 'TEACHER', 'APPROVED'),
    (4, 'Student Demo', 'student', 'student@course.local', '6437e3435a52864d90ff204ff178feb5a540c05120f152a72f2445fbd43e236b', 'RESPONDENT', 'APPROVED'),
    (5, 'Pending Teacher', 'pending.teacher', 'pending.teacher@course.local', 'cde383eee8ee7a4400adf7a15f716f179a2eb97646b37e089eb8d6d04e663416', 'TEACHER', 'PENDING');

INSERT INTO courses (id, code, name, description) VALUES
    (1, 'CSC401', 'Web Application Development', 'Course on Spring MVC, JSP, and relational web application development.'),
    (2, 'EDU302', 'Instructional Design', 'Course focused on planning, delivering, and evaluating instruction.');

INSERT INTO course_teachers (course_id, teacher_id) VALUES
    (1, 3),
    (2, 3);

INSERT INTO surveys (id, course_id, initiator_id, title, description, access_mode, published) VALUES
    (1, 1, 2, 'End of Term Course Evaluation', 'Share your feedback on course content, pace, and teaching effectiveness.', 'GUEST_ALLOWED', TRUE),
    (2, 2, 2, 'Teaching Methods Feedback', 'This survey is limited to authenticated respondents for structured program review.', 'AUTHENTICATED', TRUE);

INSERT INTO survey_questions (id, survey_id, question_text, display_order) VALUES
    (1, 1, 'How clear was the course content throughout the term?', 1),
    (2, 1, 'How appropriate was the pace of teaching?', 2),
    (3, 2, 'How helpful were the learning materials provided?', 1),
    (4, 2, 'How engaging was the teacher during class sessions?', 2);

INSERT INTO survey_options (id, question_id, option_text, display_order) VALUES
    (1, 1, 'Excellent', 1),
    (2, 1, 'Good', 2),
    (3, 1, 'Fair', 3),
    (4, 1, 'Poor', 4),
    (5, 2, 'Too fast', 1),
    (6, 2, 'Balanced', 2),
    (7, 2, 'Too slow', 3),
    (8, 2, 'Very inconsistent', 4),
    (9, 3, 'Very helpful', 1),
    (10, 3, 'Helpful', 2),
    (11, 3, 'Needs improvement', 3),
    (12, 3, 'Not helpful', 4),
    (13, 4, 'Very engaging', 1),
    (14, 4, 'Engaging', 2),
    (15, 4, 'Average', 3),
    (16, 4, 'Needs improvement', 4);

INSERT INTO respondents (id, user_id, name, email, guest) VALUES
    (1, 4, 'Student Demo', 'student@course.local', FALSE),
    (2, NULL, 'Alice Guest', 'alice.guest@example.com', TRUE),
    (3, NULL, 'Ben Guest', 'ben.guest@example.com', TRUE),
    (4, NULL, 'Chantal Guest', 'chantal.guest@example.com', TRUE);

INSERT INTO survey_responses (id, survey_id, respondent_id) VALUES
    (1, 1, 2),
    (2, 1, 3),
    (3, 1, 4);

INSERT INTO response_answers (response_id, question_id, option_id) VALUES
    (1, 1, 1),
    (1, 2, 6),
    (2, 1, 2),
    (2, 2, 6),
    (3, 1, 2),
    (3, 2, 7);

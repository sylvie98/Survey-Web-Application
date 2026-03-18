# Course Evaluation Survey System

Course Evaluation Survey System is a Spring MVC web application built with JSP, JSTL, and MySQL for collecting course feedback from authenticated respondents or guests.

## Features

- Teacher self-registration with administrator approval
- Course management and teacher assignment
- Survey creation, publication, editing, and deletion
- Question and option management per survey
- Respondent participation with guest or authenticated access modes
- Email confirmation flow after submission
- Survey result summaries for initiators and teachers

## Technology Stack

- Java 17+
- Spring MVC 6
- Spring JDBC
- JSP + JSTL
- MySQL 8
- Maven WAR packaging

## Project Structure

- `src/main/java/com/groupe/controller`: MVC controllers
- `src/main/java/com/groupe/service`: business logic
- `src/main/java/com/groupe/dao`: JDBC data access
- `src/main/java/com/groupe/model`: domain models
- `src/main/resources/application.properties`: database and email configuration
- `src/main/webapp/WEB-INF/views`: JSP views
- `database/schema.sql`: MySQL schema and seed data
- `docs/PROJECT_DOCUMENTATION.md`: submission-ready documentation

## Setup

1. Create the database objects by running `database/schema.sql` in MySQL.
2. Update `src/main/resources/application.properties` with your database credentials.
3. Optionally configure SMTP settings in `application.properties` for real email delivery.
4. Build the WAR with `mvn clean package`.
5. Deploy `target/course-evaluation.war` to a Jakarta-compatible servlet container such as Tomcat 10.1+.

## Demo Accounts

- Administrator: `admin` / `admin123`
- Survey Initiator: `initiator` / `initiator123`
- Teacher: `teacher` / `teacher123`
- Respondent: `student` / `respondent123`
- Pending teacher example: `pending.teacher` / `teacher123`

## Submission Notes

- Public repository: add your GitHub repository URL here before submission.
- YouTube video: add your presentation link here before submission.
- Documentation: see [docs/PROJECT_DOCUMENTATION.md](docs/PROJECT_DOCUMENTATION.md).

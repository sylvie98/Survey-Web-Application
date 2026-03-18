package com.groupe.service;

import com.groupe.model.Survey;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${mail.host:}")
    private String host;

    @Value("${mail.port:587}")
    private Integer port;

    @Value("${mail.username:}")
    private String username;

    @Value("${mail.password:}")
    private String password;

    @Value("${mail.from:no-reply@course-evaluation.local}")
    private String fromAddress;

    @Value("${mail.auth:true}")
    private boolean authEnabled;

    @Value("${mail.starttls:true}")
    private boolean startTlsEnabled;

    public String sendSubmissionConfirmation(String email, String respondentName, Survey survey) {
        if (email == null || email.trim().isEmpty()) {
            return "No email address was available, so no confirmation was sent.";
        }

        if (host == null || host.trim().isEmpty()) {
            System.out.printf(
                    "Email preview for %s: Thank you %s for completing survey '%s'.%n",
                    email,
                    respondentName,
                    survey.getTitle());
            return "SMTP is not configured yet, so a preview was written to the server log instead.";
        }

        try {
            sendEmail(email.trim(), respondentName, survey);
            return "A confirmation email was sent to " + email.trim() + ".";
        } catch (MessagingException exception) {
            System.err.println("Email delivery failed: " + exception.getMessage());
            return "The survey was saved, but the email could not be delivered.";
        }
    }

    private void sendEmail(String email, String respondentName, Survey survey) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", String.valueOf(port));
        properties.put("mail.smtp.auth", String.valueOf(authEnabled));
        properties.put("mail.smtp.starttls.enable", String.valueOf(startTlsEnabled));

        Session session;
        if (authEnabled && username != null && !username.isBlank()) {
            session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        } else {
            session = Session.getInstance(properties);
        }

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromAddress));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        message.setSubject("Course evaluation confirmation");
        message.setText("Hello " + respondentName + ",\n\n"
                + "Thank you for completing the survey \"" + survey.getTitle() + "\" for "
                + survey.getCourseCode() + " - " + survey.getCourseName() + ".\n\n"
                + "Your response has been recorded successfully.\n\n"
                + "Course Evaluation Survey System");
        Transport.send(message);
    }
}

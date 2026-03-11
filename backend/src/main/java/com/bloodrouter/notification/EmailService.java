package com.bloodrouter.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private NotificationRepository notificationRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base-url}")
    private String baseUrl;

    public Notification sendBloodRequestNotification(String toEmail, String donorName,
                                                     String hospitalName, String bloodType,
                                                     String urgency, Long requestId) {

        Notification notification = new Notification();
        notification.setType("EMAIL");
        notification.setRecipient(toEmail);
        notification.setTitle("Urgent Blood Request");
        notification.setUser(null); // Will be set by caller if needed

        try {
            // Create HTML email content using template
            Context context = new Context();
            context.setVariable("donorName", donorName);
            context.setVariable("hospitalName", hospitalName);
            context.setVariable("bloodType", bloodType);
            context.setVariable("urgency", urgency);
            context.setVariable("responseUrl", baseUrl + "/api/blood-requests/" + requestId + "/respond");

            String htmlContent = templateEngine.process("blood-request-email", context);

            // Send email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Urgent Blood Request - " + bloodType + " Needed");
            helper.setText(htmlContent, true);

            mailSender.send(message);

            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
            notification.setContent("Blood request notification sent for " + bloodType);

        } catch (Exception e) {
            notification.setStatus("FAILED");
            notification.setErrorMessage(e.getMessage());
            e.printStackTrace();
        }

        return notificationRepository.save(notification);
    }

    public Notification sendDonorResponseNotification(String toEmail, String hospitalName,
                                                      String donorName, String status,
                                                      Long requestId) {

        Notification notification = new Notification();
        notification.setType("EMAIL");
        notification.setRecipient(toEmail);
        notification.setTitle("Donor Response Update");
        notification.setUser(null); // Will be set by caller if needed

        try {
            Context context = new Context();
            context.setVariable("hospitalName", hospitalName);
            context.setVariable("donorName", donorName);
            context.setVariable("status", status);
            context.setVariable("requestUrl", baseUrl + "/api/blood-requests/" + requestId);

            String htmlContent = templateEngine.process("donor-response-email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Donor " + status + " - Blood Request Update");
            helper.setText(htmlContent, true);

            mailSender.send(message);

            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
            notification.setContent("Donor response notification sent for request #" + requestId);

        } catch (Exception e) {
            notification.setStatus("FAILED");
            notification.setErrorMessage(e.getMessage());
            e.printStackTrace();
        }

        return notificationRepository.save(notification);
    }
}
package com.bloodrouter.notification;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;

@Service
public class SmsService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    @Autowired
    private NotificationRepository notificationRepository;

    @Value("${app.base-url}")
    private String appBaseUrl;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public Notification sendBloodRequestSms(String toPhoneNumber, String donorName,
                                            String hospitalName, String bloodType,
                                            String urgency, Long requestId) {

        Notification notification = new Notification();
        notification.setType("SMS");
        notification.setRecipient(toPhoneNumber);
        notification.setTitle("Urgent Blood Request");
        notification.setUser(null); // Will be set by caller if needed

        try {
            String messageContent = String.format(
                    "URGENT: %s needs %s blood! Please respond: %s/api/blood-requests/%d/respond",
                    hospitalName, bloodType, appBaseUrl, requestId
            );

            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(twilioPhoneNumber),
                    messageContent
            ).create();

            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
            notification.setContent(messageContent);

        } catch (Exception e) {
            notification.setStatus("FAILED");
            notification.setErrorMessage(e.getMessage());
            e.printStackTrace();
        }

        return notificationRepository.save(notification);
    }

    public Notification sendDonorResponseSms(String toPhoneNumber, String hospitalName,
                                             String donorName, String status,
                                             Long requestId) {

        Notification notification = new Notification();
        notification.setType("SMS");
        notification.setRecipient(toPhoneNumber);
        notification.setTitle("Donor Response");
        notification.setUser(null); // Will be set by caller if needed

        try {
            String messageContent = String.format(
                    "Donor %s has %s your blood request. View details: %s/api/blood-requests/%d",
                    donorName, status, appBaseUrl, requestId
            );

            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(twilioPhoneNumber),
                    messageContent
            ).create();

            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
            notification.setContent(messageContent);

        } catch (Exception e) {
            notification.setStatus("FAILED");
            notification.setErrorMessage(e.getMessage());
            e.printStackTrace();
        }

        return notificationRepository.save(notification);
    }
}
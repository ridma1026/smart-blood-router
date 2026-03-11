package com.bloodrouter.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Send notification to a specific user
    public void sendToUser(Long userId, String destination, Object payload) {
        String userDestination = "/queue/" + destination;
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                userDestination,
                payload
        );
    }

    // Send notification to all connected clients (broadcast)
    public void sendToAll(String destination, Object payload) {
        messagingTemplate.convertAndSend("/topic/" + destination, payload);
    }

    // Send blood request update to a hospital
    public void sendBloodRequestUpdate(Long hospitalId, Object requestData) {
        sendToUser(hospitalId, "blood-request-updates", requestData);
    }

    // Send donor response to hospital
    public void sendDonorResponse(Long hospitalId, Object responseData) {
        sendToUser(hospitalId, "donor-responses", responseData);
    }

    // Send notification to donor
    public void sendDonorNotification(Long donorId, Object notificationData) {
        sendToUser(donorId, "notifications", notificationData);
    }

    // Broadcast urgent request to all connected donors
    public void broadcastUrgentRequest(Object requestData) {
        sendToAll("urgent-requests", requestData);
    }
}
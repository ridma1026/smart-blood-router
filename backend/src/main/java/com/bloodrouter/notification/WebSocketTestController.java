package com.bloodrouter.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class WebSocketTestController {

    @Autowired
    private WebSocketService webSocketService;

    // Test endpoint - client sends message to /app/test
    @MessageMapping("/test")
    @SendTo("/topic/test")
    public Map<String, Object> testWebSocket(String message, SimpMessageHeaderAccessor headerAccessor) {
        return Map.of(
                "message", "Server received: " + message,
                "time", LocalDateTime.now().toString(),
                "sessionId", headerAccessor.getSessionId()
        );
    }

    // Test endpoint for user-specific messages
    @MessageMapping("/private-test")
    public void privateTest(String message, SimpMessageHeaderAccessor headerAccessor) {
        String userId = headerAccessor.getUser() != null ?
                headerAccessor.getUser().getName() : "anonymous";

        webSocketService.sendToUser(
                Long.parseLong(userId),
                "test-responses",
                Map.of(
                        "message", "Private response to: " + message,
                        "time", LocalDateTime.now().toString()
                )
        );
    }
}
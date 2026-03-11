package com.bloodrouter.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndStatus(Long userId, String status);

    List<Notification> findByStatus(String status);

    long countByUserIdAndStatus(Long userId, String status);
}
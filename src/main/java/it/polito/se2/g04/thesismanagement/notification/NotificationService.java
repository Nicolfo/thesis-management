package it.polito.se2.g04.thesismanagement.notification;

import java.util.List;

public interface NotificationService {
    List<NotificationDTO> getAllNotificationsForLoggedInUser();
    List<NotificationDTO> getAllNotificationsByEmail(String email);
    NotificationDTO markNotificationAsRead(Long id);
    int unreadNotificationsForLoggedInUserCount();
}

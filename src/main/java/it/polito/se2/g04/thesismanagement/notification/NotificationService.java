package it.polito.se2.g04.thesismanagement.notification;

import java.util.List;

public interface NotificationService {
    List<Notification> getAllNotificationsForLoggedInUser();
    List<Notification> getAllNotificationsByEmail(String email);
}

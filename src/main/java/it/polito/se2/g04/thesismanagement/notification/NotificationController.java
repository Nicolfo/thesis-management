package it.polito.se2.g04.thesismanagement.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    @GetMapping("/API/notification/getAllNotificationsOfCurrentUser/")
    @PreAuthorize("isAuthenticated()")
    public List<Notification> getAllNotificationsOfCurrentUser() {
        return notificationService.getAllNotificationsForLoggedInUser();
    }

    @GetMapping("/API/notification/getSingleNotifications/{id}")
    @PreAuthorize("isAuthenticated()")
    public Notification getSingleNotifications(@PathVariable Long id) {
        return notificationService.markNotificationAsRead(id);
    }
}

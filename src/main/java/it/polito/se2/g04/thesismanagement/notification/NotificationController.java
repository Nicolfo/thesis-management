package it.polito.se2.g04.thesismanagement.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    @GetMapping("/API/notification/getAllNotificationsOfCurrentUser/")
    @PreAuthorize("isAuthenticated()")
    public List<NotificationDTO> getAllNotificationsOfCurrentUser() {
        return notificationService.getAllNotificationsForLoggedInUser();
    }

    @GetMapping("/API/notification/getSingleNotifications/{id}")
    @PreAuthorize("isAuthenticated()")
    public NotificationDTO getSingleNotifications(@PathVariable Long id) {
        return notificationService.markNotificationAsRead(id);
    }

    @PutMapping("/API/notification/markNotificationAsRead/{id}")
    @PreAuthorize("isAuthenticated()")
    public void markNotificationAsRead(@PathVariable Long id) {
        notificationService.markNotificationAsRead(id);
    }

    @GetMapping("/API/notification/getUnreadNotificationsCount/")
    @PreAuthorize("isAuthenticated()")
    public int getUnreadNotificationsCount() {
        return notificationService.unreadNotificationsForLoggedInUserCount();
    }
}

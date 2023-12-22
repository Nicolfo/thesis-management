package it.polito.se2.g04.thesismanagement.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService{
    @Autowired
    private NotificationRepository notificationRepository;


    @Override
    public List<NotificationDTO> getAllNotificationsForLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return getAllNotificationsByEmail(email);
    }

    @Override
    public List<NotificationDTO> getAllNotificationsByEmail(String email){
        return notificationRepository.findByRecipient(email).stream().map(NotificationDTO::fromNotification).toList();
    }

    @Override
    public NotificationDTO markNotificationAsRead(Long id) {
        Notification notification = notificationRepository.getReferenceById(id);
        notification.setRead(true);
        return NotificationDTO.fromNotification(notificationRepository.save(notification));
    }

    @Override
    public int unreadNotificationsForLoggedInUserCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return notificationRepository.countByRecipientAndReadFalse(email);
    }
}

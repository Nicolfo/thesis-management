package it.polito.se2.g04.thesismanagement.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String recipient;
    private String subject;
    private String title;
    private String text;
    private String icon;
    private boolean sent;
    private boolean read;
    private int sendTriedCounter = 0;
    private Date timestamp;

    public static NotificationDTO fromNotification(Notification notification) {
        if (notification == null)
            return null;
        return new NotificationDTO(
                notification.getId(),
                notification.getRecipient(),
                notification.getSubject(),
                notification.getTitle(),
                notification.getText(),
                notification.getIcon(),
                notification.isSent(),
                notification.isRead(),
                notification.getSendTriedCounter(),
                notification.getTimestamp()
        );
    }
}

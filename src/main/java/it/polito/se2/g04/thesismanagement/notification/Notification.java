package it.polito.se2.g04.thesismanagement.notification;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue
    private Long id;

    private String recipient;
    private String subject;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String text;
    private String icon;
    private boolean sent;
    private boolean read;
    private int sendTriedCounter = 0;
    private Date timestamp;

    public Notification(String recipient, String subject, String title, String text, String icon, Date timestamp) {
    this.recipient = recipient;
    this.subject = subject;
    this.title = title;
    this.text = text;
    this.icon = icon;
    this.timestamp = timestamp;
    this.sent = false;
    this.read = false;
    }
}

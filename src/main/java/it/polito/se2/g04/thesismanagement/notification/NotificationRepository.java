package it.polito.se2.g04.thesismanagement.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findBySentAndSendTriedCounterLessThan(boolean sent, int sendTriedCounter);
    List<Notification> findByRecipient(String recipient);
}

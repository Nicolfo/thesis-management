package it.polito.se2.g04.thesismanagement.email;

import it.polito.se2.g04.thesismanagement.application.Application;
import org.springframework.mail.SimpleMailMessage;

public interface EmailService {
    void sendEmail(SimpleMailMessage message);
    void notifyStudentOfApplicationDecision(Application application);
    void notifySupervisorOfNewApplication(Application application);
    void notifyCoSupervisorsOfNewApplication(Application application);
    void notifySupervisorAndCoSupervisorsOfNewApplication(Application application);
}

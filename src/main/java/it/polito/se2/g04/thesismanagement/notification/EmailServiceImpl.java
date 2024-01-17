package it.polito.se2.g04.thesismanagement.notification;

import it.polito.se2.g04.thesismanagement.application.Application;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.proposal.ProposalDTO;
import it.polito.se2.g04.thesismanagement.proposal_on_request.ProposalOnRequest;
import it.polito.se2.g04.thesismanagement.student.Student;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import it.polito.se2.g04.thesismanagement.teacher.TeacherRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.core.io.Resource;

import java.io.InputStream;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final ApplicationContext context;

    private final JavaMailSender mailSender;

    private final ResourceLoader resourceLoader;

    private final TeacherRepository teacherRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public void notifyStudentOfApplicationDecision(Application application) {
        Student student = application.getStudent();


        String emailText = EmailConstants.GREETING_FORMULA + " " + student.getName() + " " + student.getSurname() + ", " + EmailConstants.HTML_LINE_BREAK +
                EmailConstants.HTML_LINE_BREAK +
                "The status of your application from " + (new SimpleDateFormat("dd-MM-yyyy")).format(application.getApplyDate()) + " to the thesis proposal \"" + application.getProposal().getTitle() + "\" has been updated and now has the status: " + application.getStatus() + EmailConstants.HTML_LINE_BREAK +
                "Log in to the Thesis Management Portal to view further details.";
        String title = "Your application has been ";
        String icon = "";
        switch (application.getStatus()) {
            case ACCEPTED -> {
                title += "accepted";
                icon = "hook.png";
            }
            case REJECTED -> {
                title += "rejected";
                icon = "cross.png";
            }
            case PENDING -> {
                title += "set to pending";
                icon = "edit.png";
            }
            case CANCELLED -> {
                title += "set to cancelled, because another application has been accepted for this proposal";
                icon = "cancelled.png";
            }
        }


        createNewNotification(student.getEmail(), "One of your applications has been updated", title, emailText, icon);
    }

    @Override
    public void notifySupervisorOfNewApplication(Application application) {
        Teacher teacher = application.getProposal().getSupervisor();
        String emailText = EmailConstants.GREETING_FORMULA + " " + teacher.getName() + " " + teacher.getSurname() + ", " + EmailConstants.HTML_LINE_BREAK +
                EmailConstants.HTML_LINE_BREAK +
                EmailConstants.NEW_APPLICATION_RECEIVED + " for the thesis proposal \"" + application.getProposal().getTitle() + "\" for which you are assigned as supervisor." + EmailConstants.HTML_LINE_BREAK +
                "Log in to the Thesis Management Portal to see further details and to accept or reject the application.";

        createNewNotification(teacher.getEmail(), EmailConstants.NEW_APPLICATION_RECEIVED, EmailConstants.NEW_APPLICATION_RECEIVED, emailText, EmailConstants.HTML_ADD_ICON);
    }


    @Override
    public void notifySupervisorAndCoSupervisorsOfNewThesisRequest(ProposalOnRequest request) {
        notifySupervisorOfNewThesisRequest(request);
        notifyCoSupervisorsOfNewThesisRequest(request);
    }

    @Override
    public void notifyCoSupervisorsOfNewProposal(ProposalDTO proposalDTO, List<Teacher> oldSupervisors) {
        String emailText = EmailConstants.HTML_LINE_BREAK +
                "You have been assigned to the proposal \"" + proposalDTO.getTitle() + "\" as co supervisor." + EmailConstants.HTML_LINE_BREAK +
                "Log in to the Thesis Management Portal to see further details.";

        for (Long teacherId : proposalDTO.getCoSupervisors()) {
            if (oldSupervisors == null || oldSupervisors.stream().noneMatch(t -> t.getId().equals(teacherId))) {
                Teacher teacher = teacherRepository.getReferenceById(teacherId);
                createNewNotification(teacher.getEmail(), "You have been assigned to a proposal", "You have been assigned to a proposal", EmailConstants.GREETING_FORMULA + " " + teacher.getName() + " " + teacher.getSurname() + ", " + EmailConstants.HTML_LINE_BREAK + emailText, EmailConstants.HTML_ADD_ICON);
            }
        }
    }

    @Override
    public void notifyCoSupervisorsOfNewThesisRequest(ProposalOnRequest request) {
        String emailText = EmailConstants.HTML_LINE_BREAK +
                "A new proposal on request has been received with the title \"" + request.getTitle() + EmailConstants.CO_SUPERVISOR_ASSIGNED + EmailConstants.HTML_LINE_BREAK +
                "Log in to the Thesis Management Portal to see further details.";

        for (Teacher teacher : request.getCoSupervisors()) {
            createNewNotification(teacher.getEmail(), "A new proposal on request has been received", "A new proposal on request has been received", EmailConstants.GREETING_FORMULA + " " + teacher.getName() + " " + teacher.getSurname() + ", " + EmailConstants.HTML_LINE_BREAK + emailText, EmailConstants.HTML_ADD_ICON);
        }
    }

    @Override
    public void notifySupervisorOfNewThesisRequest(ProposalOnRequest request) {
        Teacher teacher = request.getSupervisor();
        String emailText = EmailConstants.GREETING_FORMULA + " " + teacher.getName() + " " + teacher.getSurname() + ", " + EmailConstants.HTML_LINE_BREAK +
                EmailConstants.HTML_LINE_BREAK +
                "The secretariat has approved a new thesis request with you as supervisor and the title \"" + request.getTitle() + "\"." + EmailConstants.HTML_LINE_BREAK +
                "Description of the thesis request:" + EmailConstants.HTML_LINE_BREAK +
                request.getDescription() + EmailConstants.HTML_LINE_BREAK + EmailConstants.HTML_LINE_BREAK +
                "Please log in to the Thesis management system to accept or reject this thesis request.";

        createNewNotification(teacher.getEmail(), "A new thesis request has been received", "A new thesis request has been received", emailText, EmailConstants.HTML_ADD_ICON);
    }

    @Override
    public void notifySupervisorOfExpiration(Proposal proposal) {
        Teacher teacher = proposal.getSupervisor();
        String emailText = EmailConstants.GREETING_FORMULA + " " + teacher.getName() + " " + teacher.getSurname() + ", " + EmailConstants.HTML_LINE_BREAK +
                EmailConstants.HTML_LINE_BREAK +
                "the proposal \"" + proposal.getTitle() + "\" for which you are assigned as supervisor will expire on " + (new SimpleDateFormat("dd-MM-yyyy")).format(proposal.getExpiration()) + "." + EmailConstants.HTML_LINE_BREAK +
                "If you do not take any further action, the proposal will be automatically archived on that date.";

        createNewNotification(teacher.getEmail(), "One of you proposals will expire soon", "One of you proposals will expire soon", emailText, "warning.png");
    }


    @Override
    public void notifyCoSupervisorsOfNewApplication(Application application) {
        String emailText = EmailConstants.HTML_LINE_BREAK +
                EmailConstants.NEW_APPLICATION_RECEIVED + " for the thesis proposal \"" + application.getProposal().getTitle() + EmailConstants.CO_SUPERVISOR_ASSIGNED + EmailConstants.HTML_LINE_BREAK +
                "Log in to the Thesis Management Portal to see further details and to accept or reject the application.";

        for (Teacher teacher : application.getProposal().getCoSupervisors()) {
            createNewNotification(teacher.getEmail(), EmailConstants.NEW_APPLICATION_RECEIVED, EmailConstants.NEW_APPLICATION_RECEIVED, EmailConstants.GREETING_FORMULA + " " + teacher.getName() + " " + teacher.getSurname() + ", " + EmailConstants.HTML_LINE_BREAK + emailText, EmailConstants.HTML_ADD_ICON);
        }
    }


    @Override
    public void notifySupervisorAndCoSupervisorsOfNewApplication(Application application) {
        notifySupervisorOfNewApplication(application);
        notifyCoSupervisorsOfNewApplication(application);
    }

    @Override
    public void notifyCoSupervisorsOfDecisionOnApplication(Application application) {
        String status = "";
        String icon = "";
        switch (application.getStatus()) {
            case ACCEPTED -> {
                status = "accepted";
                icon = "hook.png";
            }
            case REJECTED -> {
                status = "rejected";
                icon = "cross.png";
            }
            case PENDING -> {
                status = "set to pending";
                icon = "edit.png";
            }
            case CANCELLED -> {
                status = "set to cancelled";
                icon = "cancelled.png";
            }
        }

        String emailText = EmailConstants.HTML_LINE_BREAK +
                "A new decision was made on an application for the thesis proposal \"" + application.getProposal().getTitle() + EmailConstants.CO_SUPERVISOR_ASSIGNED + EmailConstants.HTML_LINE_BREAK +
                "The application is submitted by " + application.getStudent().getSurname() + " " + application.getStudent().getName() + " and was " + status +
                " Log in to the Thesis Management Portal to take further action.";

        for (Teacher teacher : application.getProposal().getCoSupervisors()) {
            createNewNotification(teacher.getEmail(), "A decision on an application was taken", "A decision on an application was taken", EmailConstants.GREETING_FORMULA + " " + teacher.getName() + " " + teacher.getSurname() + ", " + EmailConstants.HTML_LINE_BREAK + emailText, icon);
        }
    }


    void createNewNotification(String recipient, String subject, String title, String text, String icon) {
        Notification newNotification = new Notification(recipient, subject, title, text, icon, new Date());
        try {
            newNotification.setSendTriedCounter(0);
            EmailServiceImpl self = context.getBean(EmailServiceImpl.class);
            self.sendNotification(newNotification);
        } catch (Exception ignore) {

        }
    }

    @Async
    public void sendNotification(Notification notification) throws IOException {
        MimeMessage message = mailSender.createMimeMessage();

        Resource resource = resourceLoader.getResource("classpath:/email/template.html");
        InputStream inputStream = resource.getInputStream();
        String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        content = content.replace("%%varText1%%", notification.getTitle());
        content = content.replace("%%varText2%%", notification.getText());
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(notification.getRecipient());
            helper.setSubject(notification.getSubject());
            helper.setText(content, true);
            helper.addInline("logo", new ClassPathResource("/email/images/polito-logo.png"));
            helper.addInline("icon1", new ClassPathResource("/email/images/" + notification.getIcon()));
            mailSender.send(message);
            notification.setSent(true);
        } catch (Exception messagingException) {
            notification.setSendTriedCounter(notification.getSendTriedCounter() + 1);
        }
        notificationRepository.save(notification);
    }

    @Scheduled(fixedRate = 15 * 10 * 1000)
    @Transactional
    public void sendQueuedEmails() {
        List<Notification> notSentEmailsList = notificationRepository.findBySentAndSendTriedCounterLessThan(false, 5);
        for (Notification notification : notSentEmailsList) {
            try {
                EmailServiceImpl self = context.getBean(EmailServiceImpl.class);
                self.sendNotification(notification);
            } catch (Exception ignored) {
            }
        }
    }
}
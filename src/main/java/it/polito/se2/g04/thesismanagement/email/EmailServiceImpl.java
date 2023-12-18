package it.polito.se2.g04.thesismanagement.email;

import it.polito.se2.g04.thesismanagement.application.Application;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.proposal_on_request.ProposalOnRequest;
import it.polito.se2.g04.thesismanagement.student.Student;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import it.polito.se2.g04.thesismanagement.teacher.TeacherRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.core.io.Resource;

import java.io.InputStream;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.nio.charset.StandardCharsets;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    private final ResourceLoader resourceLoader;

    private final TeacherRepository teacherRepository;

    @Override
    @Async
    public void notifyStudentOfApplicationDecision(Application application) throws MessagingException, IOException {
        Student student = application.getStudent();


        String emailText = EmailConstants.GREETING_FORMULA + " " + student.getName() + " " + student.getSurname() + ", <br>" +
                "<br>" +
                "The status of your application from " + (new SimpleDateFormat("dd-MM-yyyy")).format(application.getApplyDate()) + " to the thesis proposal \"" + application.getProposal().getTitle() + "\" has been updated and now has the status: " + application.getStatus() + "<br>" +
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



        emailSendHelper(student.getEmail(), "One of your applications has been updated", title, emailText, icon);
    }

    @Override
    @Async
    public void notifySupervisorOfNewApplication(Application application) throws MessagingException, IOException {
        Teacher teacher = application.getProposal().getSupervisor();
        String emailText = EmailConstants.GREETING_FORMULA + " " + teacher.getName() + " " + teacher.getSurname() + ", <br>" +
                "<br>" +
                "A new application has been received for the thesis proposal \"" + application.getProposal().getTitle() + "\" for which you are assigned as supervisor.<br>" +
                "Log in to the Thesis Management Portal to see further details and to accept or reject the application.";

        emailSendHelper(teacher.getEmail(), "A new application has been received", "A new application has been received", emailText, "new.png");
    }

    @Override
    @Async
    public void notifySupervisorOfNewThesisRequest(ProposalOnRequest request) throws MessagingException, IOException {
        Teacher teacher = request.getSupervisor();
        String emailText = EmailConstants.GREETING_FORMULA + " " + teacher.getName() + " " + teacher.getSurname() + ", <br>" +
                "<br>" +
                "The secretariat has approved a new thesis request with you as supervisor and the title \"" + request.getTitle() + "\".<br>" +
                "Description of the thesis request:<br>" +
                request.getDescription() + "<br><br>" +
                "Please log in to the Thesis management system to accept or reject this thesis request.";

        emailSendHelper(teacher.getEmail(), "A new thesis request has been received", "A new thesis request has been received", emailText, "new.png");
    }

    @Override
    @Async
    public void notifySupervisorOfExpiration(Proposal proposal) throws MessagingException, IOException {
        Teacher teacher = proposal.getSupervisor();
        String emailText = EmailConstants.GREETING_FORMULA + " " + teacher.getName() + " " + teacher.getSurname() + ", <br>" +
                "<br>" +
                "the proposal \"" + proposal.getTitle() + "\" for which you are assigned as supervisor will expire on " + (new SimpleDateFormat("dd-MM-yyyy")).format(proposal.getExpiration()) + ".<br>" +
                "If you don't take any further action, the proposal will be automatically archived on that date.";

        emailSendHelper(teacher.getEmail(), "One of you proposals will expire soon", "One of you proposals will expire soon", emailText, "warning.png");
    }


    @Override
    @Async
    public void notifyCoSupervisorsOfNewApplication(Application application) throws MessagingException, IOException {
        String emailText = "<br>" +
                "A new application has been received for the thesis proposal \"" + application.getProposal().getTitle() + "\" for which you are assigned as co-supervisor.<br>" +
                "Log in to the Thesis Management Portal to see further details and to accept or reject the application.";

        for (Teacher teacher : application.getProposal().getCoSupervisors()) {
            emailSendHelper(teacher.getEmail(), "A new application has been received", "A new application has been received", EmailConstants.GREETING_FORMULA + " " + teacher.getName() + " " + teacher.getSurname() + ", <br>" + emailText, "new.png");
        }
    }

    @Override
    @Async
    public void notifySupervisorAndCoSupervisorsOfNewApplication(Application application) throws MessagingException, IOException {
        notifySupervisorOfNewApplication(application);
        notifyCoSupervisorsOfNewApplication(application);
    }


    void emailSendHelper(String recipient, String subject, String title, String text, String icon) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        Resource resource = resourceLoader.getResource("classpath:/email/template.html");
        InputStream inputStream = resource.getInputStream();
        String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        content = content.replace("%%varText1%%", title);
        content = content.replace("%%varText2%%", text);
        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(content, true);
        helper.addInline("logo", new ClassPathResource("/email/images/polito-logo.png"));
        helper.addInline("icon1", new ClassPathResource("/email/images/" + icon));
        mailSender.send(message);
    }
}
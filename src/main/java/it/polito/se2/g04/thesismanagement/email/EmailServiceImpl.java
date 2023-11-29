package it.polito.se2.g04.thesismanagement.email;

import it.polito.se2.g04.thesismanagement.application.Application;
import it.polito.se2.g04.thesismanagement.student.Student;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

import java.text.SimpleDateFormat;


@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(SimpleMailMessage message) {
        mailSender.send(message);
    }

    @Override
    public void notifyStudentOfApplicationDecision(Application application) {
        Student student = application.getStudent();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(student.getEmail());
        message.setSubject("One of your applications has been updated");
        message.setText("Dear " + student.getName() + " " + student.getSurname() + ", \n" +
                "\n" +
                "The status of your application from " + (new SimpleDateFormat("dd-MM-yyyy")).format(application.getApplyDate()) + " to the thesis proposal \"" + application.getProposal().getTitle() + "\" has been updated and now has the status: " + application.getStatus() + "\n" +
                "Log in to the Thesis Management Portal to view further details.\n" +
                "\n" +
                "Yours sincerely\n" +
                "Thesis management team of Politecnico di Torino");
        sendEmail(message);
    }

    @Override
    public void notifySupervisorOfNewApplication(Application application) {
        Teacher teacher = application.getProposal().getSupervisor();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(teacher.getEmail());
        message.setSubject("A new application has been received.");
        message.setText("Dear " + teacher.getName() + " " + teacher.getSurname() + ", \n" +
                "\n" +
                "A new application has been received for the thesis proposal \"" + application.getProposal().getTitle() + "\" for which you are assigned as supervisor.\n" +
                "Log in to the Thesis Management Portal to see further details and to accept or reject the application.\n" +
                "\n" +
                "Yours sincerely\n" +
                "Thesis management team of Politecnico di Torino");
        sendEmail(message);
    }

    @Override
    public void notifyCoSupervisorsOfNewApplication(Application application) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("A new application has been received.");
        String emailText = "\n" +
                "A new application has been received for the thesis proposal \"" + application.getProposal().getTitle() + "\" for which you are assigned as co-supervisor.\n" +
                "Log in to the Thesis Management Portal to see further details and to accept or reject the application.\n" +
                "\n" +
                "Yours sincerely\n" +
                "Thesis management team of Politecnico di Torino";

        for (Teacher teacher : application.getProposal().getCoSupervisors()) {
            message.setTo(teacher.getEmail());
            message.setText("Dear " + teacher.getName() + " " + teacher.getSurname() + ", \n" + emailText);
            sendEmail(message);
        }
    }

    @Override
    public void notifySupervisorAndCoSupervisorsOfNewApplication(Application application){
        notifySupervisorOfNewApplication(application);
        notifyCoSupervisorsOfNewApplication(application);
    }

}
package it.polito.se2.g04.thesismanagement.email;

import it.polito.se2.g04.thesismanagement.application.Application;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.proposalOnRequest.ProposalOnRequest;
import it.polito.se2.g04.thesismanagement.proposalOnRequest.ProposalOnRequestDTO;
import jakarta.mail.MessagingException;

import java.io.IOException;

public interface EmailService {

    /**
     * This method sends an email to a student, who handed in the given application, to inform him/her about
     * the current status
     *
     * @param application Application from which the applying student should receive a status email
     */
    void notifyStudentOfApplicationDecision(Application application) throws MessagingException, IOException;

    /**
     * This method sends an email to the professor being set as supervisor of the proposal of the given application,
     * informing him/her about the application
     * @param application Application about which the supervisor of the corresponding proposal should be informed
     */
    void notifySupervisorOfNewApplication(Application application) throws MessagingException, IOException;

    /**
     * This method sends an email to all professors being set as co-supervisor of the proposal of the given application,
     * informing them about the application
     * @param application Application about which the co-supervisors of the corresponding proposal should be informed
     */
    void notifyCoSupervisorsOfNewApplication(Application application) throws MessagingException, IOException;

    /**
     * This method sends an email to all professors being set either as supervisor or as co-supervisor of the proposal
     * of the given application, informing them about the application
     * @param application Application about which the supervisor and co-supervisors of the corresponding proposal should be informed
     */
    void notifySupervisorAndCoSupervisorsOfNewApplication(Application application) throws MessagingException, IOException;
    void notifySupervisorOfExpiration(Proposal proposal) throws MessagingException, IOException;
    void notifySupervisorOfNewThesisRequest(ProposalOnRequest request) throws MessagingException, IOException;
}

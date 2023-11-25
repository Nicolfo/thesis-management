package it.polito.se2.g04.thesismanagement.application;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ApplicationService {

    List<ApplicationDTO2> getApplicationsByProf(String profEmail);
    List<ApplicationDTO3> getApplicationsByStudent(String studentEmail);
    List<ApplicationDTO4> getApplicationsByProposal(Long proposalId);
    ApplicationDTO4 getApplicationById(Long applicationId);
    /**
     * This method changes the state of a given application to accepted, if the current state is pending.
     * Otherwise, an error is thrown. After changing the state, the student who handed in the application is
     * notified via email
     * @param applicationId id of the application to be accepted
     * @return true if accepting was successful, otherwise false
     */
    boolean acceptApplicationById(Long applicationId);
    /**
     * This method changes the state of a given application to rejected, if the current state is pending.
     * Otherwise, an error is thrown. After changing the state, the student who handed in the application is
     * notified via email
     * @param applicationId id of the application to be rejected
     * @return true if rejecting was successful, otherwise false
     */
    boolean rejectApplicationById(Long applicationId);
    void applyForProposal(ApplicationDTO applicationDTO);

    /**
     * This method changes the state of a given application to the given string. This is done regardless
     * the current state of the application. After the state was changed, the student who handed in the application
     * is notified via E-Mail.
     * @param applicationId id of the application of which the state should be changed
     * @param newState state to which the application should be set
     * @return true if changing the state was successful, false if it wasn't.
     */
    boolean changeApplicationStateById(Long applicationId, String newState);
}

package it.polito.se2.g04.thesismanagement.application;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ApplicationService {


    List<ApplicationDTO> getApplicationsByProf(String profEmail);
    List<ApplicationDTO> getApplicationsByStudent(String studentEmail);
    List<ApplicationDTO> getApplicationsByProposal(Long proposalId);
    List<ApplicationDTO> getApplicationsByProposalId(Long proposalId);
    ApplicationDTO getApplicationById(Long applicationId);
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
    public boolean rejectApplicationById(Long applicationId);
    public void applyForProposal(ApplicationDTO applicationDTO);

    /**
     * This method updates the status all pending Applications to the proposal with the passed id to rejected.
     * The application with the passed id is excluded and not updated.
     * @param proposalId id of the proposal of which the pending applications should be rejected
     * @param exceptionApplicationId id of the application of which the status should not be updated (can be set to -1 to not exclude any application)
     * @return true if all pending applications could be changed to rejected. Otherwise, false is returned.
     */
    boolean cancelApplicationsByProposal(Long proposalId, Long exceptionApplicationId);

    /**
     * This method updates the status all pending Applications of the user with the passed Email-Adress to rejected.
     * The application with the passed id is excluded and not updated.
     * @param studentEmail email address of the user of which the pending applications should be rejected
     * @param exceptionApplicationId id of the application of which the status should not be updated (can be set to -1 to not exclude any application)
     * @return true if all pending applications could be changed to rejected. Otherwise, false is returned.
     */
    boolean cancelApplicationsByStudent(String studentEmail, Long exceptionApplicationId);
   
    ApplicationDTO getApplicationDTO(Application toReturn);
}

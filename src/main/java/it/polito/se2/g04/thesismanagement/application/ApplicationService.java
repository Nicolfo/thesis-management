package it.polito.se2.g04.thesismanagement.application;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ApplicationService {


    public List<ApplicationDTO> getApplicationsByProf(String profEmail);
    public List<ApplicationDTO> getApplicationsByStudent(String studentEmail);
    public List<ApplicationDTO> getApplicationsByProposal(Long proposalId);
    public List<ApplicationDTO> getApplicationsByProposalId(Long proposalId);
    public ApplicationDTO getApplicationById(Long applicationId);
  /**
     * This method changes the state of a given application to accepted, if the current state is pending.
     * Otherwise, an error is thrown. After changing the state, the student who handed in the application is
     * notified via email
     * @param applicationId id of the application to be accepted
     * @return true if accepting was successful, otherwise false
     */
    public boolean acceptApplicationById(Long applicationId);
     
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
     * This method changes the state of a given application to the given string. This is done regardless
     * the current state of the application. After the state was changed, the student who handed in the application
     * is notified via E-Mail. If a state is changed to accepted, all applications to the applied proposal and of the
     * user that handed in the application
     * @param applicationId id of the application of which the state should be changed
     * @param newState state to which the application should be set
     * @return true if changing the state of the given application was successful (and all applications that should be rejected were succesfully rejected), false if it wasn't.
     */
    boolean changeApplicationStateById(Long applicationId, String newState);

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
   

}

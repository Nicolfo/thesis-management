package it.polito.se2.g04.thesismanagement.application;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ApplicationService {

    public List<ApplicationDTO> getApplicationsByProf(String profEmail);
    public List<ApplicationDTO> getApplicationsByStudent(String studentEmail);
    public List<ApplicationDTO> getApplicationsByProposal(Long proposalId);
    public ApplicationDTO getApplicationById(Long applicationId);
    public boolean acceptApplicationById(Long applicationId);
    public boolean rejectApplicationById(Long applicationId);
    public void applyForProposal(ApplicationDTO applicationDTO);
    boolean changeApplicationStateById(Long applicationId, String newState);
    boolean rejectApplicationsByProposal(Long proposalId, Long exceptionApplicationId);
   
  /*
   public void acceptApplication(Long applicationID);

    public void declineApplication(Long applicationId);
  */
}

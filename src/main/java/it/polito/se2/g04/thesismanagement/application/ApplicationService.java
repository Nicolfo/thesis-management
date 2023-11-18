package it.polito.se2.g04.thesismanagement.application;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ApplicationService {

    public List<ApplicationDTO2> getApplicationsByProf(String profEmail);
    public List<ApplicationDTO3> getApplicationsByStudent(String studentEmail);
    public List<ApplicationDTO4> getApplicationsByProposal(Long proposalId);
    public ApplicationDTO4 getApplicationById(Long applicationId);
    public boolean acceptApplicationById(Long applicationId);
    public boolean rejectApplicationById(Long applicationId);
    public void applyForProposal(ApplicationDTO applicationDTO);
    boolean changeApplicationStateById(Long applicationId, String newState);
   
  /*
   public void acceptApplication(Long applicationID);

    public void declineApplication(Long applicationId);
  */
}

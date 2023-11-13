package it.polito.se2.g04.thesismanagement.application;

import java.util.List;

public interface ApplicationService {

    public List<ApplicationDTO2> getApplicationsByProf(String profEmail);

    public List<Application> getApplicationsByProposal(Long proposalId);

    public void applyForProposal(ApplicationDTO applicationDTO);
    public void acceptApplication(Long applicationID);

    public void declineApplication(Long applicationId);

    //add method here

    public String getTitleByApplicationId(Long applicationId);
}

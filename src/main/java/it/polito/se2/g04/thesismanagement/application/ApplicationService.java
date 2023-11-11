package it.polito.se2.g04.thesismanagement.application;

public interface ApplicationService {
    public void applyForProposal(ApplicationDTO applicationDTO);
    public void acceptApplication(Long applicationID);

    public void declineApplication(Long applicationId);
    //add method here
}

package it.polito.se2.g04.thesismanagement.application;

import java.util.List;

public interface ApplicationService {
    public List<Application> getApplicationsByProf(String profEmail);

    public List<Application> getApplicationsByProposal(Long proposalId);
    public Application getApplicationById(Long applicationId);
    public boolean acceptApplicationById(Long applicationId);
}

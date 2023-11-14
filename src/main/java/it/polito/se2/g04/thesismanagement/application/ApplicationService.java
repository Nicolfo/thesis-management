package it.polito.se2.g04.thesismanagement.application;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ApplicationService {
    public List<Application> getApplicationsByProf(String profEmail);

    public List<Application> getApplicationsByProposal(Long proposalId);
    public Application getApplicationById(Long applicationId);
    public boolean acceptApplicationById(Long applicationId);

    public boolean rejectApplicationById(Long applicationId);
}

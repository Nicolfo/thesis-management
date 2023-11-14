package it.polito.se2.g04.thesismanagement.application;

import it.polito.se2.g04.thesismanagement.proposal.ProposalNotFoundException;
import it.polito.se2.g04.thesismanagement.proposal.ProposalOwnershipException;
import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
import it.polito.se2.g04.thesismanagement.security.user.UserInfoUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService{
    private final ApplicationRepository applicationRepository;
    private final ProposalRepository proposalRepository;

    @Override
    public List<Application> getApplicationsByProf(String profEmail) {
        return applicationRepository.getApplicationByProposal_Supervisor_Email(profEmail);
    }

    @Override
    public List<Application> getApplicationsByProposal(Long proposalId) {
        if(!proposalRepository.existsById(proposalId) ){
            throw new ProposalNotFoundException("Specified proposal id not found");
        }
        UserInfoUserDetails auth =(UserInfoUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String profEmail= auth.getUsername();
        if(proposalRepository.getReferenceById(proposalId).getSupervisor().getEmail().compareTo(profEmail)==0){
            return applicationRepository.getApplicationByProposal_Id(proposalId);
        }
        throw new ProposalOwnershipException("Specified proposal id is not belonging to user: "+profEmail);
    }

    @Override
    public Application getApplicationById(Long applicationId){
        return applicationRepository.getApplicationById(applicationId);
    }

    @Override
    public boolean acceptApplicationById(Long applicationId) {
        try {
            Application application = getApplicationById(applicationId);
            if (!(application.getStatus().compareTo("PENDING")==0))
                return false;
            application.setStatus("ACCEPTED");
            applicationRepository.save(application);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean rejectApplicationById(Long applicationId) {
        try {
            Application application = getApplicationById(applicationId);
            if (!(application.getStatus().compareTo("PENDING")==0))
                return false;
            application.setStatus("REJECTED");
            applicationRepository.save(application);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

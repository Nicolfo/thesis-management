package it.polito.se2.g04.thesismanagement.application;

import it.polito.se2.g04.thesismanagement.proposal.ProposalNotFoundException;
import it.polito.se2.g04.thesismanagement.proposal.ProposalOwnershipException;
import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
import it.polito.se2.g04.thesismanagement.security.user.UserInfoUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import it.polito.se2.g04.thesismanagement.attachment.AttachmentRepository;
import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
import it.polito.se2.g04.thesismanagement.student.Student;
import it.polito.se2.g04.thesismanagement.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService{
    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final AttachmentRepository attachmentRepository;
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
    public void applyForProposal( ApplicationDTO applicationDTO) {
        //TODO: add parsing of logged user
        try {
            Student loggedUser=studentRepository.getReferenceById(1L);
            Application toSave = new Application(loggedUser, attachmentRepository.getReferenceById(applicationDTO.getAttachmentID()), applicationDTO.getApplyDate(), proposalRepository.getReferenceById(applicationDTO.getProposalID()));
            applicationRepository.save(toSave);
        }catch (Exception ex){
            throw new ApplicationBadRequestFormatException("The request field are null or the ID are not present in DB");
        }

    }

    @Override
    public void acceptApplication(Long applicationID) {
        //TODO: add parsing of logged user in order to check if the teacher is the one that is hosting the proposal
        try {
            Application toAccept =applicationRepository.getReferenceById(applicationID);
            toAccept.setStatus("ACCEPTED");
            applicationRepository.save(toAccept);
        }catch (Exception ex){
            throw new ApplicationBadRequestFormatException("The request field are null or the ID are not present in DB");
        }
    }

    @Override
    public void declineApplication(Long applicationID) {
        //TODO: add parsing of logged user in order to check if the teacher is the one that is hosting the proposal
        try {
            Application toAccept =applicationRepository.getReferenceById(applicationID);
            toAccept.setStatus("DECLINED");
            applicationRepository.save(toAccept);
        }catch (Exception ex){
            throw new ApplicationBadRequestFormatException("The request field are null or the ID are not present in DB");
        }

    }


    //implement method here
}

package it.polito.se2.g04.thesismanagement.application;

import it.polito.se2.g04.thesismanagement.attachment.AttachmentRepository;
import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
import it.polito.se2.g04.thesismanagement.student.Student;
import it.polito.se2.g04.thesismanagement.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService{
    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final AttachmentRepository attachmentRepository;
    private final ProposalRepository proposalRepository;



    @Override
    public void applyForProposal( ApplicationDTO applicationDTO) {
        try {
            Student loggedUser=studentRepository.getReferenceById(1L);
            Application toSave = new Application(loggedUser, attachmentRepository.getReferenceById(applicationDTO.getAttachmentID()), applicationDTO.getApplyDate(), proposalRepository.getReferenceById(applicationDTO.getProposalID()));
            applicationRepository.save(toSave);
        }catch (Exception ex){
            throw new ApplicationBadRequestFormatException("The request field are null or the ID are not present in DB");
        }

    }

    //implement method here
}

package it.polito.se2.g04.thesismanagement.application;

import it.polito.se2.g04.thesismanagement.proposal.*;
import it.polito.se2.g04.thesismanagement.security.user.UserInfoUserDetails;
import it.polito.se2.g04.thesismanagement.student.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import it.polito.se2.g04.thesismanagement.attachment.AttachmentRepository;
import it.polito.se2.g04.thesismanagement.student.Student;
import it.polito.se2.g04.thesismanagement.student.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService{
    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final AttachmentRepository attachmentRepository;
    private final ProposalRepository proposalRepository;
    private final StudentService studentService;

    @Override
    public List<ApplicationDTO2> getApplicationsByProf(String profEmail) {
        List<Application> toReturn=applicationRepository.getApplicationByProposal_Supervisor_Email(profEmail);

        return toReturn.stream().map(it->new ApplicationDTO2(it.getId(),
                it.getStudent().getId(),
                it.getStudent().getName(),
                it.getStudent().getSurname(),
                studentService.getAverageMarks(it.getStudent().getId()),
                it.getAttachment().getAttachmentId(),
                it.getApplyDate(),
                it.getProposal().getId(),
                it.getProposal().getTitle(),
                it.getStatus()
        )).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDTO3> getApplicationsByStudent(String studentEmail) {
        List<Application> toReturn=applicationRepository.getApplicationByStudentEmail(studentEmail);

        return toReturn.stream().map(it->new ApplicationDTO3(it.getId(),
                it.getProposal().getId(),
                it.getProposal().getTitle(),
                it.getProposal().getSupervisor().getName(),
                it.getProposal().getSupervisor().getSurname(),
                it.getStatus()
        )).collect(Collectors.toList());
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
    public ApplicationDTO4 getApplicationById(Long applicationId){
        Application toReturn= applicationRepository.getApplicationById(applicationId);
        return new ApplicationDTO4(toReturn.getId(),toReturn.getStudent(),toReturn.getAttachment().getAttachmentId(),toReturn.getApplyDate(),toReturn.getProposal(),toReturn.getStatus());
    }
    public Application getApplicationByIdOriginal(Long applicationId){
        Application toReturn= applicationRepository.getApplicationById(applicationId);
        return toReturn;
    }

    @Override
  public boolean acceptApplicationById(Long applicationId) {
        try {
            Application application = getApplicationByIdOriginal(applicationId);
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

  /*
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
  */
      @Override
    public boolean rejectApplicationById(Long applicationId) {
        try {
            Application application = getApplicationByIdOriginal(applicationId);
            if (!(application.getStatus().compareTo("PENDING")==0))
                return false;
            application.setStatus("REJECTED");
            applicationRepository.save(application);
            return true;
        } catch (Exception e) {
            return false;
          }
    }
  /*
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
  
  */
 

}

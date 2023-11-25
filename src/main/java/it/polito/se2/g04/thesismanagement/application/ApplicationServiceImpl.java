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
import java.util.Objects;
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
                it.getAttachment()!=null?it.getAttachment().getAttachmentId():null,
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
    public List<ApplicationDTO4> getApplicationsByProposal(Long proposalId){
        return getApplicationsByProposal(proposalId,true);
    }

    /**
     * This method returns all applications that belong to the given proposal id. If authenticationNeeded is true,
     * the applications are only returned if the logged-in user is a supervisor or co supervisor of the given proposal.
     * Otherwise, an error is thrown.
     * @param proposalId id of the proposal, of which the applications should be returned
     * @param authenticationNeeded if true, it is checked that the
     * @return List of applications to the given proposal
     */
    private List<ApplicationDTO4> getApplicationsByProposal(Long proposalId, boolean authenticationNeeded) {
        if(!proposalRepository.existsById(proposalId) ){
            throw new ProposalNotFoundException("Specified proposal id not found");
        }
        if(authenticationNeeded){
        UserInfoUserDetails auth =(UserInfoUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String profEmail= auth.getUsername();
        if(proposalRepository.getReferenceById(proposalId).getSupervisor().getEmail().compareTo(profEmail)==0){

            List<Application> toReturn= applicationRepository.getApplicationByProposal_Id(proposalId);
            return toReturn.stream().map(it->new ApplicationDTO4(
                    it.getId(),
                    it.getStudent(),
                    it.getAttachment() != null ? it.getAttachment().getAttachmentId() : null,
                    it.getApplyDate(),
                    it.getProposal(),
                    it.getStatus()
            )).toList();

        }
        throw new ProposalOwnershipException("Specified proposal id is not belonging to user: "+profEmail);
        }
        else  {
            List<Application> toReturn = applicationRepository.getApplicationByProposal_Id(proposalId);
            return toReturn.stream().map(it->new ApplicationDTO4(
                    it.getId(),
                    it.getStudent(),
                    it.getAttachment() != null ? it.getAttachment().getAttachmentId() : null,
                    it.getApplyDate(),
                    it.getProposal(),
                    it.getStatus()
            )).toList();

        }
    }

    @Override
    public ApplicationDTO4 getApplicationById(Long applicationId){
        Application toReturn= applicationRepository.getApplicationById(applicationId);
        return new ApplicationDTO4(toReturn.getId(),toReturn.getStudent(),toReturn.getAttachment()!=null?toReturn.getAttachment().getAttachmentId():null,toReturn.getApplyDate(),toReturn.getProposal(),toReturn.getStatus());
    }
    public Application getApplicationByIdOriginal(Long applicationId){
        Application toReturn= applicationRepository.getApplicationById(applicationId);
        return toReturn;
    }

    @Override
  public boolean acceptApplicationById(Long applicationId) {
        try {
            Application application = getApplicationByIdOriginal(applicationId);
            if (application.getStatus() != ApplicationStatus.PENDING)
                return false;
            application.setStatus(ApplicationStatus.ACCEPTED);
            applicationRepository.save(application);
            return rejectApplicationsByProposal(application.getProposal().getId(),applicationId);
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

    @Override
    public boolean changeApplicationStateById(Long applicationId, String newState) {
        try {
            ApplicationStatus newStateEnum = ApplicationStatus.valueOf(newState);
            Application application = getApplicationByIdOriginal(applicationId);
            application.setStatus(newStateEnum);
            applicationRepository.save(application);
            if(newStateEnum == ApplicationStatus.REJECTED){
                return rejectApplicationsByProposal(application.getProposal().getId(), applicationId);
            }
            return true;
        } catch (Exception e) {
            return false;
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
            if (application.getStatus()!= ApplicationStatus.PENDING)
                return false;
            application.setStatus(ApplicationStatus.REJECTED);
            applicationRepository.save(application);
            return true;
        } catch (Exception e) {
            return false;
          }
    }

    @Override
    public boolean rejectApplicationsByProposal(Long proposalId, Long exceptionApplicationId){
          boolean success = true;
          List<ApplicationDTO4> applicationList = getApplicationsByProposal(proposalId, false);

          for (ApplicationDTO4 application: applicationList)
              if(!Objects.equals(exceptionApplicationId, application.getId()))
                  success = success && (this.rejectApplicationById(application.getId())|| application.getStatus() != ApplicationStatus.PENDING);

          return success;
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

package it.polito.se2.g04.thesismanagement.application;

import it.polito.se2.g04.thesismanagement.email.EmailService;
import it.polito.se2.g04.thesismanagement.proposal.*;
import it.polito.se2.g04.thesismanagement.student.StudentDTO;
import it.polito.se2.g04.thesismanagement.student.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import it.polito.se2.g04.thesismanagement.attachment.AttachmentRepository;
import it.polito.se2.g04.thesismanagement.student.Student;
import it.polito.se2.g04.thesismanagement.student.StudentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    private final EmailService emailService;

    @Override
    public List<ApplicationDTO> getApplicationsByProf(String profEmail) {
        List<Application> toReturn=applicationRepository.getApplicationByProposal_Supervisor_Email(profEmail);

        return toReturn.stream().map(it -> {
            ApplicationDTO dto = new ApplicationDTO();
            dto.setId(it.getId());
            dto.setStudentId(it.getStudent().getId());
            dto.setStudentName(it.getStudent().getName());
            dto.setStudentSurname(it.getStudent().getSurname());
            dto.setStudentAverageGrades(BigDecimal.valueOf(studentService.getAverageMarks(it.getStudent().getId())).setScale(2, BigDecimal.ROUND_HALF_UP));
            dto.setAttachmentId(it.getAttachment()!=null?it.getAttachment().getAttachmentId():null);
            dto.setApplyDate(it.getApplyDate());
            dto.setProposalId(it.getProposal().getId());
            dto.setProposalTitle(it.getProposal().getTitle());
            dto.setStatus(it.getStatus());
            return dto;
        }).collect(Collectors.toList());
        /*return toReturn.stream().map(it ->new ApplicationDTO2(it.getId(),
                it.getStudent().getId(),
                it.getStudent().getName(),
                it.getStudent().getSurname(),
                BigDecimal.valueOf(studentService.getAverageMarks(it.getStudent().getId())).setScale(2, BigDecimal.ROUND_HALF_UP),
                it.getAttachment()!=null?it.getAttachment().getAttachmentId():null,
                it.getApplyDate(),
                it.getProposal().getId(),
                it.getProposal().getTitle(),
                it.getStatus()
        )).collect(Collectors.toList());*/
    }

    @Override
    public List<ApplicationDTO> getApplicationsByStudent(String studentEmail) {
        List<Application> toReturn=applicationRepository.getApplicationByStudentEmail(studentEmail);
        return toReturn.stream().map(it -> {
            ApplicationDTO dto = new ApplicationDTO();
            dto.setId(it.getId());
            dto.setProposalId(it.getProposal().getId());
            dto.setProposalTitle(it.getProposal().getTitle());
            dto.setSupervisorName(it.getProposal().getSupervisor().getName());
            dto.setSupervisorSurname(it.getProposal().getSupervisor().getSurname());
            dto.setStatus(it.getStatus());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDTO> getApplicationsByProposal(Long proposalId){
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
    private List<ApplicationDTO> getApplicationsByProposal(Long proposalId, boolean authenticationNeeded) {
        if(!proposalRepository.existsById(proposalId) ){
            throw new ProposalNotFoundException("Specified proposal id not found");
        }
        Authentication auth =SecurityContextHolder.getContext().getAuthentication();
        String profEmail= auth.getName();
        if(proposalRepository.getReferenceById(proposalId).getSupervisor().getEmail().compareTo(profEmail)==0){

            List<Application> toReturn= applicationRepository.getApplicationByProposal_Id(proposalId);
            return toReturn.stream().map(it -> {
                ApplicationDTO dto = new ApplicationDTO();
                dto.setId(it.getId());
                dto.setStudent(StudentDTO.fromStudent(it.getStudent()));
                dto.setAttachmentId(it.getAttachment() != null ? it.getAttachment().getAttachmentId() : null);
                dto.setApplyDate(it.getApplyDate());
                dto.setProposal(ProposalFullDTO.fromProposal(it.getProposal()));
                dto.setStudentAverageGrades(BigDecimal.valueOf(studentService.getAverageMarks(it.getStudent().getId())).setScale(2, BigDecimal.ROUND_HALF_UP));
                dto.setStatus(it.getStatus());
                return dto;
            }).toList();
            /*return toReturn.stream().map(it->new ApplicationDTO4(
                    it.getId(),
                    it.getStudent(),
                    it.getAttachment() != null ? it.getAttachment().getAttachmentId() : null,
                    it.getApplyDate(),
                    it.getProposal(),
                    BigDecimal.valueOf(studentService.getAverageMarks(it.getStudent().getId())).setScale(2, BigDecimal.ROUND_HALF_UP),
                    it.getStatus()
            )).toList();*/

        }



        throw new ProposalOwnershipException("Specified proposal id is not belonging to user: "+profEmail);
        }



    @Override
    public ApplicationDTO getApplicationById(Long applicationId){
        Application toReturn= applicationRepository.getApplicationById(applicationId);
        ApplicationDTO dto = new ApplicationDTO();
        dto.setId(toReturn.getId());
        dto.setStudent(StudentDTO.fromStudent(toReturn.getStudent()));
        dto.setAttachmentId(toReturn.getAttachment()!=null?toReturn.getAttachment().getAttachmentId():null);
        dto.setApplyDate(toReturn.getApplyDate());
        dto.setProposal(ProposalFullDTO.fromProposal(toReturn.getProposal()));
        dto.setStudentAverageGrades(BigDecimal.valueOf(studentService.getAverageMarks(toReturn.getStudent().getId())).setScale(2, BigDecimal.ROUND_HALF_UP));
        dto.setStatus(toReturn.getStatus());
        return dto;
        //return new ApplicationDTO4(toReturn.getId(),toReturn.getStudent(),toReturn.getAttachment()!=null?toReturn.getAttachment().getAttachmentId():null,toReturn.getApplyDate(),toReturn.getProposal(),BigDecimal.valueOf(studentService.getAverageMarks(toReturn.getStudent().getId())).setScale(2, BigDecimal.ROUND_HALF_UP),toReturn.getStatus());
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
            application = applicationRepository.save(application);
            emailService.notifyStudentOfApplicationDecision(application);
            return rejectApplicationsByProposal(application.getProposal().getId(),applicationId);
        } catch (Exception e) {
            return false;
             }
    }
  @Override
  public void applyForProposal( ApplicationDTO applicationDTO) {
        //TODO: add parsing of logged user
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Student loggedUser = studentRepository.getStudentByEmail(auth.getName());
            Application toSave = new Application(loggedUser, attachmentRepository.getReferenceById(applicationDTO.getAttachmentId()), applicationDTO.getApplyDate(), proposalRepository.getReferenceById(applicationDTO.getProposalId()));
            Application saved=applicationRepository.save(toSave);
            emailService.notifySupervisorAndCoSupervisorsOfNewApplication(saved);
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

            emailService.notifyStudentOfApplicationDecision(application);
            if(newStateEnum == ApplicationStatus.ACCEPTED){
                return rejectApplicationsByProposal(application.getProposal().getId(), applicationId);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

      @Override
    public boolean rejectApplicationById(Long applicationId) {
        try {
            Application application = getApplicationByIdOriginal(applicationId);
            if (application.getStatus()!= ApplicationStatus.PENDING)
                return false;
            application.setStatus(ApplicationStatus.REJECTED);
            applicationRepository.save(application);
            emailService.notifyStudentOfApplicationDecision(application);
            return true;
        } catch (Exception e) {
            return false;
          }
    }


    @Override
    public boolean rejectApplicationsByProposal(Long proposalId, Long exceptionApplicationId){
          boolean success = true;
          List<ApplicationDTO> applicationList = getApplicationsByProposal(proposalId, false);

          for (ApplicationDTO application: applicationList)
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

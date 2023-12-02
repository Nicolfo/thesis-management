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

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final AttachmentRepository attachmentRepository;
    private final ProposalRepository proposalRepository;
    private final StudentService studentService;
    private final EmailService emailService;

    @Override
    public List<ApplicationDTO> getApplicationsByProf(String profEmail) {
        List<Application> toReturn = applicationRepository.getApplicationByProposal_Supervisor_Email(profEmail)
                .stream().filter(it->it.getStatus()!=ApplicationStatus.DELETED).collect(Collectors.toList());

        return toReturn.stream().map(it -> {
            ApplicationDTO dto = new ApplicationDTO();
            dto.setId(it.getId());
            dto.setStudentId(it.getStudent().getId());
            dto.setStudentName(it.getStudent().getName());
            dto.setStudentSurname(it.getStudent().getSurname());
            dto.setStudentAverageGrades(BigDecimal.valueOf(studentService.getAverageMarks(it.getStudent().getId())).setScale(2, BigDecimal.ROUND_HALF_UP));
            dto.setAttachmentId(it.getAttachment() != null ? it.getAttachment().getAttachmentId() : null);
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


        return applicationRepository
                .getApplicationByProposal_Supervisor_Email(profEmail)
                .stream().map(it -> {
                    ApplicationDTO dto = new ApplicationDTO();
                    dto.setId(it.getId());
                    dto.setStudentId(it.getStudent().getId());
                    dto.setStudentName(it.getStudent().getName());
                    dto.setStudentSurname(it.getStudent().getSurname());
                    dto.setStudentAverageGrades(BigDecimal.valueOf(studentService.getAverageMarks(it.getStudent().getId())).setScale(2, BigDecimal.ROUND_HALF_UP));
                    dto.setAttachmentId(it.getAttachment() == null ? null : it.getAttachment().getAttachmentId());
                    dto.setApplyDate(it.getApplyDate());
                    dto.setProposalId(it.getProposal().getId());
                    dto.setProposalTitle(it.getProposal().getTitle());
                    dto.setStatus(it.getStatus());
                    return dto;
                }).toList();

    }

    @Override
    public List<ApplicationDTO> getApplicationsByStudent(String studentEmail) {
        List<Application> toReturn = applicationRepository.getApplicationByStudentEmail(studentEmail)
                .stream().filter(it->it.getStatus()!=ApplicationStatus.DELETED).collect(Collectors.toList());
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
    public List<ApplicationDTO> getApplicationsByProposal(Long proposalId) {
        return getApplicationsByProposal(proposalId, true);
    }
        return applicationRepository
                .getApplicationByStudentEmail(studentEmail)
                .stream().map(it -> {
                    ApplicationDTO dto = new ApplicationDTO();
                    dto.setId(it.getId());
                    dto.setProposalId(it.getProposal().getId());
                    dto.setProposalTitle(it.getProposal().getTitle());
                    dto.setSupervisorName(it.getProposal().getSupervisor().getName());
                    dto.setSupervisorSurname(it.getProposal().getSupervisor().getSurname());
                    dto.setStatus(it.getStatus());
                    return dto;
                }).toList();
    }


    /**
     * This method returns all applications that belong to the given proposal id. If authenticationNeeded is true,
     * the applications are only returned if the logged-in user is a supervisor or co supervisor of the given proposal.
     * Otherwise, an error is thrown.
     *
     * @param proposalId           id of the proposal, of which the applications should be returned
     * @param authenticationNeeded if true, it is checked that the
     * @return List of applications to the given proposal
     */
    private List<ApplicationDTO> getApplicationsByProposal(Long proposalId, boolean authenticationNeeded) {

     * @param proposalId id of the proposal, of which the applications should be returned
     * @return List of applications to the given proposal
     */
    @Override
    public List<ApplicationDTO> getApplicationsByProposal(Long proposalId) {
        if (!proposalRepository.existsById(proposalId)) {
            throw new ProposalNotFoundException("Specified proposal id not found");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String profEmail = auth.getName();
        if (proposalRepository.getReferenceById(proposalId).getSupervisor().getEmail().compareTo(profEmail) == 0) {

            List<Application> toReturn = applicationRepository.getApplicationByProposal_Id(proposalId)
                    .stream().filter(it->it.getStatus()!=ApplicationStatus.DELETED).collect(Collectors.toList());
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


        throw new ProposalOwnershipException("Specified proposal id is not belonging to user: " + profEmail);
    }


    @Override
    public ApplicationDTO getApplicationById(Long applicationId) {
        Application toReturn = applicationRepository.getApplicationById(applicationId);
            return applicationRepository
                    .getApplicationByProposal_Id(proposalId)
                    .stream().map(it -> getApplicationDTO(it)).toList();
        }
        throw new ProposalOwnershipException("Specified proposal id is not belonging to user: " + profEmail);
    }

    @Override
    public ApplicationDTO getApplicationById(Long applicationId) {
        return getApplicationDTO(applicationRepository.getApplicationById(applicationId));
    }

    private ApplicationDTO getApplicationDTO(Application toReturn) {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setId(toReturn.getId());
        dto.setStudent(StudentDTO.fromStudent(toReturn.getStudent()));
        dto.setAttachmentId(toReturn.getAttachment() != null ? toReturn.getAttachment().getAttachmentId() : null);
        dto.setApplyDate(toReturn.getApplyDate());
        dto.setProposal(ProposalFullDTO.fromProposal(toReturn.getProposal()));
        dto.setStudentAverageGrades(BigDecimal.valueOf(studentService.getAverageMarks(toReturn.getStudent().getId())).setScale(2, BigDecimal.ROUND_HALF_UP));
        dto.setStatus(toReturn.getStatus());
        return dto;
    }

    public Application getApplicationByIdOriginal(Long applicationId) {
        return applicationRepository.getApplicationById(applicationId);
    }

    @Override
    public boolean acceptApplicationById(Long applicationId) {
        try {
            Application application = getApplicationByIdOriginal(applicationId);
            Proposal proposal = proposalRepository.getReferenceById(application.getProposal().getId());
            if (application.getStatus() != ApplicationStatus.PENDING)
                return false;
            application.setStatus(ApplicationStatus.ACCEPTED);
            proposal.setStatus(Proposal.Status.ACCEPTED);
            application = applicationRepository.save(application);
            proposal = proposalRepository.save(proposal);
            emailService.notifyStudentOfApplicationDecision(application);
            return rejectApplicationsByProposal(application.getProposal().getId(), applicationId)
                    && rejectApplicationsByStudent(application.getStudent().getEmail(), applicationId);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void applyForProposal(ApplicationDTO applicationDTO) {
        //TODO: add parsing of logged user
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Student loggedUser = studentRepository.getStudentByEmail(auth.getName());
            Application toSave = new Application(loggedUser, attachmentRepository.getReferenceById(applicationDTO.getAttachmentId()), applicationDTO.getApplyDate(), proposalRepository.getReferenceById(applicationDTO.getProposalId()));
            Application saved = applicationRepository.save(toSave);
            emailService.notifySupervisorAndCoSupervisorsOfNewApplication(saved);
        } catch (Exception ex) {
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
            if (newStateEnum == ApplicationStatus.ACCEPTED) {
                return
                        rejectApplicationsByProposal(application.getProposal().getId(), applicationId)
                                && rejectApplicationsByStudent(application.getStudent().getEmail(), applicationId);
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
            if (application.getStatus() != ApplicationStatus.PENDING)
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
    public boolean rejectApplicationsByProposal(Long proposalId, Long exceptionApplicationId) {
        boolean success = true;
        List<ApplicationDTO> applicationList = getApplicationsByProposal(proposalId);
        for (ApplicationDTO application : applicationList)
            if (!Objects.equals(exceptionApplicationId, application.getId()))
                success = success && (this.rejectApplicationById(application.getId()) || application.getStatus() != ApplicationStatus.PENDING);
        return success;
    }

    @Override
    public boolean rejectApplicationsByStudent(String studentEmail, Long exceptionApplicationId) {
        boolean success = true;
        List<ApplicationDTO> applicationList = getApplicationsByStudent(studentEmail);
        for (ApplicationDTO application : applicationList)
            if (!Objects.equals(exceptionApplicationId, application.getId()))
                success = success
                        && (this.rejectApplicationById(application.getId()) || application.getStatus() != ApplicationStatus.PENDING);
        return success;
    }
}

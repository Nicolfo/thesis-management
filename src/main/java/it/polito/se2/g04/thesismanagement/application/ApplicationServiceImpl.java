package it.polito.se2.g04.thesismanagement.application;

import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.application.ApplicationBadRequestFormatException;
import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.application.ApplicationDeletedException;
import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.application.DuplicateApplicationException;
import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.application.ProposalNotActiveException;
import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal.ProposalNotFoundException;
import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal.ProposalOwnershipException;
import it.polito.se2.g04.thesismanagement.attachment.Attachment;
import it.polito.se2.g04.thesismanagement.attachment.AttachmentRepository;
import it.polito.se2.g04.thesismanagement.notification.EmailService;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.proposal.ProposalFullDTO;
import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
import it.polito.se2.g04.thesismanagement.student.Student;
import it.polito.se2.g04.thesismanagement.student.StudentDTO;
import it.polito.se2.g04.thesismanagement.student.StudentRepository;
import it.polito.se2.g04.thesismanagement.student.StudentService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ApplicationDTO> getApplicationsByProf(String profEmail) {
        return applicationRepository
                .getApplicationByProposal_Supervisor_EmailAndStatusIsNotOrderByProposalId(profEmail, ApplicationStatus.CANCELLED)
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
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Application> cq = cb.createQuery(Application.class);
        Root<Application> proposal = cq.from(Application.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.notEqual(proposal.get("status"), Proposal.Status.DELETED));
        predicates.add(cb.equal(proposal.join("student").get("email"), studentEmail));
        cq.where(predicates.toArray(new Predicate[0]));
        List<Application> toReturn = entityManager.createQuery(cq).getResultList();


        return toReturn.stream().map(it -> {
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

    @Override
    public List<ApplicationDTO> getApplicationsByProposal(Long proposalId) {
        if (!proposalRepository.existsById(proposalId)) {
            throw new ProposalNotFoundException("Specified proposal id not found");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String profEmail = auth.getName();
        Proposal proposal = proposalRepository.getReferenceById(proposalId);
        if (proposal.getSupervisor().getEmail().compareTo(profEmail) == 0 && proposal.getStatus() != Proposal.Status.DELETED) {
            return applicationRepository
                    .getApplicationByProposal_Id(proposalId)
                    .stream().filter(it -> it.getStatus() != ApplicationStatus.CANCELLED).map(this::getApplicationDTO).toList();
        }
        throw new ProposalOwnershipException("Specified proposal id is not belonging to user: " + profEmail);
    }

    @Override
    public List<ApplicationDTO> getApplicationsByProposalId(Long proposalId) {
        if (!proposalRepository.existsById(proposalId)) {
            throw new ProposalNotFoundException("Specified proposal id not found");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String studEmail = auth.getName();
        Student student = studentRepository.getStudentByEmail(studEmail);
        Proposal proposal = proposalRepository.getReferenceById(proposalId);
        if (proposal.getStatus() != Proposal.Status.DELETED) {
            return applicationRepository
                    .getApplicationByProposalIdAndStudentId(proposalId, student.getId())
                    .stream().filter(it -> it.getStatus() != ApplicationStatus.CANCELLED).map(this::getApplicationDTO).toList();
        }
        return null;
    }

    @Override
    public ApplicationDTO getApplicationById(Long applicationId) {
        if (applicationRepository.getApplicationById(applicationId).getStatus() == ApplicationStatus.CANCELLED) {
            throw new ApplicationDeletedException("this application is flagged to be deleted");
        }
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
            proposalRepository.save(proposal);
            emailService.notifyStudentOfApplicationDecision(application);
            emailService.notifyCoSupervisorsOfDecisionOnApplication(application);
            return cancelApplicationsByProposal(application.getProposal().getId(), applicationId)
                    && cancelApplicationsByStudent(application.getStudent().getEmail(), applicationId);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void applyForProposal(ApplicationDTO applicationDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!studentRepository.existsByEmail(auth.getName())) {
            throw new ApplicationBadRequestFormatException("The student doesn't exist");
        }

        Student loggedUser = studentRepository.getStudentByEmail(auth.getName());

        applyForProposalHelper(applicationDTO, loggedUser);
    }


    private void applyForProposalHelper(ApplicationDTO applicationDTO, Student loggedUser) {
        if (applicationDTO.getProposalId() == null || !proposalRepository.existsById(applicationDTO.getProposalId())) {
            throw new ApplicationBadRequestFormatException("The proposal doesn't exist");
        }

        Proposal proposal = proposalRepository.getReferenceById(applicationDTO.getProposalId());
        if (proposal.getStatus() != Proposal.Status.ACTIVE) {
            throw new ProposalNotActiveException("This proposal is not active");
        }

        if (applicationRepository.existsByProposalAndStudent(proposal, loggedUser)) {
            throw new DuplicateApplicationException("An application already exists for this proposal");
        }

        Attachment attachment = applicationDTO.getAttachmentId() != null ? attachmentRepository.getReferenceById(applicationDTO.getAttachmentId()) : null;
        Application toSave = new Application(loggedUser, attachment, applicationDTO.getApplyDate(), proposalRepository.getReferenceById(applicationDTO.getProposalId()));
        Application saved = applicationRepository.save(toSave);
        //try {
            emailService.notifySupervisorAndCoSupervisorsOfNewApplication(saved);
        /*} catch (MessagingException | IOException e) {
            throw new EmailFailedSendException(MAIL_ERROR);
        }*/
    }

    @Override
    public boolean changeApplicationStateById(Long applicationId, String newState) {
        try {
            ApplicationStatus newStateEnum = ApplicationStatus.valueOf(newState);
            Application application = getApplicationByIdOriginal(applicationId);
            application.setStatus(newStateEnum);
            if (application.getProposal().getStatus() != Proposal.Status.ACTIVE) {
                Proposal proposalToChange = application.getProposal();
                proposalToChange.setStatus(Proposal.Status.ACTIVE);
                proposalRepository.save(proposalToChange);
            }
            applicationRepository.save(application);

            emailService.notifyStudentOfApplicationDecision(application);
            if (newStateEnum == ApplicationStatus.ACCEPTED) {
                return
                        cancelApplicationsByProposal(application.getProposal().getId(), applicationId)
                                && cancelApplicationsByStudent(application.getStudent().getEmail(), applicationId);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean cancelApplicationById(Long applicationId) {
        try {
            Application application = getApplicationByIdOriginal(applicationId);
            if (application.getStatus() != ApplicationStatus.PENDING)
                return false;
            application.setStatus(ApplicationStatus.CANCELLED);
            application = applicationRepository.save(application);
            emailService.notifyCoSupervisorsOfDecisionOnApplication(application);
            emailService.notifyStudentOfApplicationDecision(application);
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
            application = applicationRepository.save(application);
            emailService.notifyCoSupervisorsOfDecisionOnApplication(application);
            emailService.notifyStudentOfApplicationDecision(application);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean cancelApplicationsByProposal(Long proposalId, Long exceptionApplicationId) {
        boolean success = true;
        List<ApplicationDTO> applicationList = getApplicationsByProposal(proposalId);
        return cancelApplicationsHelper(exceptionApplicationId, success, applicationList);
    }

    @Override
    public boolean cancelApplicationsByStudent(String studentEmail, Long exceptionApplicationId) {
        boolean success = true;
        List<ApplicationDTO> applicationList = getApplicationsByStudent(studentEmail);
        return cancelApplicationsHelper(exceptionApplicationId, success, applicationList);
    }

    //TODO: check if it works properly
    private boolean cancelApplicationsHelper(Long exceptionApplicationId, boolean success, List<ApplicationDTO> applicationList) {
        for (ApplicationDTO application : applicationList)
            if (!Objects.equals(exceptionApplicationId, application.getId())) {
                success = success && (this.cancelApplicationById(application.getId()) || application.getStatus() != ApplicationStatus.PENDING);
                if (success)
                    emailService.notifyCoSupervisorsOfDecisionOnApplication(applicationRepository.findById(application.getId()).get());
            }
        return success;
    }
}

package it.polito.se2.g04.thesismanagement.proposal_on_request;

import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal.ProposalNotFoundException;
import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.student.StudentNotFoundException;
import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.teacher.TeacherNotFoundException;
import it.polito.se2.g04.thesismanagement.application.Application;
import it.polito.se2.g04.thesismanagement.application.ApplicationRepository;
import it.polito.se2.g04.thesismanagement.application.ApplicationStatus;
import it.polito.se2.g04.thesismanagement.notification.EmailService;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
import it.polito.se2.g04.thesismanagement.student.Student;
import it.polito.se2.g04.thesismanagement.student.StudentRepository;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import it.polito.se2.g04.thesismanagement.teacher.TeacherRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProposalOnRequestServiceImpl implements ProposalOnRequestService {
    private final ProposalOnRequestRepository proposalOnRequestRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final ProposalRepository proposalRepository;
    private final ApplicationRepository applcationRepository;
    private final EmailService emailService;
    private static final String PROPOSAL_ON_REQUEST_IS_NOT_PENDING = "Proposal On Request is not pending";

    @PersistenceContext
    private EntityManager entityManager;
  
    @Override
    public List<ProposalOnRequestFullDTO> getAllPending() {
        List<ProposalOnRequest> pendingProposals = proposalOnRequestRepository.getProposalOnRequestByStatus(ProposalOnRequest.Status.PENDING);
        List<ProposalOnRequestFullDTO> pendingProposalDTOs = new ArrayList<>();
        for (ProposalOnRequest proposal : pendingProposals) {
            pendingProposalDTOs.add(proposal.toFullDTO());
        }
        return pendingProposalDTOs;
    }

    private ProposalOnRequest checkProposalId(Long id) {
        if (!proposalOnRequestRepository.existsById(id)) {
            String proposalDoNotExistError = "Proposal with this id does not exist";
            throw (new ProposalNotFoundException(proposalDoNotExistError));
        }
        return proposalOnRequestRepository.getReferenceById(id);
    }

    @Override
    public ProposalOnRequestDTO proposalOnRequestSecretaryAccepted(Long id) {
        ProposalOnRequest proposal = checkProposalId(id);

        if (proposal.getStatus() != ProposalOnRequest.Status.PENDING) {
            throw (new ProposalNotFoundException(PROPOSAL_ON_REQUEST_IS_NOT_PENDING));
        }
        proposal.setStatus(ProposalOnRequest.Status.SECRETARY_ACCEPTED);
        emailService.notifySupervisorAndCoSupervisorsOfNewThesisRequest(proposal);
        return proposalOnRequestRepository.save(proposal).toDTO();
    }

    @Override
    public ProposalOnRequestDTO proposalOnRequestSecretaryRejected(Long id) {
        ProposalOnRequest proposal = checkProposalId(id);

        if (proposal.getStatus() != ProposalOnRequest.Status.PENDING) {
            throw (new ProposalNotFoundException(PROPOSAL_ON_REQUEST_IS_NOT_PENDING));
        }
        proposal.setStatus(ProposalOnRequest.Status.SECRETARY_REJECTED);
        return proposalOnRequestRepository.save(proposal).toDTO();
    }

    @Override
    public ProposalOnRequestDTO createProposalRequest(ProposalOnRequestDTO proposalOnRequestDTO) {
        //PARSE TEACHER
        if (!teacherRepository.existsById(proposalOnRequestDTO.getSupervisor()))
            throw new TeacherNotFoundException("Teacher not found exception");
        Teacher teacher = teacherRepository.getReferenceById(proposalOnRequestDTO.getSupervisor());
        //PARSE STUDENT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!studentRepository.existsByEmail(auth.getName()))
            throw new StudentNotFoundException("Student not found exception");
        Student student = studentRepository.getStudentByEmail(auth.getName());
        //PARSE CO-SUPERVISORS
        List<Teacher> coSupervisors;
        if (proposalOnRequestDTO.getCoSupervisors() == null || proposalOnRequestDTO.getCoSupervisors().isEmpty())
            coSupervisors = List.of();
        else {
            coSupervisors = proposalOnRequestDTO.getCoSupervisors().stream().map(it -> {
                if (!teacherRepository.existsById(it))
                    throw new TeacherNotFoundException("Some co-supervisor has not been found");
                return teacherRepository.getReferenceById(it);
            }).toList();
        }
        ProposalOnRequest toAdd = new ProposalOnRequest(
                proposalOnRequestDTO.getTitle(),
                proposalOnRequestDTO.getDescription(),
                teacher,
                student,
                coSupervisors,
                proposalOnRequestDTO.getApprovalDate()
        );
        return proposalOnRequestRepository.save(toAdd).toDTO();
    }
    @Override
    public ProposalOnRequestDTO proposalOnRequestTeacherAccepted(Long id) {
        ProposalOnRequest proposal = checkProposalId(id);
        if (proposal.getStatus() != ProposalOnRequest.Status.SECRETARY_ACCEPTED) {
            throw (new ProposalNotFoundException(PROPOSAL_ON_REQUEST_IS_NOT_PENDING));
        }
            proposal.setStatus(ProposalOnRequest.Status.TEACHER_ACCEPTED);
            proposal.setApprovalDate(new Date());
            Proposal proposal1=new Proposal(proposal);
            proposal1.setStatus(Proposal.Status.ACCEPTED);
            Application application = new Application(
                    proposal.getStudent(),
                    null,
                    proposal.getApprovalDate(),
                    proposal1
            );
            application.setStatus(ApplicationStatus.ACCEPTED);
            proposalRepository.save(proposal1);
            applcationRepository.save(application);
            return proposalOnRequestRepository.save(proposal).toDTO();
    }

    @Override
    public ProposalOnRequestDTO proposalOnRequestTeacherRejected(Long id) {
        ProposalOnRequest proposal = checkProposalId(id);
        if (proposal.getStatus() != ProposalOnRequest.Status.SECRETARY_ACCEPTED) {
            throw (new ProposalNotFoundException(PROPOSAL_ON_REQUEST_IS_NOT_PENDING));
        }
        proposal.setStatus(ProposalOnRequest.Status.TEACHER_REJECTED);
        return proposalOnRequestRepository.save(proposal).toDTO();
    }
    @Override
    public ProposalOnRequestDTO proposalOnRequestTeacherRequestChange(Long id) {
        ProposalOnRequest proposal = checkProposalId(id);
        if (proposal.getStatus() != ProposalOnRequest.Status.SECRETARY_ACCEPTED) {
            throw (new ProposalNotFoundException(PROPOSAL_ON_REQUEST_IS_NOT_PENDING));
        }
        proposal.setStatus(ProposalOnRequest.Status.TEACHER_REVIEW);
        return proposalOnRequestRepository.save(proposal).toDTO();
    }

    @Override
    public ProposalOnRequestDTO proposalOnRequestTeacherChangeStatus(Long id, ProposalOnRequest.Status status){
        ProposalOnRequest proposal= checkProposalId(id);
        if (proposal.getStatus() != ProposalOnRequest.Status.SECRETARY_ACCEPTED){
            throw (new ProposalNotFoundException(PROPOSAL_ON_REQUEST_IS_NOT_PENDING));
        }
        proposal.setStatus(status);
        if (status == ProposalOnRequest.Status.TEACHER_ACCEPTED)
            proposal.setApprovalDate(new Date());
        return proposalOnRequestRepository.save(proposal).toDTO();
    }

    @Override
    public List<ProposalOnRequestFullDTO> getPendingRequestsByTeacher(Long teacherId) {
        return proposalOnRequestRepository.getProposalOnRequestsBySupervisorIdAndStatus(teacherId,ProposalOnRequest.Status.SECRETARY_ACCEPTED).stream().map(ProposalOnRequest::toFullDTO).toList();
    }

    @Override
    public List<ProposalOnRequestFullDTO> getProposalOnRequestByStudent(String studentId) {
        return proposalOnRequestRepository.getProposalOnRequestsByStudentEmail(studentId).stream().map(ProposalOnRequest::toFullDTO).toList();
    }

}



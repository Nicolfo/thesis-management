package it.polito.se2.g04.thesismanagement.proposal_on_request;

import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal.ProposalNotFoundException;
import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal.ProposalOwnershipException;
import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal_on_request.EmptyRequestedChangeException;
import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal_on_request.MultipleProposalOnRequestPending;
import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal_on_request.ProposalInvalidStateException;
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

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProposalOnRequestServiceImpl implements ProposalOnRequestService {
    private final ProposalOnRequestRepository proposalOnRequestRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final EmailService emailService;
    private static final String PROPOSAL_ON_REQUEST_IS_NOT_PENDING = "Proposal On Request is not pending";
    private static final String REQUESTED_CHANGE_IS_EMPTY = "The requested change description is empty.";

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
            throw (new ProposalInvalidStateException(PROPOSAL_ON_REQUEST_IS_NOT_PENDING));
        }
        proposal.setStatus(ProposalOnRequest.Status.SECRETARY_ACCEPTED);
        emailService.notifySupervisorAndCoSupervisorsOfNewThesisRequest(proposal);
        return proposalOnRequestRepository.save(proposal).toDTO();
    }

    @Override
    public ProposalOnRequestDTO proposalOnRequestSecretaryRejected(Long id) {
        ProposalOnRequest proposal = checkProposalId(id);

        if (proposal.getStatus() != ProposalOnRequest.Status.PENDING) {
            throw (new ProposalInvalidStateException(PROPOSAL_ON_REQUEST_IS_NOT_PENDING));
        }
        proposal.setStatus(ProposalOnRequest.Status.SECRETARY_REJECTED);
        return proposalOnRequestRepository.save(proposal).toDTO();
    }

    @Override
    public ProposalOnRequestDTO createProposalRequest(ProposalOnRequestDTO proposalOnRequestDTO) {
        //PARSE STUDENT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!studentRepository.existsByEmail(auth.getName()))
            throw new StudentNotFoundException("Student not found exception");
        Student student = studentRepository.getStudentByEmail(auth.getName());
        if (proposalOnRequestRepository.existsProposalOnRequestByStudentIdAndStatusNotIn(student.getId(), List.of(ProposalOnRequest.Status.TEACHER_REJECTED, ProposalOnRequest.Status.SECRETARY_REJECTED)))
            throw new MultipleProposalOnRequestPending("You already have other pending proposal request!");
        //PARSE TEACHER
        if (!teacherRepository.existsById(proposalOnRequestDTO.getSupervisor()))
            throw new TeacherNotFoundException("Teacher not found exception");

        Teacher teacher = teacherRepository.getReferenceById(proposalOnRequestDTO.getSupervisor());
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
            throw (new ProposalInvalidStateException(PROPOSAL_ON_REQUEST_IS_NOT_PENDING));
        }
        proposal.setStatus(ProposalOnRequest.Status.TEACHER_ACCEPTED);
        proposal.setApprovalDate(new Date());

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
    public ProposalOnRequestDTO proposalOnRequestTeacherRequestChange(Long id, RequestChangeDTO requestChangeDTO) {
        ProposalOnRequest proposal = checkProposalId(id);
        if (proposal.getStatus() != ProposalOnRequest.Status.SECRETARY_ACCEPTED) {
            throw (new ProposalNotFoundException(PROPOSAL_ON_REQUEST_IS_NOT_PENDING));
        }
        if (requestChangeDTO.getRequestedChange() == null || requestChangeDTO.getRequestedChange().isBlank()) {
            throw new EmptyRequestedChangeException(REQUESTED_CHANGE_IS_EMPTY);
        }
        proposal.setStatus(ProposalOnRequest.Status.TEACHER_REVIEW);
        proposal.setRequestedChange(requestChangeDTO.getRequestedChange());
        return proposalOnRequestRepository.save(proposal).toDTO();
    }

    @Override
    public ProposalOnRequestDTO proposalOnRequestTeacherChangeStatus(Long id, ProposalOnRequest.Status status) {
        ProposalOnRequest proposal = checkProposalId(id);
        if (proposal.getStatus() != ProposalOnRequest.Status.SECRETARY_ACCEPTED) {
            throw (new ProposalNotFoundException(PROPOSAL_ON_REQUEST_IS_NOT_PENDING));
        }
        proposal.setStatus(status);
        if (status == ProposalOnRequest.Status.TEACHER_ACCEPTED)
            proposal.setApprovalDate(new Date());
        return proposalOnRequestRepository.save(proposal).toDTO();
    }

    @Override
    public List<ProposalOnRequestFullDTO> getPendingRequestsByTeacher(Long teacherMail) {
        return proposalOnRequestRepository.getProposalOnRequestsBySupervisorIdAndStatus(teacherMail, ProposalOnRequest.Status.SECRETARY_ACCEPTED).stream().map(ProposalOnRequest::toFullDTO).toList();
    }

    @Override
    public List<ProposalOnRequestFullDTO> getNotPendingRequestsByTeacher(Long teacherMail) {
        return proposalOnRequestRepository.getProposalOnRequestsBySupervisorIdAndStatusIn(teacherMail, List.of(ProposalOnRequest.Status.TEACHER_ACCEPTED, ProposalOnRequest.Status.TEACHER_REJECTED, ProposalOnRequest.Status.TEACHER_REVIEW)).stream().map(ProposalOnRequest::toFullDTO).toList();
    }

    @Override
    public List<ProposalOnRequestFullDTO> getProposalOnRequestByStudent(String studentMail) {
        return proposalOnRequestRepository.getProposalOnRequestsByStudentEmail(studentMail).stream().map(ProposalOnRequest::toFullDTO).toList();
    }

    @Override
    public ProposalOnRequestDTO proposalOnRequestMakeChanges(Long oldId, ProposalOnRequestDTO updatedProposalOnRequestDTO) {
        if(!Objects.equals(oldId, updatedProposalOnRequestDTO.getId()))
            throw new ProposalNotFoundException("The new proposal doesnt match the old id");
        Optional<ProposalOnRequest> optOld=proposalOnRequestRepository.findById(oldId);
        if(optOld.isEmpty()){
            throw new ProposalNotFoundException("The specified proposalOnRequest doesn't exists");
        }
        //MAYBE CHECK IF SOMETHING HAS CHANGED BEFORE CHANGING
        ProposalOnRequest old=optOld.get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(old.getStudent().getEmail().compareTo(auth.getName())!=0)
            throw new ProposalOwnershipException("The logged student is not authorized to do this task");
        if(old.getStatus()!= ProposalOnRequest.Status.TEACHER_REVIEW)
            throw new ProposalInvalidStateException("In order to review a proposal request the status should be TEACHER_REVIEW");
        old.setTitle(updatedProposalOnRequestDTO.getTitle());
        old.setDescription(updatedProposalOnRequestDTO.getDescription());
        if(old.getCoSupervisors()==null)
            old.setCoSupervisors(new ArrayList<>());
        else
            old.getCoSupervisors().clear();
        updatedProposalOnRequestDTO.getCoSupervisors().forEach(it->{
            Optional<Teacher> optionalTeacher=teacherRepository.findById(it);
            if(optionalTeacher.isEmpty())
                throw new TeacherNotFoundException("Cant find a teacher with the specified id");
            old.getCoSupervisors().add(optionalTeacher.get());
        });
        old.setStatus(ProposalOnRequest.Status.SECRETARY_ACCEPTED);
        return proposalOnRequestRepository.save(old).toDTO();
    }

}



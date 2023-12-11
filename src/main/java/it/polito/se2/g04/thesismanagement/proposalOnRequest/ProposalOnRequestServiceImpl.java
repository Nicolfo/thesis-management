package it.polito.se2.g04.thesismanagement.proposalOnRequest;

import it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Proposal.ProposalNotFoundException;
import it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Student.StudentNotFoundException;
import it.polito.se2.g04.thesismanagement.student.Student;
import it.polito.se2.g04.thesismanagement.student.StudentRepository;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Teacher.TeacherNotFoundException;
import it.polito.se2.g04.thesismanagement.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProposalOnRequestServiceImpl implements ProposalOnRequestService {
    private final ProposalOnRequestRepository proposalOnRequestRepository;

    private final String proposalIsNotPendingError = "Proposal On Request is not pending";

    @Override
    public List<ProposalOnRequestDTO> getAllPending() {
        return proposalOnRequestRepository.getProposalOnRequestByStatus(ProposalOnRequest.Status.PENDING).stream().map(ProposalOnRequest::toDTO).toList();
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
            throw (new ProposalNotFoundException(proposalIsNotPendingError));
        }
        proposal.setStatus(ProposalOnRequest.Status.SECRETARY_ACCEPTED);
        proposalOnRequestRepository.save(proposal);

        return proposal.toDTO();
    }

    @Override
    public ProposalOnRequestDTO proposalOnRequestSecretaryRejected(Long id) {
        ProposalOnRequest proposal = checkProposalId(id);

        if (proposal.getStatus() != ProposalOnRequest.Status.PENDING) {
            throw (new ProposalNotFoundException(proposalIsNotPendingError));
        }
        proposal.setStatus(ProposalOnRequest.Status.SECRETARY_REJECTED);
        proposalOnRequestRepository.save(proposal);

        return proposal.toDTO();
    }
    
    @Override
    public ProposalOnRequestDTO createProposalRequest(ProposalOnRequestDTO proposalOnRequestDTO) {
        //PARSE TEACHER
        if(!teacherRepository.existsById(proposalOnRequestDTO.getSupervisor()))
            throw new TeacherNotFoundException("Teacher not found exception");
        Teacher teacher = teacherRepository.getReferenceById(proposalOnRequestDTO.getId());
        //PARSE STUDENT
        if(!studentRepository.existsById(proposalOnRequestDTO.getStudentId()))
            throw new StudentNotFoundException("Student not found exception");
        Student student = studentRepository.getReferenceById(proposalOnRequestDTO.getStudentId());
        //PARSE CO-SUPERVISORS
        List<Teacher> coSupervisors;
        if(proposalOnRequestDTO.getCoSupervisors()==null || proposalOnRequestDTO.getCoSupervisors().isEmpty())
            coSupervisors= List.of();
        else{
            coSupervisors= proposalOnRequestDTO.getCoSupervisors().stream().map(it->{
                if(!teacherRepository.existsById(it))
                    throw new TeacherNotFoundException("Some co-supervisor has not been found");
                return teacherRepository.getReferenceById(it);
            }).toList();
        }
    }

    @Override
    public ProposalOnRequestDTO proposalOnRequestTeacherAccepted(Long id) {
        ProposalOnRequest proposal = checkProposalId(id);
        if (proposal.getStatus() != ProposalOnRequest.Status.PENDING) {
            throw (new ProposalNotFoundException(proposalIsNotPendingError));
        proposal.setStatus(ProposalOnRequest.Status.TEACHER_ACCEPTED);
        proposal.setApprovalDate(new Date());
        proposalOnRequestRepository.save(proposal);
        return proposal.toDTO();
    }

    @Override
    public ProposalOnRequestDTO proposalOnRequestTeacherRejected(Long id) {
        ProposalOnRequest proposal = checkProposalId(id);
        if (proposal.getStatus() != ProposalOnRequest.Status.PENDING) {
            throw (new ProposalNotFoundException(proposalIsNotPendingError));
        }
        proposal.setStatus(ProposalOnRequest.Status.TEACHER_REJECTED);
        proposalOnRequestRepository.save(proposal);
        return proposal.toDTO();
    }
    @Override
    public ProposalOnRequestDTO proposalOnRequestTeacherRequestChange(Long id) {
        ProposalOnRequest proposal = checkProposalId(id);
        if (proposal.getStatus() != ProposalOnRequest.Status.PENDING) {
            throw (new ProposalNotFoundException(proposalIsNotPendingError));
        }
        proposal.setStatus(ProposalOnRequest.Status.TEACHER_REVIEW);
        proposalOnRequestRepository.save(proposal);
        return proposal.toDTO();
    }

}



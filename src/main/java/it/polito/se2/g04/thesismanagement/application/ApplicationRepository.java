package it.polito.se2.g04.thesismanagement.application;

import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application,Long> {
    public List<Application> getApplicationByProposal_Supervisor_EmailAndStatusIsNotOrderByProposalId(String email, ApplicationStatus status);
    public List<Application> getApplicationByProposal_Id(Long proposalId);
    public List<Application> getApplicationByProposalIdAndStudentId(Long proposalId, Long studentId);
    public Application getApplicationById(Long id);
    public List<Application> getApplicationByStudentEmail(String email);
    public boolean existsApplicationByStudentIdAndStatusIn(Long studentId, Collection<ApplicationStatus> status);
    boolean existsByProposalAndStudent(Proposal proposal, Student student);
    boolean existsApplicationByStudentEmail(String email);
    boolean existsApplicationByProposalId(Long id);


}

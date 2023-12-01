package it.polito.se2.g04.thesismanagement.proposal;

import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposalRepository extends JpaRepository<Proposal,Long> {
    List<Proposal> findAllBySupervisorAndStatusOrderById(Teacher supervisor, Proposal.Status status);
    List<Proposal> findAllByCoSupervisorsContainsAndStatusOrderById(Teacher coSupervisor, Proposal.Status status);
    List<Proposal> findAllByStatus(Proposal.Status status);
}

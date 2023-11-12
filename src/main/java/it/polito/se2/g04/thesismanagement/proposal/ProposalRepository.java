package it.polito.se2.g04.thesismanagement.proposal;

import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposalRepository extends JpaRepository<Proposal,Long> {
    List<Proposal> findAllBySupervisorAndArchived(Teacher supervisor, boolean archived);
    List<Proposal> findAllByCoSupervisorsContainsAndArchived(Teacher coSupervisor, boolean archived);
    List<Proposal> findAllByArchived(boolean archived);
}

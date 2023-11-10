package it.polito.se2.g04.thesismanagement.proposal;

import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import it.polito.se2.g04.thesismanagement.teacher.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProposalServiceImpl implements ProposalService {
    private final ProposalRepository proposalRepository;
    private final TeacherRepository teacherRepository;

    public ProposalServiceImpl(ProposalRepository proposalRepository, TeacherRepository teacherRepository) {
        this.proposalRepository = proposalRepository;
        this.teacherRepository = teacherRepository;
    }


    @Override
    public List<Proposal> getAllProposals(){
        return proposalRepository.findAll();
    }

    @Override
    public List<Proposal> getProposalsByProf(String UserName){
        Teacher teacher = teacherRepository.findByEmail(UserName);
        if (teacher != null) {
            List<Proposal> supervisorProposals = proposalRepository.findAllBySupervisorAndArchived(teacher, false);
            List<Proposal> coSupervisorProposals = proposalRepository.findAllByCoSupervisorsContainsAndArchived(teacher, false);
            supervisorProposals.addAll(coSupervisorProposals);
            return supervisorProposals;
        }
        return new ArrayList<>();
    }

    @Override
    public List<Proposal> getAllNotArchivedProposals(){
        return proposalRepository.findAllByArchived(false);
    }
}
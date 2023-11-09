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
    public List<Proposal> getProposalsByProf(long teacherId){
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        if (teacher != null) {
            List<Proposal> supervisorProposals = proposalRepository.findAllBySupervisor(teacher);
            List<Proposal> coSupervisorProposals = proposalRepository.findAllByCoSupervisorsContains(teacher);
            supervisorProposals.addAll(coSupervisorProposals);
            return supervisorProposals;
        }
        return new ArrayList<>();
    }
}
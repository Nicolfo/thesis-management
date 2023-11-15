package it.polito.se2.g04.thesismanagement.proposal;

import it.polito.se2.g04.thesismanagement.application.Application;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import it.polito.se2.g04.thesismanagement.teacher.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
    public String getTitleByProposalId(Long proposalId) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found with id: " + proposalId));

        if (proposal == null || proposal.getTitle() == null) {
            return null;
        }
        return proposal.getTitle();
    }

    @Override
    public Proposal createProposal(ProposalDTO proposalDTO) {
        Proposal toAdd=new Proposal(
                proposalDTO.getTitle(),
                teacherRepository.getReferenceById(proposalDTO.getSupervisorId()),
                proposalDTO.getCoSupervisors().stream().map(it->teacherRepository.getReferenceById(it)).toList(),
                proposalDTO.getKeywords(),
                proposalDTO.getType(),
                Stream.concat(Stream.of(teacherRepository.getReferenceById(proposalDTO.getSupervisorId()).getGroup()),proposalDTO.getCoSupervisors().stream().map(it->teacherRepository.getReferenceById(it).getGroup())).toList(),
                proposalDTO.getDescription(),
                proposalDTO.getRequiredKnowledge(),
                proposalDTO.getNotes(),
                proposalDTO.getExpiration(),
                proposalDTO.getLevel(),
                proposalDTO.getCdS()
        );
        return proposalRepository.save(toAdd) ;
    }

    @Override
    public Proposal updateProposal(Long id, ProposalDTO proposalDTO) {
        Proposal old= proposalRepository.getReferenceById(id);
        old.setTitle(proposalDTO.getTitle());
                old.setSupervisor(teacherRepository.getReferenceById(proposalDTO.getSupervisorId()));
                old.setCoSupervisors(proposalDTO.getCoSupervisors().stream().map(it->teacherRepository.getReferenceById(it)).toList());
                old.setType(proposalDTO.getType());
                old.setGroups(Stream.concat(Stream.of(teacherRepository.getReferenceById(proposalDTO.getSupervisorId()).getGroup()),proposalDTO.getCoSupervisors().stream().map(it->teacherRepository.getReferenceById(it).getGroup())).toList());
                old.setDescription(proposalDTO.getDescription());
                old.setRequiredKnowledge(proposalDTO.getRequiredKnowledge());
                old.setNotes(proposalDTO.getNotes());
                old.setExpiration(proposalDTO.getExpiration());
                old.setLevel(proposalDTO.getLevel());
                old.setCdS(proposalDTO.getCdS());
                old.setKeywords(proposalDTO.getKeywords());

        return proposalRepository.save(old);
    }

    @Override
    public List<Proposal> getAllNotArchivedProposals(){
        return proposalRepository.findAllByArchived(false);
    }
}
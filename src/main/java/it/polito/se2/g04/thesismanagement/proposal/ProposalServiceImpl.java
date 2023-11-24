package it.polito.se2.g04.thesismanagement.proposal;

import it.polito.se2.g04.thesismanagement.group.Group;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import it.polito.se2.g04.thesismanagement.teacher.TeacherRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ProposalServiceImpl implements ProposalService {
    private final ProposalRepository proposalRepository;
    private final TeacherRepository teacherRepository;
    @PersistenceContext
    private EntityManager entityManager;

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
            List<Proposal> supervisorProposals = proposalRepository.findAllBySupervisorAndArchivedOrderById(teacher, false);
            /*List<Proposal> coSupervisorProposals = proposalRepository.findAllByCoSupervisorsContainsAndArchivedOrderById(teacher, false);
            supervisorProposals.addAll(coSupervisorProposals);*/
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
        Teacher teacher=teacherRepository.getReferenceById(proposalDTO.getSupervisorId());
        Proposal toAdd=new Proposal(
                proposalDTO.getTitle(),
                teacher,
                proposalDTO.getCoSupervisors().stream().map(it->teacherRepository.getReferenceById(it)).toList(),
                proposalDTO.getKeywords(),
                proposalDTO.getType(),
                Stream.concat(Stream.of(teacherRepository.getReferenceById(proposalDTO.getSupervisorId()).getGroup()),proposalDTO.getCoSupervisors().stream().map(it->teacherRepository.getReferenceById(it).getGroup())).toList(),
                proposalDTO.getDescription(),
                proposalDTO.getRequiredKnowledge(),
                proposalDTO.getNotes(),
                proposalDTO.getExpiration(),
                proposalDTO.getLevel(),
                proposalDTO.getCds()
        );
        System.out.println( toAdd);
        return proposalRepository.save(toAdd) ;
    }

    @Override
    public Proposal updateProposal(Long id, ProposalDTO proposalDTO) {
        Proposal old= proposalRepository.getReferenceById(id);
        old.setTitle(proposalDTO.getTitle());
                old.setSupervisor(teacherRepository.getReferenceById(proposalDTO.getSupervisorId()));
                if(proposalDTO.getCoSupervisors().size()>0) {
                    old.getCoSupervisors().clear();
                    proposalDTO.getCoSupervisors().stream().forEach(it -> old.getCoSupervisors().add(teacherRepository.getReferenceById(it)));
                }
                old.setType(proposalDTO.getType());
                old.getGroups().clear();
                old.getGroups().addAll(Stream.concat(Stream.of(teacherRepository.getReferenceById(proposalDTO.getSupervisorId()).getGroup()),proposalDTO.getCoSupervisors().stream().map(it->teacherRepository.getReferenceById(it).getGroup())).toList());
                old.setDescription(proposalDTO.getDescription());
                old.setRequiredKnowledge(proposalDTO.getRequiredKnowledge());
                old.setNotes(proposalDTO.getNotes());
                old.setExpiration(proposalDTO.getExpiration());
                old.setLevel(proposalDTO.getLevel());
                old.setCdS(proposalDTO.getCds());
                old.setKeywords(proposalDTO.getKeywords());

        return proposalRepository.save(old);
    }

    @Override
    public List<Proposal> getAllNotArchivedProposals(){
        return proposalRepository.findAllByArchived(false);
    }
    @Query
    @Override
    public List<Proposal> searchProposals(ProposalSearchRequest proposalSearchRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Proposal> cq = cb.createQuery(Proposal.class);

        Root<Proposal> proposal = cq.from(Proposal.class);
        List<Predicate> predicates = new ArrayList<>();

        // We gradually add predicates to the query, depending on
        // the specified filters.

        predicates.add(cb.like(cb.upper(proposal.get("cds")), "%" + proposalSearchRequest.getCdS().toUpperCase() + "%"));
        if (proposalSearchRequest.getTitle() != null) {
            predicates.add(cb.like(cb.upper(proposal.get("title")), "%" + proposalSearchRequest.getTitle().toUpperCase() + "%"));
        }
        if (proposalSearchRequest.getKeywords() != null) {
            List<String> keywordList = List.of(proposalSearchRequest.getKeywords().split("[\\s,]+"));
            for (String keyword : keywordList) {
                predicates.add(cb.like(cb.upper(proposal.get("keywords")), "%" + keyword.toUpperCase() + "%"));
            }
        }
        if (proposalSearchRequest.getType() != null) {
            List<String> typeList = List.of(proposalSearchRequest.getType().split("[\\s,]+"));
            for (String type : typeList) {
                predicates.add(cb.like(cb.upper(proposal.get("type")), "%" + type.toUpperCase() + "%"));
            }
        }
        if (proposalSearchRequest.getDescription() != null) {
            predicates.add(cb.like(cb.upper(proposal.get("description")), "%" + proposalSearchRequest.getDescription().toUpperCase() + "%"));
        }
        if (proposalSearchRequest.getRequiredKnowledge() != null) {
            List<String> requirementList = List.of(proposalSearchRequest.getRequiredKnowledge().split("[\\s,]+"));
            for (String requirement : requirementList) {
                predicates.add(cb.like(cb.upper(proposal.get("requiredKnowledge")), "%" + requirement.toUpperCase() + "%"));
            }
        }
        if (proposalSearchRequest.getNotes() != null) {
            predicates.add(cb.like(cb.upper(proposal.get("notes")), "%" + proposalSearchRequest.getNotes().toUpperCase() + "%"));
        }
//        if (proposalSearchRequest.getMinExpiration() != null) {
//            predicates.add(cb.greaterThanOrEqualTo(proposal.<Timestamp>get("expiration"), proposalSearchRequest.getMinExpiration()));
//        }
//        if (proposalSearchRequest.getMaxExpiration() != null) {
//            predicates.add(cb.lessThanOrEqualTo(proposal.<Timestamp>get("expiration"), proposalSearchRequest.getMaxExpiration()));
//        }

        cq.where(predicates.toArray(new Predicate[0]));

        List<Proposal> proposalList = entityManager.createQuery(cq).getResultList();

        // Filter proposals that don't have all the co-supervisors
        // in the filter or all the specified groups

        List<Proposal> filteredList = new ArrayList<>();
        for (Proposal prop : proposalList) {
            boolean include = true;
            // Check if it has all the supervisors
            if (proposalSearchRequest.getSupervisorIdList() != null) {
                if (!proposalSearchRequest.getSupervisorIdList().contains(prop.getSupervisor().getId())) {
                    include = false;
                }
            }
            // Check if it has all the co-supervisors
            if (proposalSearchRequest.getCoSupervisorIdList() != null) {
                List<Long> coSupervisorIdList = prop.getCoSupervisors().stream().map(Teacher::getId).toList();
                for (Long filterId : proposalSearchRequest.getCoSupervisorIdList()) {
                    if (!coSupervisorIdList.contains(filterId)) {
                        include = false;
                        break;
                    }
                }
            }
            // Check if it has all the groups
            if (proposalSearchRequest.getCodGroupList() != null) {
                List<Long> codGroupList = prop.getGroups().stream().map(Group::getCodGroup).toList();
                for (Long codGroup : proposalSearchRequest.getCodGroupList()) {
                    if (!codGroupList.contains(codGroup)) {
                        include = false;
                        break;
                    }
                }
            }
            if (include) {
                filteredList.add(prop);
            }
        }

        return filteredList;
    }
}
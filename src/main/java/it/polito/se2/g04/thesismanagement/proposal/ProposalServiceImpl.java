package it.polito.se2.g04.thesismanagement.proposal;

import it.polito.se2.g04.thesismanagement.application.Application;
import it.polito.se2.g04.thesismanagement.application.ApplicationRepository;
import it.polito.se2.g04.thesismanagement.application.ApplicationStatus;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProposalServiceImpl implements ProposalService {
    private final ProposalRepository proposalRepository;
    private final TeacherRepository teacherRepository;
    private final ApplicationRepository applicationRepository;
    @PersistenceContext
    private EntityManager entityManager;
    private static final String PROPOSAL_ID_NOT_EXISTS = "Proposal with this id does not exist";
    private static final String REGEX_PATTERN = "[\\s,]+";

    public ProposalServiceImpl(ProposalRepository proposalRepository, TeacherRepository teacherRepository, ApplicationRepository applicationRepository) {
        this.proposalRepository = proposalRepository;
        this.teacherRepository = teacherRepository;
        this.applicationRepository= applicationRepository;
    }


    @Override
    public List<ProposalFullDTO> getAllProposals(){
        return proposalRepository.findAll().stream().map(ProposalFullDTO::fromProposal).toList();
    }

    @Override
    public List<ProposalFullDTO> getProposalsByProf(String userName){
        Teacher teacher = teacherRepository.findByEmail(userName);
        if (teacher != null) {
            List<Proposal> supervisorProposals = proposalRepository.findAllBySupervisorAndStatusOrderById(teacher, Proposal.Status.ACTIVE);
            return supervisorProposals.stream().map(ProposalFullDTO::fromProposal).toList();
        }
        return new ArrayList<>();
    }


    @Override
    public List<ProposalFullDTO> getArchivedProposals(String userName){
        Teacher teacher = teacherRepository.findByEmail(userName);
        if (teacher != null) {
            List<Proposal> supervisorProposals = proposalRepository.findAllBySupervisorAndStatusOrderById(teacher, Proposal.Status.ARCHIVED);
            return supervisorProposals.stream().map(ProposalFullDTO::fromProposal).toList();
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
    public ProposalFullDTO createProposal(ProposalDTO proposalDTO) {
        Teacher teacher=teacherRepository.getReferenceById(proposalDTO.getSupervisorId());
        Proposal toAdd=new Proposal(
                proposalDTO.getTitle(),
                teacher,
                proposalDTO.getCoSupervisors() != null ? proposalDTO.getCoSupervisors().stream().map(teacherRepository::getReferenceById).collect(Collectors.toList()) : null,
                proposalDTO.getKeywords(),
                proposalDTO.getType(),
                Stream.concat(Stream.of(teacherRepository.getReferenceById(proposalDTO.getSupervisorId()).getGroup()),proposalDTO.getCoSupervisors() != null ? proposalDTO.getCoSupervisors().stream().map(it->teacherRepository.getReferenceById(it).getGroup()) : Stream.of()).collect(Collectors.toList()),
                proposalDTO.getDescription(),
                proposalDTO.getRequiredKnowledge(),
                proposalDTO.getNotes(),
                proposalDTO.getExpiration(),
                proposalDTO.getLevel(),
                proposalDTO.getCds()
        );
        return ProposalFullDTO.fromProposal(proposalRepository.save(toAdd));
    }

    @Override
    public ProposalFullDTO updateProposal(Long id, ProposalDTO proposalDTO) {
        if (!proposalRepository.existsById(id))
            throw (new ProposalNotFoundException(PROPOSAL_ID_NOT_EXISTS));

        Proposal old = proposalRepository.getReferenceById(id);
        if(old.getStatus()==Proposal.Status.ACCEPTED){
            throw (new updateAfterAccepted("you can't update this proposal after an application to it is accepted"));
        }
        old.setTitle(proposalDTO.getTitle());
        old.setSupervisor(teacherRepository.getReferenceById(proposalDTO.getSupervisorId()));
        if(proposalDTO.getCoSupervisors() != null && !proposalDTO.getCoSupervisors().isEmpty()) {
            old.setCoSupervisors(proposalDTO.getCoSupervisors().stream().map(teacherRepository::getReferenceById).collect(Collectors.toList()));
        }
        old.setType(proposalDTO.getType());
        old.setGroups(Stream.concat(Stream.of(teacherRepository.getReferenceById(proposalDTO.getSupervisorId()).getGroup()),proposalDTO.getCoSupervisors() != null ? proposalDTO.getCoSupervisors().stream().map(it->teacherRepository.getReferenceById(it).getGroup()) : Stream.of()).collect(Collectors.toList()));
        old.setDescription(proposalDTO.getDescription());
        old.setRequiredKnowledge(proposalDTO.getRequiredKnowledge());
        old.setNotes(proposalDTO.getNotes());
        old.setExpiration(proposalDTO.getExpiration());
        old.setLevel(proposalDTO.getLevel());
        old.setCds(proposalDTO.getCds());
        old.setKeywords(proposalDTO.getKeywords());

        Proposal updated = proposalRepository.save(old);
        return ProposalFullDTO.fromProposal(updated);
    }

    @Override
    public List<ProposalFullDTO> getAllNotArchivedProposals() {
        return proposalRepository.findAllByStatus(Proposal.Status.ACTIVE).stream().map(ProposalFullDTO::fromProposal).toList();
    }
  
    private void addPredicates(ProposalSearchRequest proposalSearchRequest, CriteriaBuilder cb, Root<Proposal> proposal, List<Predicate> predicates, Proposal.Status status) {
        if (proposalSearchRequest.getCds() != null) {
            predicates.add(cb.like(cb.upper(proposal.get("cds")), "%" + proposalSearchRequest.getCds().toUpperCase() + "%"));
        }
        if (proposalSearchRequest.getTitle() != null) {
            predicates.add(cb.like(cb.upper(proposal.get("title")), "%" + proposalSearchRequest.getTitle().toUpperCase() + "%"));
        }
        if (proposalSearchRequest.getKeywords() != null) {
            List<String> keywordList = List.of(proposalSearchRequest.getKeywords().split(REGEX_PATTERN));
            for (String keyword : keywordList) {
                predicates.add(cb.like(cb.upper(proposal.get("keywords")), "%" + keyword.toUpperCase() + "%"));
            }
        }
        if (proposalSearchRequest.getType() != null) {
            List<String> typeList = List.of(proposalSearchRequest.getType().split(REGEX_PATTERN));
            for (String type : typeList) {
                predicates.add(cb.like(cb.upper(proposal.get("type")), "%" + type.toUpperCase() + "%"));
            }
        }
        if (proposalSearchRequest.getDescription() != null) {
            predicates.add(cb.like(cb.upper(proposal.get("description")), "%" + proposalSearchRequest.getDescription().toUpperCase() + "%"));
        }
        if (proposalSearchRequest.getRequiredKnowledge() != null) {
            List<String> requirementList = List.of(proposalSearchRequest.getRequiredKnowledge().split(REGEX_PATTERN));
            for (String requirement : requirementList) {
                predicates.add(cb.like(cb.upper(proposal.get("requiredKnowledge")), "%" + requirement.toUpperCase() + "%"));
            }
        }
        if (proposalSearchRequest.getNotes() != null) {
            predicates.add(cb.like(cb.upper(proposal.get("notes")), "%" + proposalSearchRequest.getNotes().toUpperCase() + "%"));
        }
        if (proposalSearchRequest.getLevel() != null) {
            if (!List.of("Bachelor's", "Master's").contains(proposalSearchRequest.getLevel())) {
                throw new ProposalLevelInvalidException("Proposal level '" + proposalSearchRequest.getLevel() + "' is not valid");
            }
            predicates.add(cb.equal(cb.upper(proposal.get("level")), proposalSearchRequest.getLevel().toUpperCase()));
        }
        if (status != null) {
            predicates.add(cb.equal(proposal.get("status"), status));
        }
    }


    @Query
    @Override
    public List<ProposalFullDTO> searchProposals(ProposalSearchRequest proposalSearchRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Proposal> cq = cb.createQuery(Proposal.class);

        Root<Proposal> proposal = cq.from(Proposal.class);
        List<Predicate> predicates = new ArrayList<>();

        addPredicates(proposalSearchRequest, cb, proposal, predicates, Proposal.Status.ACTIVE);

        cq.where(predicates.toArray(new Predicate[0]));

        List<Proposal> proposalList = entityManager.createQuery(cq).getResultList();

        // Filter proposals that don't have all the co-supervisors
        // in the filter or all the specified groups

        List<Proposal> filteredList = new ArrayList<>();
        for (Proposal prop : proposalList) {
            boolean include = proposalSearchRequest.getSupervisorIdList() == null || proposalSearchRequest.getSupervisorIdList().contains(prop.getSupervisor().getId());
            // Check if it has all the supervisors
            // Check if it has at least one of the co-supervisors
            if (proposalSearchRequest.getCoSupervisorIdList() != null) {
                List<Long> coSupervisorIdList = prop.getCoSupervisors().stream().map(Teacher::getId).toList();
                include = false;
                for (Long filterId : proposalSearchRequest.getCoSupervisorIdList()) {
                    if (coSupervisorIdList.contains(filterId)) {
                        include = true;
                        break;
                    }
                }
            }
            // Check if it has at least one of the groups
            if (proposalSearchRequest.getCodGroupList() != null) {
                List<Long> codGroupList = prop.getGroups().stream().map(Group::getCodGroup).toList();
                include = false;
                for (Long codGroup : proposalSearchRequest.getCodGroupList()) {
                    if (codGroupList.contains(codGroup)) {
                        include = true;
                        break;
                    }
                }
            }
            if (include) {
                filteredList.add(prop);
            }
        }

        return filteredList.stream().map(ProposalFullDTO::fromProposal).toList();
    }

    @Query
    @Override
    public List <ProposalFullDTO> searchArchivedProposals(ProposalSearchRequest proposalSearchRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Proposal> cq = cb.createQuery(Proposal.class);

        Root<Proposal> proposal = cq.from(Proposal.class);
        List<Predicate> predicates = new ArrayList<>();

        addPredicates(proposalSearchRequest, cb, proposal, predicates, Proposal.Status.ARCHIVED);

        cq.where(predicates.toArray(new Predicate[0]));

        List<Proposal> proposalList = entityManager.createQuery(cq).getResultList();

        // Filter proposals that don't have all the co-supervisors
        // in the filter or all the specified groups

        List<Proposal> filteredList = new ArrayList<>();
        for (Proposal prop : proposalList) {
            boolean include = proposalSearchRequest.getSupervisorIdList() == null || proposalSearchRequest.getSupervisorIdList().contains(prop.getSupervisor().getId());
            // Check if it has all the supervisors
            // Check if it has at least one of the co-supervisors
            if (proposalSearchRequest.getCoSupervisorIdList() != null) {
                List<Long> coSupervisorIdList = prop.getCoSupervisors().stream().map(Teacher::getId).toList();
                include = false;
                for (Long filterId : proposalSearchRequest.getCoSupervisorIdList()) {
                    if (coSupervisorIdList.contains(filterId)) {
                        include = true;
                        break;
                    }
                }
            }
            // Check if it has at least one of the groups
            if (proposalSearchRequest.getCodGroupList() != null) {
                List<Long> codGroupList = prop.getGroups().stream().map(Group::getCodGroup).toList();
                include = false;
                for (Long codGroup : proposalSearchRequest.getCodGroupList()) {
                    if (codGroupList.contains(codGroup)) {
                        include = true;
                        break;
                    }
                }
            }
            if (include) {
                filteredList.add(prop);
            }
        }

        return filteredList.stream().map(ProposalFullDTO::fromProposal).toList();
    }


    @Override
    public ProposalFullDTO archiveProposal(Long id) {
        if (!proposalRepository.existsById(id)) {
            throw (new ProposalNotFoundException("Proposal with this id does not exist"));
        }
        Proposal old = proposalRepository.getReferenceById(id);
        if (old.getStatus() == Proposal.Status.ARCHIVED) {
            throw (new ProposalNotFoundException("Proposal already archived"));
        } else {
            old.setStatus(Proposal.Status.ARCHIVED);
        }
        return ProposalFullDTO.fromProposal(proposalRepository.save(old));
    }

    @Override
    public void deleteProposal(Long id) {
        if (!proposalRepository.existsById(id))
            throw (new ProposalNotFoundException("Proposal with this id does not exist"));

        applicationRepository.getApplicationByProposal_Id(id).forEach(it -> {
                    Application application = applicationRepository.getReferenceById(it.getId());
                    application.setStatus(ApplicationStatus.DELETED);
                    applicationRepository.save(application);
                }
        );
        Proposal proposal = proposalRepository.getReferenceById(id);
        proposal.setStatus(Proposal.Status.DELETED);
        proposalRepository.save(proposal);
    }
}
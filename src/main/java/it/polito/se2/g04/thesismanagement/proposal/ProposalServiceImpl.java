package it.polito.se2.g04.thesismanagement.proposal;

import it.polito.se2.g04.thesismanagement.application.ApplicationRepository;
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
        this.applicationRepository = applicationRepository;
    }


    @Override
    public List<ProposalFullDTO> getAllProposals() {
        return proposalRepository.findAll().stream().map(ProposalFullDTO::fromProposal).toList();
    }

    @Override
    public List<ProposalFullDTO> getProposalsByProf(String userName) {
        Teacher teacher = teacherRepository.findByEmail(userName);
        if (teacher != null) {
            List<Proposal> supervisorProposals = proposalRepository.findAllBySupervisorAndArchivedOrderById(teacher, false);
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
        Teacher teacher = teacherRepository.getReferenceById(proposalDTO.getSupervisorId());
        Proposal toAdd = new Proposal(
                proposalDTO.getTitle(),
                teacher,
                proposalDTO.getCoSupervisors() != null ? proposalDTO.getCoSupervisors().stream().map(teacherRepository::getReferenceById).collect(Collectors.toList()) : null,
                proposalDTO.getKeywords(),
                proposalDTO.getType(),
                Stream.concat(Stream.of(teacherRepository.getReferenceById(proposalDTO.getSupervisorId()).getGroup()), proposalDTO.getCoSupervisors() != null ? proposalDTO.getCoSupervisors().stream().map(it -> teacherRepository.getReferenceById(it).getGroup()) : Stream.of()).collect(Collectors.toList()),
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
        old.setTitle(proposalDTO.getTitle());
        old.setSupervisor(teacherRepository.getReferenceById(proposalDTO.getSupervisorId()));
        if (proposalDTO.getCoSupervisors() != null && !proposalDTO.getCoSupervisors().isEmpty()) {
            old.setCoSupervisors(proposalDTO.getCoSupervisors().stream().map(teacherRepository::getReferenceById).collect(Collectors.toList()));
        }
        old.setType(proposalDTO.getType());
        old.setGroups(Stream.concat(Stream.of(teacherRepository.getReferenceById(proposalDTO.getSupervisorId()).getGroup()), proposalDTO.getCoSupervisors() != null ? proposalDTO.getCoSupervisors().stream().map(it -> teacherRepository.getReferenceById(it).getGroup()) : Stream.of()).collect(Collectors.toList()));
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
        return proposalRepository.findAllByArchived(false).stream().map(ProposalFullDTO::fromProposal).toList();
    }

    private void addPredicates(ProposalSearchRequest proposalSearchRequest, CriteriaBuilder cb, Root<Proposal> proposal, List<Predicate> predicates) {
        predicates.add(cb.like(cb.upper(proposal.get("CdS")), "%" + proposalSearchRequest.getCdS().toUpperCase() + "%"));

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
    }


    @Query
    @Override
    public List<ProposalFullDTO> searchProposals(ProposalSearchRequest proposalSearchRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Proposal> cq = cb.createQuery(Proposal.class);

        Root<Proposal> proposal = cq.from(Proposal.class);
        List<Predicate> predicates = new ArrayList<>();

        addPredicates(proposalSearchRequest, cb, proposal, predicates);

        cq.where(predicates.toArray(new Predicate[0]));

        List<Proposal> proposalList = entityManager.createQuery(cq).getResultList();

        return proposalList.stream()
                .filter(prop -> shouldIncludeProposal(proposalSearchRequest, prop))
                .map(ProposalFullDTO::fromProposal)
                .toList();
    }

    private boolean shouldIncludeProposal(ProposalSearchRequest proposalSearchRequest, Proposal prop) {
        if (proposalSearchRequest.getSupervisorIdList() != null && !proposalSearchRequest.getSupervisorIdList().contains(prop.getSupervisor().getId())) {
            return false;
        }
        if (proposalSearchRequest.getCoSupervisorIdList() != null && !hasMatchingCoSupervisor(proposalSearchRequest, prop)) {
            return false;
        }
        return proposalSearchRequest.getCodGroupList() == null || hasMatchingGroup(proposalSearchRequest, prop);
    }

    private boolean hasMatchingCoSupervisor(ProposalSearchRequest proposalSearchRequest, Proposal prop) {
        List<Long> coSupervisorIdList = prop.getCoSupervisors().stream().map(Teacher::getId).toList();
        for (Long filterId : proposalSearchRequest.getCoSupervisorIdList()) {
            if (coSupervisorIdList.contains(filterId)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasMatchingGroup(ProposalSearchRequest proposalSearchRequest, Proposal prop) {
        List<Long> codGroupList = prop.getGroups().stream().map(Group::getCodGroup).toList();
        for (Long codGroup : proposalSearchRequest.getCodGroupList()) {
            if (codGroupList.contains(codGroup)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Proposal archiveProposal(Long id) {
        if (!proposalRepository.existsById(id)) {
            throw (new ProposalNotFoundException(PROPOSAL_ID_NOT_EXISTS));
        }
        Proposal old = proposalRepository.getReferenceById(id);
        if (old.getArchived()) {
            throw (new ProposalNotFoundException("Proposal already archived"));
        } else {
            old.setArchived(true);
        }

        return proposalRepository.save(old);
    }

    @Override
    public void deleteProposal(Long id) {
        if (!proposalRepository.existsById(id))
            throw (new ProposalNotFoundException(PROPOSAL_ID_NOT_EXISTS));

        applicationRepository.getApplicationByProposal_Id(id).forEach(it -> applicationRepository.deleteById(it.getId()));
        proposalRepository.deleteById(id);
    }
}
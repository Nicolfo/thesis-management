package it.polito.se2.g04.thesismanagement.proposal;

import it.polito.se2.g04.thesismanagement.application.Application;
import it.polito.se2.g04.thesismanagement.application.ApplicationRepository;
import it.polito.se2.g04.thesismanagement.application.ApplicationStatus;
import it.polito.se2.g04.thesismanagement.email.EmailService;
import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal.ProposalNotFoundException;
import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal.UpdateAfterAcceptException;
import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.teacher.TeacherNotFoundException;
import it.polito.se2.g04.thesismanagement.group.Group;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import it.polito.se2.g04.thesismanagement.teacher.TeacherRepository;
import it.polito.se2.g04.thesismanagement.virtualclock.VirtualClockController;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private final EmailService emailService;
    private static final String PROPOSAL_ID_NOT_EXISTS = "Proposal with this id does not exist";
    private static final String REGEX_PATTERN = "[\\s,]+";

    public ProposalServiceImpl(ProposalRepository proposalRepository, TeacherRepository teacherRepository, ApplicationRepository applicationRepository, EmailService emailService) {
        this.proposalRepository = proposalRepository;
        this.teacherRepository = teacherRepository;
        this.applicationRepository = applicationRepository;
        this.emailService = emailService;
    }


    @Override
    public List<ProposalFullDTO> getAllProposals() {
        return proposalRepository.findAll().stream().map(ProposalFullDTO::fromProposal).toList();
    }

    @Override
    public List<ProposalFullDTO> getProposalsByProf(String userName) {
        Teacher teacher = teacherRepository.findByEmail(userName);
        if (teacher == null)
            throw new TeacherNotFoundException("Can't find the specified teacher on db!");
        List<Proposal> supervisorProposals = proposalRepository.findAllBySupervisorAndStatusOrderById(teacher, Proposal.Status.ACTIVE);
        return supervisorProposals.stream().map(ProposalFullDTO::fromProposal).toList();

    }


    @Override
    public List<ProposalFullDTO> getArchivedProposals(String userName) {
        Teacher teacher = teacherRepository.findByEmail(userName);
        if (teacher == null)
            throw new TeacherNotFoundException("Can't find the specified teacher on db!");

        List<Proposal> supervisorProposals = proposalRepository.findAllBySupervisorAndStatusOrderById(teacher, Proposal.Status.ARCHIVED);
        supervisorProposals.addAll(proposalRepository.findAllBySupervisorAndStatusOrderById(teacher, Proposal.Status.ACCEPTED));
        return supervisorProposals.stream().map(ProposalFullDTO::fromProposal).toList();
    }

    @Override
    public String getTitleByProposalId(Long proposalId) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ProposalNotFoundException("Proposal not found with id: " + proposalId));

        return proposal.getTitle();
    }

    @Override
    public void createProposal(ProposalDTO proposalDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Teacher teacher = teacherRepository.getReferenceById(proposalDTO.getSupervisorId());
        if (teacher == null || teacher.getEmail().compareTo(auth.getName()) != 0)
            throw new TeacherNotFoundException("Defined teacher is invalid!");
        Proposal toAdd = new Proposal();
        toAdd.setTitle(proposalDTO.getTitle());
        toAdd.setSupervisor(teacher);
        toAdd.setCoSupervisors(proposalDTO.getCoSupervisors() != null ? proposalDTO.getCoSupervisors().stream().map(teacherRepository::getReferenceById).collect(Collectors.toList()) : List.of());
        toAdd.setKeywords(proposalDTO.getKeywords());
        toAdd.setType(proposalDTO.getType());
        toAdd.setGroups(Stream.concat(Stream.of(teacherRepository.getReferenceById(proposalDTO.getSupervisorId()).getGroup()), proposalDTO.getCoSupervisors() != null ? proposalDTO.getCoSupervisors().stream().map(it -> teacherRepository.getReferenceById(it).getGroup()) : Stream.of()).distinct().toList());
        toAdd.setDescription(proposalDTO.getDescription());
        toAdd.setRequiredKnowledge(proposalDTO.getRequiredKnowledge());
        toAdd.setNotes(proposalDTO.getNotes());
        toAdd.setExpiration(proposalDTO.getExpiration());
        toAdd.setLevel(proposalDTO.getLevel());
        toAdd.setCds(proposalDTO.getCds());

        ProposalFullDTO.fromProposal(proposalRepository.save(toAdd));
    }

    @Override
    public void updateProposal(Long id, ProposalDTO proposalDTO) {
        if (!proposalRepository.existsById(id))
            throw (new ProposalNotFoundException(PROPOSAL_ID_NOT_EXISTS));

        Proposal old = proposalRepository.getReferenceById(id);
        if (old.getStatus() == Proposal.Status.ACCEPTED) {
            throw (new UpdateAfterAcceptException("you can't update this proposal after an application to it is accepted"));
        }
        old.setTitle(proposalDTO.getTitle());
        old.setSupervisor(teacherRepository.getReferenceById(proposalDTO.getSupervisorId()));
        if (proposalDTO.getCoSupervisors() != null && !proposalDTO.getCoSupervisors().isEmpty()) {
            old.setCoSupervisors(proposalDTO.getCoSupervisors().stream().map(teacherRepository::getReferenceById).collect(Collectors.toList()));
        }
        old.setType(proposalDTO.getType());
        old.setGroups(Stream.concat(Stream.of(teacherRepository.getReferenceById(proposalDTO.getSupervisorId()).getGroup()), proposalDTO.getCoSupervisors() != null ? proposalDTO.getCoSupervisors().stream().map(it -> teacherRepository.getReferenceById(it).getGroup()) : Stream.of()).distinct().collect(Collectors.toList()));
        old.setDescription(proposalDTO.getDescription());
        old.setRequiredKnowledge(proposalDTO.getRequiredKnowledge());
        old.setNotes(proposalDTO.getNotes());
        old.setExpiration(proposalDTO.getExpiration());
        old.setLevel(proposalDTO.getLevel());
        old.setCds(proposalDTO.getCds());
        old.setKeywords(proposalDTO.getKeywords());

        Proposal updated = proposalRepository.save(old);
        ProposalFullDTO.fromProposal(updated);
    }

    @Override
    public List<ProposalFullDTO> getAllNotArchivedProposals() {
        return proposalRepository.findAllByStatus(Proposal.Status.ACTIVE).stream().map(ProposalFullDTO::fromProposal).toList();
    }


    private void addPredicates(ProposalSearchRequest proposalSearchRequest, CriteriaBuilder cb, Root<Proposal> proposal, List<Predicate> predicates) {
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
    }




    @Query
    @Override
    public List<ProposalFullDTO> searchProposals(ProposalSearchRequest proposalSearchRequest,List<Proposal.Status> statuses) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Proposal> cq = cb.createQuery(Proposal.class);

        Root<Proposal> proposal = cq.from(Proposal.class);
        List<Predicate> predicates = new ArrayList<>();

        addPredicates(proposalSearchRequest, cb, proposal, predicates);

        predicates.add(proposal.get("status").in(statuses));

        cq.where(predicates.toArray(new Predicate[0]));

        List<Proposal> proposalList = entityManager.createQuery(cq).getResultList();

        // Filter proposals that don't have all the co-supervisors
        // in the filter or all the specified groups

        List<Proposal> filteredList = new ArrayList<>();
        for (Proposal prop : proposalList) {
            boolean include = proposalSearchRequest.getSupervisorIdList() == null || proposalSearchRequest.getSupervisorIdList().contains(prop.getSupervisor().getId());
            // Check if it has all the supervisors
            // Check if it has at least one of the co-supervisors
            if (include && proposalSearchRequest.getCoSupervisorIdList() != null) {
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
            if (include && proposalSearchRequest.getCodGroupList() != null) {
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
    public List<ProposalFullDTO> searchProposals(ProposalSearchRequest proposalSearchRequest, Proposal.Status status) {
        return searchProposals(proposalSearchRequest,List.of(status));
    }


    @Override
    public void archiveProposal(Long id) {
        if (!proposalRepository.existsById(id)) {
            throw (new ProposalNotFoundException("Proposal with this id does not exist"));
        }
        Proposal old = proposalRepository.getReferenceById(id);
        if (old.getStatus() == Proposal.Status.ARCHIVED) {
            throw (new ProposalNotFoundException("Proposal already archived"));
        } else {
            applicationRepository.getApplicationByProposal_Id(id).forEach(it -> {
                        Application application = applicationRepository.getReferenceById(it.getId());
                        application.setStatus(ApplicationStatus.CANCELLED);
                        applicationRepository.save(application);
                    }
            );
            old.setStatus(Proposal.Status.ARCHIVED);
        }
        ProposalFullDTO.fromProposal(proposalRepository.save(old));
    }

    @Override
    public void deleteProposal(Long id) {
        if (!proposalRepository.existsById(id))
            throw (new ProposalNotFoundException("Proposal with this id does not exist"));

        applicationRepository.getApplicationByProposal_Id(id).forEach(it -> {
                    Application application = applicationRepository.getReferenceById(it.getId());
                    application.setStatus(ApplicationStatus.CANCELLED);
                    applicationRepository.save(application);
                }
        );
        Proposal proposal = proposalRepository.getReferenceById(id);
        proposal.setStatus(Proposal.Status.DELETED);
        proposalRepository.save(proposal);
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    @Async
    @Transactional
    public void archiveExpiredProposals() {
        Date nowOld = Calendar.getInstance().getTime();
        Calendar now = Calendar.getInstance();
        now.setTime(nowOld);
        now.add(Calendar.DAY_OF_MONTH, VirtualClockController.getOffset());
        Calendar oneWeekFromNow = (Calendar) now.clone();

        oneWeekFromNow.add(Calendar.WEEK_OF_YEAR, 1);
        List<Proposal> proposals = proposalRepository.findAllByStatusNotIn(List.of(Proposal.Status.ARCHIVED, Proposal.Status.DELETED));

        for (Proposal proposal : proposals) {
            Date expiration = proposal.getExpiration();

            if (expiration != null) {
                boolean edited = false;
                if (now.getTime().after(expiration)) {
                    try {
                        archiveProposal(proposal.getId());
                    } catch (Error ignore) {

                    }
                }
                if (!proposal.getNotifiedAboutExpiration() && expiration.before(oneWeekFromNow.getTime())) {
                    try {
                        emailService.notifySupervisorOfExpiration(proposal);
                        proposal.setNotifiedAboutExpiration(true);
                        edited = true;
                    } catch (Exception ignored) {

                    }
                }
                if (edited)
                    proposalRepository.save(proposal);
            }
        }
    }

    public List<ProposalFullDTO> getAllProposalByCoSupervisor(String email) {
        if (!teacherRepository.existsTeacherByEmail(email)) {
            throw new TeacherNotFoundException("Logged teacher cannot be found on the DB");
        }
        return proposalRepository.findProposalsByCoSupervisorsContaining(teacherRepository.findByEmail(email)).stream().map(ProposalFullDTO::fromProposal).toList();
    }
}
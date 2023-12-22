package it.polito.se2.g04.thesismanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.se2.g04.thesismanagement.application.Application;
import it.polito.se2.g04.thesismanagement.application.ApplicationRepository;
import it.polito.se2.g04.thesismanagement.application.ApplicationService;
import it.polito.se2.g04.thesismanagement.degree.Degree;
import it.polito.se2.g04.thesismanagement.degree.DegreeRepository;
import it.polito.se2.g04.thesismanagement.group.Group;
import it.polito.se2.g04.thesismanagement.group.GroupRepository;
import it.polito.se2.g04.thesismanagement.proposal.*;
import it.polito.se2.g04.thesismanagement.student.Student;
import it.polito.se2.g04.thesismanagement.student.StudentRepository;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import it.polito.se2.g04.thesismanagement.teacher.TeacherRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UpdateProposalTest {

    @Autowired
    private ProposalRepository proposalRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private DegreeRepository degreeRepository;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private ProposalService proposalService;

    @Autowired
    private MockMvc mockMvc;

    @AfterAll
    public void CleanUp() {
        applicationRepository.deleteAll();
        proposalRepository.deleteAll();
        teacherRepository.deleteAll();
        studentRepository.deleteAll();
        groupRepository.deleteAll();
        degreeRepository.deleteAll();
    }

    @Test
    @Rollback
    @WithMockUser(username = "m.potenza@example.com", roles = {"TEACHER"})
    void TestCreate() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Group g = new Group("TestGroup");
        g = groupRepository.save(g);
        Teacher teacher = new Teacher("Massimo", "Potenza", "m.potenza@example.com", g, null);
        teacher = teacherRepository.save(teacher);
        Proposal proposal = new Proposal();
        proposal.setTitle("test1");
        proposal.setSupervisor(teacher);
        proposal.setCoSupervisors(List.of(teacher));
        proposal.setKeywords("parola");
        proposal.setType("type");
        proposal.setGroups(List.of(g));
        proposal.setDescription("descrizione");
        proposal.setRequiredKnowledge("poca");
        proposal.setNotes("notes");
        proposal.setExpiration(null); // Assuming that the date is nullable
        proposal.setLevel("level");
        proposal.setCds("cds");
        proposal = proposalRepository.save(proposal);

        proposal.setDescription("nuova descrizione");

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.put("/API/proposal/update/{id}", proposal.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(ProposalDTO.fromProposal(proposal))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Proposal proposalOutput = proposalRepository.getReferenceById(proposal.getId());
        assertEquals(proposalOutput, proposal, "they should be the same");


        Degree degree = new Degree("ingegneria informatica");
        degreeRepository.save(degree);
        Student student = new Student("rossi", "marco", "male", "ita", "m.rossi@example.com", degree, 2020);
        studentRepository.save(student);
        Application application = new Application(student, null, null, proposal);
        application = applicationRepository.save(application);
        applicationService.acceptApplicationById(application.getId());

        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposal/update/{id}", proposal.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(ProposalDTO.fromProposal(proposal))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposal/update")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());


        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposal/update/a")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());


        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposal/update/" + new Random().nextLong(3, 100))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(ProposalDTO.fromProposal(proposal))))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Proposal proposalEnumTestAccepted = new Proposal();
        proposalEnumTestAccepted.setTitle("test1");
        proposalEnumTestAccepted.setSupervisor(teacher);
        proposalEnumTestAccepted.setCoSupervisors(List.of(teacher));
        proposalEnumTestAccepted.setKeywords("parola");
        proposalEnumTestAccepted.setType("type");
        proposalEnumTestAccepted.setGroups(List.of(g));
        proposalEnumTestAccepted.setDescription("descrizione");
        proposalEnumTestAccepted.setRequiredKnowledge("poca");
        proposalEnumTestAccepted.setNotes("notes");
        proposalEnumTestAccepted.setExpiration(null); // Assuming that the date is nullable
        proposalEnumTestAccepted.setLevel("level");
        proposalEnumTestAccepted.setCds("cds");
        proposalEnumTestAccepted.setStatus(Proposal.Status.ACCEPTED);
        proposalRepository.save(proposalEnumTestAccepted);

        Proposal proposalEnumTestDelete = new Proposal();
        proposalEnumTestDelete.setTitle("test1");
        proposalEnumTestDelete.setSupervisor(teacher);
        proposalEnumTestDelete.setCoSupervisors(List.of(teacher));
        proposalEnumTestDelete.setKeywords("parola");
        proposalEnumTestDelete.setType("type");
        proposalEnumTestDelete.setGroups(List.of(g));
        proposalEnumTestDelete.setDescription("descrizione");
        proposalEnumTestDelete.setRequiredKnowledge("poca");
        proposalEnumTestDelete.setNotes("notes");
        proposalEnumTestDelete.setExpiration(null); // Assuming that the date is nullable
        proposalEnumTestDelete.setLevel("level");
        proposalEnumTestDelete.setCds("cds");
        proposalEnumTestDelete.setStatus(Proposal.Status.DELETED);
        proposalRepository.save(proposalEnumTestDelete);

        Proposal proposalEnumTestArchived = new Proposal();
        proposalEnumTestArchived.setTitle("test1");
        proposalEnumTestArchived.setSupervisor(teacher);
        proposalEnumTestArchived.setCoSupervisors(List.of(teacher));
        proposalEnumTestArchived.setKeywords("parola");
        proposalEnumTestArchived.setType("type");
        proposalEnumTestArchived.setGroups(List.of(g));
        proposalEnumTestArchived.setDescription("descrizione");
        proposalEnumTestArchived.setRequiredKnowledge("poca");
        proposalEnumTestArchived.setNotes("notes");
        proposalEnumTestArchived.setExpiration(null); // Assuming that the date is nullable
        proposalEnumTestArchived.setLevel("level");
        proposalEnumTestArchived.setCds("cds");
        proposalEnumTestArchived.setStatus(Proposal.Status.ARCHIVED);
        proposalRepository.save(proposalEnumTestArchived);

        List<ProposalFullDTO> proposal3 = proposalService.getAllNotArchivedProposals();
        assertEquals(0, proposal3.size(), "proposal3 should be empty");
    }
}
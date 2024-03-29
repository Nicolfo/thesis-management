package it.polito.se2.g04.thesismanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.polito.se2.g04.thesismanagement.degree.DegreeRepository;
import it.polito.se2.g04.thesismanagement.department.Department;
import it.polito.se2.g04.thesismanagement.department.DepartmentRepository;
import it.polito.se2.g04.thesismanagement.group.Group;
import it.polito.se2.g04.thesismanagement.group.GroupRepository;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.proposal.ProposalFullDTO;
import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
import it.polito.se2.g04.thesismanagement.student.StudentRepository;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import it.polito.se2.g04.thesismanagement.teacher.TeacherRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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

import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProposalByCoSupervisorTest {
    @Autowired
    private ProposalRepository proposalRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private DegreeRepository degreeRepository;
    @Autowired
    private MockMvc mockMvc;

    private Teacher t1, t2, t3,t4;
    private Group g1, g2;
    private Proposal p1, p2, p3;

    @BeforeAll
    public void setup() throws Exception {
        g1 = groupRepository.save(new Group("Group1"));
        g2 = groupRepository.save(new Group("Group2"));
        Department d = departmentRepository.save(new Department("Dept"));
        t1 = new Teacher("Potenza", "Massimo", "m.potenza@polito.it", g1, d);
        t1 = teacherRepository.save(t1);
        t2 = new Teacher("Bitta", "Paolo", "bitta.paolo@polito.it", g2, d);
        t2 = teacherRepository.save(t2);
        t3 = new Teacher("Nervi", "Luca", "nervi.luca@polito.it", g2, d);
        t3 = teacherRepository.save(t3);
        t4 = new Teacher("Errore", "Previsto", "previsto.errore@polito.it", g2, d);
        t4 = teacherRepository.save(t3);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        p1 = new Proposal();
        p1.setTitle("Title1");
        p1.setSupervisor(t1);
        p1.setCoSupervisors(List.of(t2));
        p1.setKeywords("Key1");
        p1.setType("Development");
        p1.setGroups(List.of(g1));
        p1.setDescription("Desc1");
        p1.setRequiredKnowledge("Know1");
        p1.setNotes("Notes1");
        p1.setExpiration(formatter.parse("15-06-2025"));
        p1.setLevel("Bachelor's");
        p1.setCds("Electrical Engineering, Computer Engineering");
        p1.setStatus(Proposal.Status.ACTIVE);
        p1 = proposalRepository.save(p1);
        p2 = new Proposal();
        p2.setTitle("Title2");
        p2.setSupervisor(t2);
        p2.setCoSupervisors(List.of(t3));
        p2.setKeywords("Key2");
        p2.setType("Development");
        p2.setGroups(List.of(g1, g2));
        p2.setDescription("Desc2");
        p2.setRequiredKnowledge("Know2");
        p2.setNotes("Notes2");
        p2.setExpiration(formatter.parse("15-06-2025"));
        p2.setLevel("Master's");
        p2.setCds("Electrical Engineering, Computer Engineering");
        p2.setStatus(Proposal.Status.ACTIVE);
        p2 = proposalRepository.save(p2);
        p3 = new Proposal();
        p3.setTitle("Title3");
        p3.setSupervisor(t1);
        p3.setCoSupervisors(List.of(t2, t3));
        p3.setKeywords("Key3");
        p3.setType("Research");
        p3.setGroups(List.of(g2));
        p3.setDescription("Desc3");
        p3.setRequiredKnowledge("Know3");
        p3.setNotes("Notes3");
        p3.setExpiration(formatter.parse("15-06-2025"));
        p3.setLevel("Bachelor's");
        p3.setCds("Computer Engineering");
        p3.setStatus(Proposal.Status.ACTIVE);
        p3 = proposalRepository.save(p3);
    }

    @AfterAll
    public void cleanUp() {
        proposalRepository.deleteAll();
        teacherRepository.deleteAll();
        groupRepository.deleteAll();
        departmentRepository.deleteAll();
        studentRepository.deleteAll();
        degreeRepository.deleteAll();
    }

    @Test
    @Rollback
    @WithMockUser(username = "bitta.paolo@polito.it", roles = {"TEACHER"})
    void getAllProposalByCoSupervisorTest() throws Exception {
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getAllProposalByCoSupervisor/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProposalFullDTO[] finalResult = mapper.readValue(json, ProposalFullDTO[].class);

        assertEquals(2, finalResult.length, "Search should return 2 proposal");
        ProposalFullDTO proposal = finalResult[0];
        assertEquals("Title1", proposal.getTitle(), "Proposal should be the second one");
    }

    @Test
    @Rollback
    @WithMockUser(username = "previsto.errore@polito.it", roles = {"TEACHER"})
    void getAllProposalByCoSupervisorErrorTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getAllProposalByCoSupervisor/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
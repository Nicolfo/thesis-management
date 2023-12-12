package it.polito.se2.g04.thesismanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.polito.se2.g04.thesismanagement.degree.Degree;
import it.polito.se2.g04.thesismanagement.degree.DegreeRepository;
import it.polito.se2.g04.thesismanagement.department.Department;
import it.polito.se2.g04.thesismanagement.department.DepartmentRepository;
import it.polito.se2.g04.thesismanagement.group.Group;
import it.polito.se2.g04.thesismanagement.group.GroupRepository;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.proposal.ProposalFullDTO;
import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
import it.polito.se2.g04.thesismanagement.proposal.ProposalSearchRequest;
import it.polito.se2.g04.thesismanagement.student.Student;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SearchProposalTest {
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

    private Teacher t1, t2, t3;
    private Group g1, g2;
    private Proposal p1, p2, p3;

    @BeforeAll
    public void setup() throws Exception {
        Degree degree = degreeRepository.save(new Degree("Computer Engineering"));
        studentRepository.save(new Student("test", "test", "female", "Italy", "test@example.com", degree, 2021));
        g1 = groupRepository.save(new Group("Group1"));
        g2 = groupRepository.save(new Group("Group2"));
        Department d = departmentRepository.save(new Department("Dept"));
        t1 = new Teacher("Rossi", "Mario", "rossi.mario@polito.it", g1, d);
        t1 = teacherRepository.save(t1);
        t2 = new Teacher("Bitta", "Paolo", "bitta.paolo@polito.it", g2, d);
        t2 = teacherRepository.save(t2);
        t3 = new Teacher("Nervi", "Luca", "nervi.luca@polito.it", g2, d);
        t3 = teacherRepository.save(t3);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        p1 = new Proposal("Title1", t1, List.of(t2), "Key1", "Development", List.of(g1), "Desc1", "Know1", "Notes1", formatter.parse("15-06-2025"), "Bachelor's", "Electrical Engineering, Computer Engineering");
        p1 = proposalRepository.save(p1);
        p2 = new Proposal("Title2", t2, List.of(t3), "Key2", "Development", List.of(g1, g2), "Desc2", "Know2", "Notes2", formatter.parse("15-06-2025"), "Master's", "Electrical Engineering, Computer Engineering");
        p2 = proposalRepository.save(p2);
        p3 = new Proposal("Title3", t1, List.of(t2, t3), "Key3", "Research", List.of(g2), "Desc3", "Know3", "Notes3", formatter.parse("15-06-2025"), "Bachelor's", "Computer Engineering");
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
    @WithMockUser(username = "test@example.com", roles = {"STUDENT"})
    public void searchWithAllFields() throws Exception {
        ProposalSearchRequest req = new ProposalSearchRequest();
        req.setTitle(p1.getTitle());
        req.setSupervisorIdList(List.of(t1.getId()));
        req.setCoSupervisorIdList(List.of(t2.getId()));
        req.setKeywords(p1.getKeywords());
        req.setType(p1.getType());
        req.setCodGroupList(List.of(g1.getCodGroup()));
        req.setDescription(p1.getDescription());
        req.setRequiredKnowledge(p1.getRequiredKnowledge());
        //req.setLevel(p1.getLevel());
        req.setNotes(p1.getNotes());

        ObjectMapper objectMapper = new ObjectMapper();
        String reqJson = objectMapper.writeValueAsString(req);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqJson))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProposalFullDTO[] finalResult = mapper.readValue(json, ProposalFullDTO[].class);

        assertEquals(1, finalResult.length, "Search should return 1 proposal");
        ProposalFullDTO proposal = finalResult[0];
        assertEquals("Title1", proposal.getTitle(), "Proposal should be the first one");
    }

    @Test
    @Rollback
    @WithMockUser(username = "test@example.com", roles = {"STUDENT"})
    public void searchOnlyByGroups() throws Exception {
        ProposalSearchRequest req = new ProposalSearchRequest();
        req.setCodGroupList(List.of(g2.getCodGroup()));

        ObjectMapper objectMapper = new ObjectMapper();
        String reqJson = objectMapper.writeValueAsString(req);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProposalFullDTO[] finalResult = mapper.readValue(json, ProposalFullDTO[].class);

        assertEquals(2, finalResult.length, "Search should return 2 proposals");
        assertTrue(Stream.of(finalResult).anyMatch(p -> p.getTitle().equals("Title2")));
        assertTrue(Stream.of(finalResult).anyMatch(p -> p.getTitle().equals("Title3")));
    }

    @Test
    @Rollback
    @WithMockUser(username = "test@example.com", roles = {"STUDENT"})
    public void searchOnlyByCoSupervisors() throws Exception {
        ProposalSearchRequest req = new ProposalSearchRequest();
        req.setCoSupervisorIdList(List.of(t2.getId()));

        ObjectMapper objectMapper = new ObjectMapper();
        String reqJson = objectMapper.writeValueAsString(req);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProposalFullDTO[] finalResult = mapper.readValue(json, ProposalFullDTO[].class);

        assertEquals(2, finalResult.length, "Search should return 2 proposals");
        assertTrue(Stream.of(finalResult).anyMatch(p -> p.getTitle().equals("Title1")));
        assertTrue(Stream.of(finalResult).anyMatch(p -> p.getTitle().equals("Title3")));
    }

    @Test
    @Rollback
    @WithMockUser(username = "test@example.com", roles = {"STUDENT"})
    public void searchWithNoFields() throws Exception {
        ProposalSearchRequest req = new ProposalSearchRequest();

        ObjectMapper objectMapper = new ObjectMapper();
        String reqJson = objectMapper.writeValueAsString(req);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProposalFullDTO[] finalResult = mapper.readValue(json, ProposalFullDTO[].class);

        assertEquals(3, finalResult.length, "Search should return 2 proposals");
    }
/*
    @Test
    @Rollback
    @WithMockUser(username = "test@example.com", roles = {"STUDENT"})
    public void searchWithInvalidLevel() throws Exception {
        ProposalSearchRequest req = new ProposalSearchRequest();
        req.setLevel("Invalid");

        ObjectMapper objectMapper = new ObjectMapper();
        String reqJson = objectMapper.writeValueAsString(req);

        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }*/
}

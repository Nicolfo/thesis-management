package it.polito.se2.g04.thesismanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.shaded.gson.JsonParser;
import it.polito.se2.g04.thesismanagement.department.Department;
import it.polito.se2.g04.thesismanagement.department.DepartmentRepository;
import it.polito.se2.g04.thesismanagement.group.Group;
import it.polito.se2.g04.thesismanagement.group.GroupRepository;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.proposal.ProposalDTO;
import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
import it.polito.se2.g04.thesismanagement.security.old.user.LoginDTO;
import it.polito.se2.g04.thesismanagement.security.old.user.RegisterDTO;
import it.polito.se2.g04.thesismanagement.security.old.user.User;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import it.polito.se2.g04.thesismanagement.teacher.TeacherRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
public class BrowseProposalTest {

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    private RegisterDTO teacherReg;
    private LoginDTO teacherLogin;
    private String token2;
    private Teacher teacher;



    @BeforeAll
    public void setup() throws Exception {
        teacher = new Teacher("Gerald", "Juarez","test@example.com",null,null);
        teacherRepository.save(teacher);




        ObjectMapper objectMapper=new ObjectMapper();

        Group saved=groupRepository.save(new Group("Group 1"));
        Department department=departmentRepository.save(new Department("Department 1"));

        teacherReg = new RegisterDTO("surname_1", "name", "test@example.com", "TEACHER", "prova2", saved.getCodGroup(), department.getCodDepartment(), null, null, null, 0);
        mockMvc.perform(MockMvcRequestBuilders.post("/API/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherReg)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        teacherLogin = new LoginDTO(teacherReg.getEmail(),teacherReg.getPassword());

        MvcResult res2=mockMvc.perform(MockMvcRequestBuilders.post("/API/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherLogin)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").exists())
                .andReturn();
        token2= JsonParser.parseString(res2.getResponse().getContentAsString()).getAsJsonObject().get("token").getAsString();

        /*//mock logged in user
        User user = new User("test@example.com", "password", "TEACHER");
        Authentication auth = new TestingAuthenticationToken(user, "password");
        SecurityContextHolder.getContext().setAuthentication(auth);*/
    }

    @AfterAll
    public void CleanUp(){
        teacherRepository.deleteAll();
        proposalRepository.deleteAll();
    }

    @Test
    @Rollback
    public void createProposal() throws Exception {
        ProposalDTO proposal = new ProposalDTO("Proposal 1", teacher.getId(), new ArrayList<>(),"Description 1","req knowledge","notes",new Date(2024, Calendar.DECEMBER,10),"level","CdS", "keywords","type");


        ObjectMapper objectMapper = new ObjectMapper();
        String jsonProposal = objectMapper.writeValueAsString(proposal);

        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/insert/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonProposal)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(MockMvcResultMatchers.status().isCreated());


        //Proposal proposal = new Proposal( "Proposal 1", teacher, null, "keywords", "type", null, "Description 1", "requiredKnowledge", "notes", null, "level", "CdS");
        /*mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/insert/",proposal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));*/





        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/insert/").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

@Test
    public void getAllProposals() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();

    ProposalDTO proposal1 = new ProposalDTO("Proposal 1", teacher.getId(), new ArrayList<>(),"Description 1","req knowledge","notes",new Date(2023, Calendar.DECEMBER,10),"level","CdS", "keywords","type");

    String jsonProposal = objectMapper.writeValueAsString(proposal1);
    mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/insert/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonProposal)
                    .header("Authorization", "Bearer " + token2))
            .andExpect(MockMvcResultMatchers.status().isCreated());

    ProposalDTO proposal2 = new ProposalDTO("Proposal 2", teacher.getId(), new ArrayList<>(),"Description 2","req knowledge","notes",new Date(2024, Calendar.MARCH,4),"level","CdS", "keywords","type");
    jsonProposal = objectMapper.writeValueAsString(proposal2);
    mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/insert/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonProposal)
                    .header("Authorization", "Bearer " + token2))
            .andExpect(MockMvcResultMatchers.status().isCreated());


        //get result
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getAll/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Proposal[] proposals = mapper.readValue(json, Proposal[].class);

        //check that number of proposals match
        assertEquals(2, proposals.length, "getAll should just return 2 values");

        //check that titles match
        List<String> proposalTitles = Arrays.stream(proposals)
                .map(Proposal::getTitle)
                .toList();
        List<String> expectedTitles = List.of(proposal1.getTitle(), proposal2.getTitle());
        assertTrue(proposalTitles.containsAll(expectedTitles), "getAll does not return the correct values");

        //add one archived and one not archived proposal
        Proposal proposal3 = new Proposal(3L, "Proposal 3", teacher, null, "keywords", "type", null, "Description 1", "requiredKnowledge", "notes", null, "level", "CdS", true);
        Proposal proposal4 = new Proposal(4L, "Proposal 4", teacher, null, "keywords", "type", null, "Description 2", "requiredKnowledge", "notes", null, "level", "CdS", false);
        proposalRepository.save(proposal3);
        proposalRepository.save(proposal4);

        //get result
        res = mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        proposals = mapper.readValue(json, Proposal[].class);

        //check that number of proposals match
        assertEquals(3, proposals.length, "getAll should just return 3 values");

        //check that titles match
        proposalTitles = Arrays.stream(proposals)
                .map(Proposal::getTitle)
                .toList();
        expectedTitles = List.of(proposal1.getTitle(), proposal2.getTitle(), proposal4.getTitle());
        assertTrue(proposalTitles.containsAll(expectedTitles), "getAll does not return the correct values");
    }


    @Test
    @Rollback
    public void testGetProposalsByProf() throws Exception {
        Proposal proposal1 = new Proposal(1L, "Proposal 1", teacher, null, "keywords", "type", null, "Description 1", "requiredKnowledge", "notes", null, "level", "CdS", false);
        Proposal proposal2 = new Proposal(2L, "Proposal 2", teacher, null, "keywords", "type", null, "Description 2", "requiredKnowledge", "notes", null, "level", "CdS", false);
        proposalRepository.save(proposal1);
        proposalRepository.save(proposal2);

        //get result
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getByProf")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Proposal[] proposals = mapper.readValue(json, Proposal[].class);

        //check that number of proposals match
        assertEquals(2, proposals.length, "getAll should just return 2 values");

        //check that titles match
        List<String> proposalTitles = Arrays.stream(proposals)
                .map(Proposal::getTitle)
                .toList();
        List<String> expectedTitles = List.of(proposal1.getTitle(), proposal2.getTitle());
        assertTrue(proposalTitles.containsAll(expectedTitles), "getAll does not return the correct values");

        Teacher otherTeacher = new Teacher("Kemp", "Denise","Kemp@example.com",null,null);
        teacherRepository.save(otherTeacher);
        Proposal proposal3 = new Proposal(3L, "Proposal 3", otherTeacher, null, "keywords", "type", null, "Description 3", "requiredKnowledge", "notes", null, "level", "CdS", false);
        Proposal proposal4 = new Proposal(4L, "Proposal 4", otherTeacher, List.of(teacher), "keywords", "type", null, "Description 4", "requiredKnowledge", "notes", null, "level", "CdS", false);
        proposalRepository.save(proposal3);
        proposalRepository.save(proposal4);

        //get result
        res = mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getByProf")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        proposals = mapper.readValue(json, Proposal[].class);

        //check that number of proposals match
        assertEquals(3, proposals.length, "getAll should just return 3 values");

        //check that titles match
        proposalTitles = Arrays.stream(proposals)
                .map(Proposal::getTitle)
                .toList();
        expectedTitles = List.of(proposal1.getTitle(), proposal2.getTitle(), proposal4.getTitle());
        assertTrue(proposalTitles.containsAll(expectedTitles), "getAll does not return the correct values");


        proposalRepository.deleteAll();
        proposal1 = new Proposal(1L, "Proposal 1", otherTeacher, null, "keywords", "type", null, "Description 1", "requiredKnowledge", "notes", null, "level", "CdS", false);
        proposal2 = new Proposal(2L, "Proposal 2", otherTeacher, List.of(otherTeacher), "keywords", "type", null, "Description 2", "requiredKnowledge", "notes", null, "level", "CdS", false);
        proposalRepository.save(proposal1);
        proposalRepository.save(proposal2);

        //get result
        res = mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getByProf")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        proposals = mapper.readValue(json, Proposal[].class);

        //check that number of proposals match
        assertEquals(0, proposals.length, "getAll should return 0 values");

        User user = new User("not-existing@example.com", "password", "TEACHER");
        Authentication auth = new TestingAuthenticationToken(user, "password");
        SecurityContextHolder.getContext().setAuthentication(auth);

        //get result
        res = mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getByProf")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        proposals = mapper.readValue(json, Proposal[].class);

        //check that number of proposals match
        assertEquals(0, proposals.length, "getAll should return 0 values");
    }
}


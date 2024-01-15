package it.polito.se2.g04.thesismanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.polito.se2.g04.thesismanagement.department.Department;
import it.polito.se2.g04.thesismanagement.department.DepartmentRepository;
import it.polito.se2.g04.thesismanagement.group.Group;
import it.polito.se2.g04.thesismanagement.group.GroupRepository;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.proposal.ProposalDTO;
import it.polito.se2.g04.thesismanagement.proposal.ProposalFullDTO;
import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class BrowseProposalTest {

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

    private Teacher teacher;




    @BeforeAll
    public void setup() throws Exception {
        teacher = new Teacher("Gerald", "Juarez","test@example.com",null,null);
        teacherRepository.save(teacher);

        Group saved=groupRepository.save(new Group("Group 1"));
        Department department=departmentRepository.save(new Department("Department 1"));
    }

    @AfterAll
    public void CleanUp(){
        teacherRepository.deleteAll();
        proposalRepository.deleteAll();
        departmentRepository.deleteAll();
        groupRepository.deleteAll();
    }

    @Test
    @Rollback
    @WithMockUser(username = "test@example.com", roles = {"TEACHER"})
    void createProposal() throws Exception {
        ProposalDTO proposal = new ProposalDTO("Proposal 1", teacher.getId(), new ArrayList<>(), "Description 1", "req knowledge", "notes", new Date(2024, Calendar.DECEMBER, 10), "level", "CdS", "keywords", "type");


        ObjectMapper objectMapper = new ObjectMapper();
        String jsonProposal = objectMapper.writeValueAsString(proposal);

        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/insert/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonProposal))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @Rollback
    @WithMockUser(username = "test@example.com", roles = {"TEACHER"})
    void getAllProposals() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();

    ProposalDTO proposal1 = new ProposalDTO("Proposal 1", teacher.getId(), new ArrayList<>(),"Description 1","req knowledge","notes",new Date(2023, Calendar.DECEMBER,10),"level","CdS", "keywords","type");

    String jsonProposal = objectMapper.writeValueAsString(proposal1);
    mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/insert/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonProposal))
            .andExpect(MockMvcResultMatchers.status().isCreated());

    ProposalDTO proposal2 = new ProposalDTO("Proposal 2", teacher.getId(), new ArrayList<>(),"Description 2","req knowledge","notes",new Date(2024, Calendar.MARCH,4),"level","CdS", "keywords","type");
    jsonProposal = objectMapper.writeValueAsString(proposal2);
    mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/insert/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonProposal))
            .andExpect(MockMvcResultMatchers.status().isCreated());


        //get result
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProposalFullDTO[] proposals = mapper.readValue(json, ProposalFullDTO[].class);

        //check that number of proposals match
        assertEquals(2, proposals.length, "getAll should just return 2 values");

        //check that titles match
        List<String> proposalTitles = Arrays.stream(proposals)
                .map(ProposalFullDTO::getTitle)
                .toList();
        List<String> expectedTitles = List.of(proposal1.getTitle(), proposal2.getTitle());
        assertTrue(proposalTitles.containsAll(expectedTitles), "getAll does not return the correct values");

        //add one archived and one not archived proposal
        Proposal proposal3 = new Proposal(3L, "Proposal 3", teacher, null, "keywords", "type", null, "Description 1", "requiredKnowledge", "notes", null, "level", "CdS", Proposal.Status.ARCHIVED,false);
        Proposal proposal4 = new Proposal(4L, "Proposal 4", teacher, null, "keywords", "type", null, "Description 2", "requiredKnowledge", "notes", null, "level", "CdS", Proposal.Status.ACTIVE,false);
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
        proposals = mapper.readValue(json, ProposalFullDTO[].class);

        //check that number of proposals match

        assertEquals(3, proposals.length, "getAll should just return 3 values");

        //check that titles match
        proposalTitles = Arrays.stream(proposals)
                .map(ProposalFullDTO::getTitle)
                .toList();
        expectedTitles = List.of(proposal1.getTitle(), proposal2.getTitle(), proposal4.getTitle());
        assertTrue(proposalTitles.containsAll(expectedTitles), "getAll does not return the correct values");
    }


    @Test
    @Rollback
    @WithMockUser(username = "test@example.com", roles = {"TEACHER"})
    void testGetProposalsByProf() throws Exception {
        Proposal proposal1 = new Proposal(1L, "Proposal 1", teacher, null, "keywords", "type", null, "Description 1", "requiredKnowledge", "notes", null, "level", "CdS", Proposal.Status.ACTIVE,false);
        Proposal proposal2 = new Proposal(2L, "Proposal 2", teacher, null, "keywords", "type", null, "Description 2", "requiredKnowledge", "notes", null, "level", "CdS", Proposal.Status.ACTIVE,false);
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
        ProposalFullDTO[] proposals = mapper.readValue(json, ProposalFullDTO[].class);

        //check that number of proposals match
        assertEquals(2, proposals.length, "getAll should just return 2 values");

        //check that titles match
        List<String> proposalTitles = Arrays.stream(proposals)
                .map(ProposalFullDTO::getTitle)
                .toList();
        List<String> expectedTitles = List.of(proposal1.getTitle(), proposal2.getTitle());
        assertTrue(proposalTitles.containsAll(expectedTitles), "getAll does not return the correct values");

        Teacher otherTeacher = new Teacher("Kemp", "Denise","Kemp@example.com",null,null);
        teacherRepository.save(otherTeacher);
        Proposal proposal3 = new Proposal(3L, "Proposal 3", otherTeacher, null, "keywords", "type", null, "Description 3", "requiredKnowledge", "notes", null, "level", "CdS", Proposal.Status.ACTIVE,false);
        Proposal proposal4 = new Proposal(4L, "Proposal 4", teacher, List.of(teacher), "keywords", "type", null, "Description 4", "requiredKnowledge", "notes", null, "level", "CdS", Proposal.Status.ACTIVE,false);
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
        proposals = mapper.readValue(json, ProposalFullDTO[].class);

        //check that number of proposals match
        assertEquals(3, proposals.length, "getAll should just return 3 values");

        //check that titles match
        proposalTitles = Arrays.stream(proposals)
                .map(ProposalFullDTO::getTitle)
                .toList();
        expectedTitles = List.of(proposal1.getTitle(), proposal2.getTitle(), proposal4.getTitle());
        assertTrue(proposalTitles.containsAll(expectedTitles), "getAll does not return the correct values");


        proposalRepository.deleteAll();
        proposal1 = new Proposal(1L, "Proposal 1", otherTeacher, null, "keywords", "type", null, "Description 1", "requiredKnowledge", "notes", null, "level", "CdS", Proposal.Status.ACTIVE,false);
        proposal2 = new Proposal(2L, "Proposal 2", otherTeacher, List.of(otherTeacher), "keywords", "type", null, "Description 2", "requiredKnowledge", "notes", null, "level", "CdS", Proposal.Status.ACTIVE,false);
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
        proposals = mapper.readValue(json, ProposalFullDTO[].class);

        //check that number of proposals match
        assertEquals(0, proposals.length, "getAll should return 0 values");

        //get result
        res = mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getByProf")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        proposals = mapper.readValue(json, ProposalFullDTO[].class);

        //check that number of proposals match
        assertEquals(0, proposals.length, "getAll should return 0 values");
    }

    @Test
    @Rollback
    @WithMockUser(username = "notExisting@example.com", roles = {"TEACHER"})
    void getProposalsByProfWithoutTeacher() throws Exception {
        Proposal proposal1 = new Proposal(1L, "Proposal 1", teacher, null, "keywords", "type", null, "Description 1", "requiredKnowledge", "notes", null, "level", "CdS", Proposal.Status.ACTIVE,false);
        Proposal proposal2 = new Proposal(2L, "Proposal 2", teacher, null, "keywords", "type", null, "Description 2", "requiredKnowledge", "notes", null, "level", "CdS", Proposal.Status.ACTIVE,false);
        proposalRepository.save(proposal1);
        proposalRepository.save(proposal2);

        // This should throw an exception
        mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getByProf")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Rollback
    @WithMockUser(username = "test@example.com", roles = {"TEACHER"})
    void testGetArchived() throws Exception {
        Proposal proposal1 = new Proposal(1L, "Proposal 1", teacher, null, "keywords", "type", null, "Description 1", "requiredKnowledge", "notes", null, "level", "CdS", Proposal.Status.ARCHIVED,false);
        Proposal proposal2 = new Proposal(2L, "Proposal 2", teacher, null, "keywords", "type", null, "Description 2", "requiredKnowledge", "notes", null, "level", "CdS", Proposal.Status.ACCEPTED,false);
        proposalRepository.save(proposal1);
        proposalRepository.save(proposal2);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getArchived")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProposalFullDTO[] proposals = mapper.readValue(json, ProposalFullDTO[].class);

        //check that number of proposals match
        assertEquals(2, proposals.length, "getAll should return 0 values");
        assertTrue(Stream.of(proposals).anyMatch(p -> p.getTitle().equals(proposal1.getTitle())));
        assertTrue(Stream.of(proposals).anyMatch(p -> p.getTitle().equals(proposal2.getTitle())));
    }

    @Test
    @Rollback
    @WithMockUser(username = "notExisting@example.com", roles = {"TEACHER"})
    void testGetArchivedWithoutTeacher() throws Exception {
        Proposal proposal1 = new Proposal(1L, "Proposal 1", teacher, null, "keywords", "type", null, "Description 1", "requiredKnowledge", "notes", null, "level", "CdS", Proposal.Status.ARCHIVED,false);
        Proposal proposal2 = new Proposal(2L, "Proposal 2", teacher, null, "keywords", "type", null, "Description 2", "requiredKnowledge", "notes", null, "level", "CdS", Proposal.Status.ACCEPTED,false);
        proposalRepository.save(proposal1);
        proposalRepository.save(proposal2);

        // This should throw an exception
        mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getArchived")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Rollback
    @WithMockUser(username = "notExisting@example.com", roles = {"TEACHER"})
    void testGetTitleByProposalId() throws Exception {
        Proposal proposal1 = new Proposal(1L, "Proposal 1", teacher, null, "keywords", "type", null, "Description 1", "requiredKnowledge", "notes", null, "level", "CdS", Proposal.Status.ARCHIVED,false);
        Proposal proposal2 = new Proposal(2L, "Proposal 2", teacher, null, "keywords", "type", null, "Description 2", "requiredKnowledge", "notes", null, "level", "CdS", Proposal.Status.ACCEPTED,false);
        proposalRepository.save(proposal1);
        proposalRepository.save(proposal2);

        // This should throw an exception
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getTitleByProposalId/" + proposal1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String title = res.getResponse().getContentAsString();

        //check that number of proposals match
        assertEquals(proposal1.getTitle(), title, "Title should be proposal 1");
    }

    @Test
    @Rollback
    @WithMockUser(username = "notExisting@example.com", roles = {"TEACHER"})
    void testGetTitleByProposalIdWithoutProposal() throws Exception {
        Proposal proposal1 = new Proposal(1L, "Proposal 1", teacher, null, "keywords", "type", null, "Description 1", "requiredKnowledge", "notes", null, "level", "CdS", Proposal.Status.ARCHIVED,false);
        Proposal proposal2 = new Proposal(2L, "Proposal 2", teacher, null, "keywords", "type", null, "Description 2", "requiredKnowledge", "notes", null, "level", "CdS", Proposal.Status.ACCEPTED,false);
        proposalRepository.save(proposal1);
        proposalRepository.save(proposal2);

        // This should throw an exception
        mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getTitleByProposalId/" + 4000)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    @Test
    @Rollback
    @WithMockUser(username = "notExisting@example.com", roles = {"TEACHER"})
    void getProposalByIdTest() throws Exception {
        Proposal proposal1 = new Proposal(1L, "Proposal 1", teacher, null, "keywords", "type", null, "Description 1", "requiredKnowledge", "notes", null, "level", "CdS", Proposal.Status.ARCHIVED,false);
        Proposal proposal2 = new Proposal(2L, "Proposal 2", teacher, null, "keywords", "type", null, "Description 2", "requiredKnowledge", "notes", null, "level", "CdS", Proposal.Status.ACCEPTED,false);
        proposalRepository.save(proposal1);
        proposalRepository.save(proposal2);

        // This should throw an exception
        MvcResult res=mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getProposalById/" + proposal1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProposalFullDTO proposal= mapper.readValue(json, ProposalFullDTO.class);

        assertEquals("Proposal 1", proposal.getTitle(), "the title should be Proposal 1");
        assertEquals("Description 1", proposal.getDescription(), "the description should be Description 1");

        // This should throw an exception
        res=mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getProposalById/" + proposal2.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        json = res.getResponse().getContentAsString();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        proposal = mapper.readValue(json, ProposalFullDTO.class);

        assertEquals("Proposal 2", proposal.getTitle(), "the title should be Proposal 2");
        assertEquals("Description 2", proposal.getDescription(), "the description should be Description 2");

        mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getProposalById/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getProposalById/a")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getProposalById/"+ 100L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }
}




package it.polito.se2.g04.thesismanagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.se2.g04.thesismanagement.department.DepartmentRepository;
import it.polito.se2.g04.thesismanagement.group.Group;
import it.polito.se2.g04.thesismanagement.group.GroupRepository;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.proposal.ProposalDTO;
import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
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
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CreateProposalTest {

    @Autowired
    private ProposalRepository proposalRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private MockMvc mockMvc;


    @BeforeAll
    public void setup() {
        //mock logged in user
        /*User user = new User("test@example.com", "password", "TEACHER");
        Authentication auth = new TestingAuthenticationToken(user, "password");
        SecurityContextHolder.getContext().setAuthentication(auth);*/
    }

    @AfterAll
    public void CleanUp(){
        proposalRepository.deleteAll();
        teacherRepository.deleteAll();
        groupRepository.deleteAll();
    }

    @Test
    @Rollback
    @WithMockUser(username = "m.potenza@example.com", roles = {"TEACHER"})
    void testInsert() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Group g = new Group("Test");
        g = groupRepository.save(g);
        Teacher teacher=new Teacher("Massimo", "Potenza", "m.potenza@example.com",g,null);
        teacherRepository.save(teacher);
        Proposal proposal = new Proposal();
        proposal.setTitle("test1");
        proposal.setSupervisor(teacher);
        proposal.setCoSupervisors(null);
        proposal.setKeywords("parola");
        proposal.setType("type");
        proposal.setGroups(null);
        proposal.setDescription("descrizione");
        proposal.setRequiredKnowledge("poca");
        proposal.setNotes("notes");
        proposal.setExpiration(null); // Assuming that the date is nullable
        proposal.setLevel("level");
        proposal.setCds("cds");
        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/insert/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(ProposalDTO.fromProposal(proposal))))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/insert/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }
    @Test
    @Rollback
    @WithMockUser(username = "mismatching@no.it", roles = {"TEACHER"})
    void invalidTeacherException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Group g = new Group("Test");
        g = groupRepository.save(g);
        Teacher teacher=new Teacher("Massimo", "Potenza", "m.potenza@example.com",g,null);
        teacherRepository.save(teacher);
        Proposal proposal = new Proposal();
        proposal.setTitle("test1");
        proposal.setSupervisor(teacher);
        proposal.setCoSupervisors(null);
        proposal.setKeywords("parola");
        proposal.setType("type");
        proposal.setGroups(null);
        proposal.setDescription("descrizione");
        proposal.setRequiredKnowledge("poca");
        proposal.setNotes("notes");
        proposal.setExpiration(null); // Assuming that the date is nullable
        proposal.setLevel("level");
        proposal.setCds("cds");
        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/insert/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(ProposalDTO.fromProposal(proposal))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    @Test
    @Rollback
    @WithMockUser(username = "m.potenza@example.com", roles = {"TEACHER"})
    void teacherNotFoundException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Group g = new Group("Test");
        g = groupRepository.save(g);
        Teacher teacher=new Teacher("Massimo", "Potenza", "m.potenza@example.com",g,null);
        Proposal proposal = new Proposal();
        proposal.setTitle("test1");
        proposal.setSupervisor(teacher);
        proposal.setCoSupervisors(null);
        proposal.setKeywords("parola");
        proposal.setType("type");
        proposal.setGroups(null);
        proposal.setDescription("descrizione");
        proposal.setRequiredKnowledge("poca");
        proposal.setNotes("notes");
        proposal.setExpiration(null); // Assuming that the date is nullable
        proposal.setLevel("level");
        proposal.setCds("cds");
        ProposalDTO failedProposalDTO= ProposalDTO.fromProposal(proposal);
        failedProposalDTO.setSupervisorId(222L);
        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/insert/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(failedProposalDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


}

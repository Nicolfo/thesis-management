package it.polito.se2.g04.thesismanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.se2.g04.thesismanagement.group.Group;
import it.polito.se2.g04.thesismanagement.group.GroupRepository;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.proposal.ProposalDTO;
import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
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
public class UpdateProposalTest {

    @Autowired
    private ProposalRepository proposalRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private MockMvc mockMvc;

    @AfterAll
    public void CleanUp(){
        proposalRepository.deleteAll();
        teacherRepository.deleteAll();
        groupRepository.deleteAll();

    }

    @Test
    @Rollback
    @WithMockUser(username = "m.potenza@example.com", roles = {"TEACHER"})
    public void TestCreate() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Group g = new Group("TestGroup");
        g = groupRepository.save(g);
        Teacher teacher=new Teacher("Massimo", "Potenza", "m.potenza@example.com",g,null);
        teacher = teacherRepository.save(teacher);
        Proposal proposal=new Proposal("test1",teacher, List.of(teacher), "parola", "type", List.of(g), "descrizione", "poca", "notes",null,"level", "cds");
        proposal = proposalRepository.save(proposal);

        proposal.setDescription("nuova descrizione");

        MvcResult res= mockMvc.perform(MockMvcRequestBuilders.put("/API/proposal/update/{id}",proposal.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(ProposalDTO.fromProposal(proposal))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Proposal proposalOutput=proposalRepository.getReferenceById(proposal.getId());
        assertEquals(proposalOutput,proposal,"they should be the same");

        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposal/update")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());


        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposal/update/a")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());


        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposal/update/" + new Random().nextLong(2,100))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(ProposalDTO.fromProposal(proposal))))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }
}
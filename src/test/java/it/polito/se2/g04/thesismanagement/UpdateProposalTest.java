package it.polito.se2.g04.thesismanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
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
import org.springframework.transaction.annotation.Transactional;

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
    }

    @Test
    @Rollback
    public void TestCreate() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Teacher teacher=new Teacher("Massimo", "Potenza", "m.potenza@example.com",null,null);
        Proposal proposal=new Proposal("test1",teacher, null, "parola", "type", null, "descrizione", "poca", "notes",null,"level", "cds");
        proposalRepository.save(proposal);

        proposal.setDescription("nuova descrizione");

        MvcResult res= mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/update/{id}",1f)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(proposal)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        String json = res.getResponse().getContentAsString();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Proposal[] proposalOutput = mapper.readValue(json, Proposal[].class);

        assertEquals(proposalOutput[0],proposal,"they should be the same");

        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/update")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }
}
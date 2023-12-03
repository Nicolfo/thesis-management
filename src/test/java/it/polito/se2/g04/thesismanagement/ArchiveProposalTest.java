package it.polito.se2.g04.thesismanagement;

import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.proposal.ProposalFullDTO;
import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
import it.polito.se2.g04.thesismanagement.proposal.ProposalService;
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
public class ArchiveProposalTest {
    @Autowired
    private ProposalRepository proposalRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private ProposalService proposalService;

    @Autowired
    private MockMvc mockMvc;

    @AfterAll
    public void CleanUp(){
        proposalRepository.deleteAll();
        teacherRepository.deleteAll();
    }
    @Test
    @Rollback
    @WithMockUser(username = "test@example.it", roles = {"TEACHER"})
    public void archiveProposal() throws Exception {
        Teacher teacher=new Teacher("Massimo", "Potenza", "m.potenza@example.com",null,null);
        teacherRepository.save(teacher);
        Proposal proposal=new Proposal("test1",teacher, null, "parola", "type", null, "descrizione", "poca", "notes",null,"level", "cds");
        proposalRepository.save(proposal);
        Proposal proposal2=new Proposal("test2",teacher, null, "parola", "type", null, "descrizione", "poca", "notes",null,"level", "cds");
        proposalRepository.save(proposal2);

        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/archive/{id}",proposal.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        List<ProposalFullDTO> proposalOutput=proposalService.getAllNotArchivedProposals();
        assertEquals(1, proposalOutput.size(), "proposalOutput should be 1 long");
        assertEquals(Proposal.Status.ARCHIVED, proposalRepository.getReferenceById(proposal.getId()).getStatus(), "proposalOutput should be tagged to archive");

        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/archive/{id}",proposal2.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        proposalOutput=proposalService.getAllNotArchivedProposals();
        assertEquals(0, proposalOutput.size(), "proposalOutput should be empty");
        assertEquals(Proposal.Status.ARCHIVED, proposalRepository.getReferenceById(proposal2.getId()).getStatus(), "proposalOutput should be tagged to archive");

        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/archive/a")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/archive/" + new Random().nextLong(3,100))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}

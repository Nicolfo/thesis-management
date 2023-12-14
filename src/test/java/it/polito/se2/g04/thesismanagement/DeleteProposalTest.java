package it.polito.se2.g04.thesismanagement;

import it.polito.se2.g04.thesismanagement.application.Application;
import it.polito.se2.g04.thesismanagement.application.ApplicationRepository;
import it.polito.se2.g04.thesismanagement.application.ApplicationService;
import it.polito.se2.g04.thesismanagement.application.ApplicationStatus;
import it.polito.se2.g04.thesismanagement.degree.Degree;
import it.polito.se2.g04.thesismanagement.degree.DegreeRepository;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.proposal.ProposalFullDTO;
import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
import it.polito.se2.g04.thesismanagement.proposal.ProposalService;
import it.polito.se2.g04.thesismanagement.student.Student;
import it.polito.se2.g04.thesismanagement.student.StudentRepository;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
public class DeleteProposalTest {

    @Autowired
    private ProposalRepository proposalRepository;
    @Autowired
    private ProposalService proposalService;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private DegreeRepository degreeRepository;

    @Autowired
    private MockMvc mockMvc;


    @BeforeAll
    public void setup() {
    }

    @AfterAll
    public void CleanUp() {
        applicationRepository.deleteAll();
        proposalRepository.deleteAll();
        teacherRepository.deleteAll();
        studentRepository.deleteAll();
        degreeRepository.deleteAll();

    }

    @Test
    @Rollback
    @WithMockUser(username = "m.potenza@example.com", roles = {"TEACHER"})
    public void deleteProposal() throws Exception {
        Teacher teacher = new Teacher("Massimo", "Potenza", "m.potenza@example.com", null, null);
        teacherRepository.save(teacher);
        Degree degree = new Degree("ingegneria informatica");
        degreeRepository.save(degree);
        Student student1 = new Student("rossi", "marco", "male", "ita", "m.rossi@example.com", degree, 2020);
        Student student2 = new Student("viola", "marta", "female", "ita", "m.viola@example.com", degree, 2018);
        studentRepository.saveAll(List.of(student2, student1));
        Proposal proposal = new Proposal("test1", teacher, null, "parola", "type", null, "descrizione", "poca", "notes", null, "level", "cds");
        proposal = proposalRepository.save(proposal);
        Application application1 = new Application(student1, null, null, proposal);
        applicationRepository.save(application1);
        Application application2 = new Application(student2, null, null, proposal);
        applicationRepository.save(application2);

        mockMvc.perform(MockMvcRequestBuilders.delete("/API/proposal/delete/{id}", proposal.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        List<ProposalFullDTO> proposalOutput = proposalService.getAllProposals();
        assertEquals(Proposal.Status.DELETED, proposalRepository.getReferenceById(proposalOutput.get(0).getId()).getStatus(), "proposalOutput should be tagged to deletion");

        List<Application> applicationOutput = applicationRepository.findAll();
        assertEquals(ApplicationStatus.CANCELLED, applicationOutput.get(0).getStatus(), "applicationOutput should be tagged to deletion");
        assertEquals(ApplicationStatus.CANCELLED, applicationOutput.get(1).getStatus(), "applicationOutput should be tagged to deletion");

        proposalOutput = proposalService.getAllNotArchivedProposals();
        assertEquals(0, proposalOutput.size(), "proposalOutput should be 0 long");

        proposalOutput = proposalService.getProposalsByProf(teacher.getEmail());
        assertEquals(0, proposalOutput.size(), "proposalOutput should be 0 long");

        mockMvc.perform(MockMvcRequestBuilders.delete("/API/proposal/delete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.delete("/API/proposal/delete/a")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.delete("/API/proposal/delete/" + new Random().nextLong(2, 100))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}

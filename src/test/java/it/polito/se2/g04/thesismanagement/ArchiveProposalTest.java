package it.polito.se2.g04.thesismanagement;

import it.polito.se2.g04.thesismanagement.application.Application;
import it.polito.se2.g04.thesismanagement.application.ApplicationRepository;
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
class ArchiveProposalTest {
    @Autowired
    private ProposalRepository proposalRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private ProposalService proposalService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private DegreeRepository degreeRepository;
    @Autowired
    private ApplicationRepository applicationRepository;

    @AfterAll
    public void CleanUp(){
        proposalRepository.deleteAll();
        teacherRepository.deleteAll();
    }
    @Test
    @Rollback
    @WithMockUser(username = "m.potenza@example.com", roles = {"TEACHER"})
    void archiveProposal() throws Exception {
        Teacher teacher=new Teacher("Massimo", "Potenza", "m.potenza@example.com",null,null);
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
        proposalRepository.save(proposal);
        Proposal proposal2 = new Proposal();
        proposal2.setTitle("test2");
        proposal2.setSupervisor(teacher);
        proposal2.setCoSupervisors(null);
        proposal2.setKeywords("parola");
        proposal2.setType("type");
        proposal2.setGroups(null);
        proposal2.setDescription("descrizione");
        proposal2.setRequiredKnowledge("poca");
        proposal2.setNotes("notes");
        proposal2.setExpiration(null); // Assuming that the date is nullable
        proposal2.setLevel("level");
        proposal2.setCds("cds");
        proposalRepository.save(proposal2);
        Degree degree = new Degree("ingegneria informatica");
        degreeRepository.save(degree);
        Student student1 = new Student("rossi", "marco", "male", "ita", "m.rossi@example.com", degree, 2020);
        Student student2 = new Student("viola", "marta", "female", "ita", "m.viola@example.com", degree, 2018);
        studentRepository.saveAll(List.of(student2, student1));
        Application application1 = new Application(student1, null, null, proposal);
        applicationRepository.save(application1);
        Application application2 = new Application(student2, null, null, proposal);
        applicationRepository.save(application2);

        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/archive/{id}",proposal.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        List<ProposalFullDTO> proposalOutput=proposalService.getAllNotArchivedProposals();
        assertEquals(1, proposalOutput.size(), "proposalOutput should be 1 long");
        assertEquals(Proposal.Status.ARCHIVED, proposalRepository.getReferenceById(proposal.getId()).getStatus(), "proposalOutput should be tagged to archive");

        List<Application> applicationOutput = applicationRepository.findAll();
        assertEquals(ApplicationStatus.CANCELLED, applicationOutput.get(0).getStatus(), "applicationOutput should be tagged to deletion");
        assertEquals(ApplicationStatus.CANCELLED, applicationOutput.get(1).getStatus(), "applicationOutput should be tagged to deletion");

        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/archive/{id}",proposal2.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        proposalOutput=proposalService.getAllNotArchivedProposals();
        assertEquals(0, proposalOutput.size(), "proposalOutput should be empty");
        assertEquals(Proposal.Status.ARCHIVED, proposalRepository.getReferenceById(proposal2.getId()).getStatus(), "proposalOutput should be tagged to archive");

        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/archive/a")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/archive")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/archive/" + new Random().nextLong(3,100))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}

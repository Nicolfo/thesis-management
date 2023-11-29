package it.polito.se2.g04.thesismanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.polito.se2.g04.thesismanagement.application.Application;
import it.polito.se2.g04.thesismanagement.application.ApplicationRepository;
import it.polito.se2.g04.thesismanagement.degree.Degree;
import it.polito.se2.g04.thesismanagement.department.DepartmentRepository;
import it.polito.se2.g04.thesismanagement.group.GroupRepository;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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
    private TeacherRepository teacherRepository;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MockMvc mockMvc;



    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private DepartmentRepository departmentRepository;


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
    }
    @Test
    @Rollback
    public void deleteProposal() throws Exception {
        Teacher teacher=new Teacher("Massimo", "Potenza", "m.potenza@example.com",null,null);
        teacherRepository.save(teacher);
        Degree degree=new Degree("ingegneria informatica");
        Student student1=new Student("rossi", "marco", "male", "ita","m.rossi@example.com", degree, 2020);
        Student student2=new Student("viola", "marta", "female", "ita", "m.viola@example.com", degree,2018);
        Proposal proposal=new Proposal("test1",teacher, null, "parola", "type", null, "descrizione", "poca", "notes",null,"level", "cds");
        proposalRepository.save(proposal);
        Application application1=new Application(student1,null, null, proposal);
        applicationRepository.save(application1);
        Application application2=new Application(student2,null, null, proposal);
        applicationRepository.save(application2);

        mockMvc.perform(MockMvcRequestBuilders.delete("/API/proposal/delete/{id}",1f)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        MvcResult res= mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Proposal[] proposalOutput = mapper.readValue(json, Proposal[].class);
        assertEquals(0, proposalOutput.length, "proposalOutput should be empty");



        res= mockMvc.perform(MockMvcRequestBuilders.delete("/API/application/getByProf")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Application[] applicationOutput = mapper.readValue(json, Application[].class);
        assertEquals(0, proposalOutput.length, "applicationOutput should be empty");

        mockMvc.perform(MockMvcRequestBuilders.delete("/API/proposal/delete/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());




        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/delete/a")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());


        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/delete/" + new Random().nextLong(2,100))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}

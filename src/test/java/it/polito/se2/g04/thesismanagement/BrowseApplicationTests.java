package it.polito.se2.g04.thesismanagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.JsonParser;
import it.polito.se2.g04.thesismanagement.application.Application;
import it.polito.se2.g04.thesismanagement.application.ApplicationRepository;
import it.polito.se2.g04.thesismanagement.degree.DegreeRepository;
import it.polito.se2.g04.thesismanagement.department.Department;
import it.polito.se2.g04.thesismanagement.department.DepartmentRepository;
import it.polito.se2.g04.thesismanagement.group.Group;
import it.polito.se2.g04.thesismanagement.group.GroupRepository;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
import it.polito.se2.g04.thesismanagement.security.user.LoginDTO;
import it.polito.se2.g04.thesismanagement.security.user.RegisterDTO;
import it.polito.se2.g04.thesismanagement.student.Student;
import it.polito.se2.g04.thesismanagement.student.StudentRepository;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import it.polito.se2.g04.thesismanagement.teacher.TeacherRepository;
import org.json.JSONObject;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BrowseApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ProposalRepository proposalRepository;
    @Autowired
    private ApplicationRepository applicationRepository;

    private RegisterDTO teacherReg;
    private Teacher teacher;
    private Student student;

    private Proposal proposal1;
    private Proposal proposal2;


    // You can still mock your service if needed

    private Application application1;
    private Application application2;



    @BeforeAll
    public void setup() throws Exception {

        ObjectMapper objectMapper=new ObjectMapper();

        Group saved=groupRepository.save(new Group("Group 1"));
        Department department=departmentRepository.save(new Department("Department 1"));

        teacherReg = new RegisterDTO("surname_1","name","email2@email.com","TEACHER","prova2", saved.getCodGroup(), department.getCodDepartment(),null,null,null,0);
        mockMvc.perform(MockMvcRequestBuilders.post("/API/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherReg)))
                .andExpect(MockMvcResultMatchers.status().isOk());



        teacher=teacherRepository.findByEmail(teacherReg.getEmail());
        student = new Student("Georgina","Ferrell","female","italian","georgina.ferrell@example.com",null,2020);
        studentRepository.save(student);

        proposal1 = new Proposal("Patch-based discriminative learning for Iris Presentation Attack Detection",teacher,null,"Iris, PAD, Recognition, Detection, Spoofing","Bachelor Thesis",null,"Iris recognition is considered a prominent biometric authentication method. The accuracy, usability and touchless acquisition of iris recognition have led to their wide deployments.", "Good programming skills, atleast 2.0 in AuD, Basic Knowledge about AI",null,new Date(2024, Calendar.DECEMBER,10),null,null);
        proposal2 = new Proposal("Proposal 2", teacher, null, "keywords", "type", null, "Description 2", "requiredKnowledge", "notes", null, "level", "CdS");

        proposal1 = proposalRepository.save(proposal1);
        proposal2 = proposalRepository.save(proposal2);

        application1 = new Application(student,null,new Date(2023,Calendar.NOVEMBER,13),proposal2);
        application2 = new Application(student,null,new Date(2023,Calendar.NOVEMBER,7),proposal1);
        applicationRepository.save(application1);
        applicationRepository.save(application2);
    }
    @AfterAll
    void cleanup(){
        applicationRepository.deleteAll();
        proposalRepository.deleteAll();
        teacherRepository.deleteAll();
        studentRepository.deleteAll();
        departmentRepository.deleteAll();
        groupRepository.deleteAll();

    }


    @Test
    @Rollback
    public void getAllApplicationProf() throws Exception {
        ObjectMapper objectMapper=new ObjectMapper();


        LoginDTO teacherLogin= new LoginDTO(teacherReg.getEmail(),teacherReg.getPassword());

        MvcResult res2=mockMvc.perform(MockMvcRequestBuilders.post("/API/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherLogin)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").exists())
                .andReturn();
        String token2=JsonParser.parseString(res2.getResponse().getContentAsString()).getAsJsonObject().get("token").getAsString();
        mockMvc.perform(MockMvcRequestBuilders.get("/API/application/getByProf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));


    }


}
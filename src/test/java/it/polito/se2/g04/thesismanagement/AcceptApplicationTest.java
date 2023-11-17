package it.polito.se2.g04.thesismanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.JsonParser;
import it.polito.se2.g04.thesismanagement.application.Application;
import it.polito.se2.g04.thesismanagement.application.ApplicationRepository;
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
import org.junit.jupiter.api.*;
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
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
public class AcceptApplicationTest {
    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    ApplicationRepository applicationRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    private Teacher teacher;
    private Student student;
    private Proposal proposal;
    private Proposal proposal1;
    private Proposal proposal2;
    private Application application1;
    private Application application2;
    private RegisterDTO teacherReg;
    private LoginDTO teacherLogin;
    private String token2;



    @BeforeAll
    public void setup() throws Exception {
        ObjectMapper objectMapper=new ObjectMapper();


        Group saved=groupRepository.save(new Group("Group 1"));
        Department department=departmentRepository.save(new Department("Department 1"));

        teacherReg = new RegisterDTO("surname_1", "name", "email2@example.com", "TEACHER", "prova2", saved.getCodGroup(), department.getCodDepartment(), null, null, null, 0);
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

        teacher = new Teacher("Gerald", "Juarez","test@example.com",null,null);
        teacherRepository.save(teacher);

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
    public void CleanUp(){
        applicationRepository.deleteAll();
        proposalRepository.deleteAll();
        teacherRepository.deleteAll();
        studentRepository.deleteAll();
    }

    @Test
    @Rollback
    public void testAcceptApplication() throws Exception {
        //create error result
        mockMvc.perform(MockMvcRequestBuilders.get("/API/application/rejectApplicationById/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.get("/API/application/acceptApplicationById/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.get("/API/application/getApplicationById/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());


        //start valid tests
        MvcResult res =  mockMvc.perform(MockMvcRequestBuilders.get("/API/application/getApplicationById/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String json = res.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(json);
        int applicationid = jsonObject.getInt("id");
        jsonObject = jsonObject.getJSONObject("proposal");
        int proposalId = jsonObject.getInt("id");

        assertEquals(application1.getId(), applicationid);
        assertEquals(application1.getProposal().getId(), proposalId);

        //get result
        res = mockMvc.perform(MockMvcRequestBuilders.get("/API/application/getApplicationById/"+2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();
        jsonObject = new JSONObject(json);
        applicationid = jsonObject.getInt("id");
        jsonObject = jsonObject.getJSONObject("proposal");
        proposalId = jsonObject.getInt("id");

        assertEquals(application2.getId(), applicationid);
        assertEquals(application2.getProposal().getId(), proposalId);
    }

    @Test
    @Rollback
    public void acceptApplication() throws Exception {
        application1.setStatus("PENDING");
        application2.setStatus("REJECTED");
        application1 = applicationRepository.save(application1);
        application2 = applicationRepository.save(application2);

        //get result
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/API/application/acceptApplicationById/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();

        assertEquals(json, "true");
        assertEquals(0, applicationRepository.getApplicationById(1L).getStatus().compareTo("ACCEPTED"));

        //get result
        res = mockMvc.perform(MockMvcRequestBuilders.get("/API/application/acceptApplicationById/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();

        assertEquals(json, "false");
        assertEquals(0, applicationRepository.getApplicationById(2L).getStatus().compareTo("REJECTED"));

        //get result
        res = mockMvc.perform(MockMvcRequestBuilders.get("/API/application/acceptApplicationById/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();

        assertEquals(json, "false");

        //get result
        res = mockMvc.perform(MockMvcRequestBuilders.get("/API/application/rejectApplicationById/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();
        assertEquals(json, "false");

        application1.setStatus("PENDING");
        application1 = applicationRepository.save(application1);

        res = mockMvc.perform(MockMvcRequestBuilders.get("/API/application/rejectApplicationById/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();

        assertEquals(json, "true");
        assertEquals(0, applicationRepository.getApplicationById(1L).getStatus().compareTo("REJECTED"));

        res = mockMvc.perform(MockMvcRequestBuilders.get("/API/application/rejectApplicationById/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();

        assertEquals(json, "false");
        assertEquals(0, applicationRepository.getApplicationById(1L).getStatus().compareTo("REJECTED"));
    }

}

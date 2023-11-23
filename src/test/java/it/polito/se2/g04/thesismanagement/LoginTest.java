package it.polito.se2.g04.thesismanagement;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.JsonParser;
import it.polito.se2.g04.thesismanagement.degree.Degree;
import it.polito.se2.g04.thesismanagement.degree.DegreeRepository;
import it.polito.se2.g04.thesismanagement.department.Department;
import it.polito.se2.g04.thesismanagement.department.DepartmentRepository;
import it.polito.se2.g04.thesismanagement.group.Group;
import it.polito.se2.g04.thesismanagement.group.GroupRepository;
import it.polito.se2.g04.thesismanagement.security.old.user.LoginDTO;
import it.polito.se2.g04.thesismanagement.security.old.user.RegisterDTO;
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


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LoginTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DegreeRepository degreeRepository;

    private RegisterDTO studentReg;
    private RegisterDTO teacherReg;
    // You can still mock your service if needed


    @Test
    @BeforeAll
    public void testRegister() throws Exception {
       Degree degree=degreeRepository.save(new Degree("Degree 1"));
        studentReg = new RegisterDTO("surname_1","name","email@email.com","STUDENT","prova1",null, null,"MALE","IT",degree.getCodDegree(),2022);

        ObjectMapper objectMapper=new ObjectMapper();

        //Test API Call
        mockMvc.perform(MockMvcRequestBuilders.post("/API/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentReg)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Group saved=groupRepository.save(new Group("Group 1"));
        Department department=departmentRepository.save(new Department("Department 1"));

        teacherReg = new RegisterDTO("surname_1","name","email2@email.com","TEACHER","prova2", saved.getCodGroup(), department.getCodDepartment(),null,null,null,0);
        mockMvc.perform(MockMvcRequestBuilders.post("/API/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherReg)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    @Rollback
    public void testLogin() throws Exception {
        ObjectMapper objectMapper=new ObjectMapper();


        //STUDENT
        LoginDTO studentLogin= new LoginDTO(studentReg.getEmail(),studentReg.getPassword());

        MvcResult res=mockMvc.perform(MockMvcRequestBuilders.post("/API/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentLogin)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").exists())
                .andReturn();
        String token=JsonParser.parseString(res.getResponse().getContentAsString()).getAsJsonObject().get("token").getAsString();
        mockMvc.perform(MockMvcRequestBuilders.get("/API/testAuth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("You are logged in as student : email@email.com and role [STUDENT]"));


        //TEACHER
        LoginDTO teacherLogin= new LoginDTO(teacherReg.getEmail(),teacherReg.getPassword());

        MvcResult res2=mockMvc.perform(MockMvcRequestBuilders.post("/API/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherLogin)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").exists())
                .andReturn();
        String token2=JsonParser.parseString(res2.getResponse().getContentAsString()).getAsJsonObject().get("token").getAsString();
        mockMvc.perform(MockMvcRequestBuilders.get("/API/testAuth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("You are logged in as student : email2@email.com and role [TEACHER]"));


    }


}


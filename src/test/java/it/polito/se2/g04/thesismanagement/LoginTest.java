package it.polito.se2.g04.thesismanagement;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.JsonParser;
import it.polito.se2.g04.thesismanagement.degree.Degree;
import it.polito.se2.g04.thesismanagement.degree.DegreeRepository;
import it.polito.se2.g04.thesismanagement.department.Department;
import it.polito.se2.g04.thesismanagement.department.DepartmentRepository;
import it.polito.se2.g04.thesismanagement.group.Group;
import it.polito.se2.g04.thesismanagement.group.GroupRepository;
import it.polito.se2.g04.thesismanagement.security.user.LoginDTO;
import it.polito.se2.g04.thesismanagement.security.user.RegisterDTO;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.UUID;


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

    @BeforeAll
    public void setup() {
        Group saved=groupRepository.save(new Group("Group 1"));
        Department department=departmentRepository.save(new Department("Department 1"));
        Degree degree=degreeRepository.save(new Degree("Degree 1"));
        studentReg = new RegisterDTO("surname_1","name","email@email.com","STUDENT","prova1",null, null,"MALE","IT",degree.getCodDegree(),2022);
        teacherReg = new RegisterDTO("surname_1","name","email@email.com","STUDENT","prova1", saved.getCodGroup(), department.getCodDepartment(),null,null,null,0);
    }

    @Test
    @Rollback
    @Order(1)
    public void testRegister() throws Exception {

         ObjectMapper objectMapper=new ObjectMapper();

        //Test API Call
        mockMvc.perform(MockMvcRequestBuilders.post("/API/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentReg)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.post("/API/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherReg)))
                .andExpect(MockMvcResultMatchers.status().isOk());

       /* mockMvc.perform(MockMvcRequestBuilders.post("/API/tickets/createTicket/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/API/tickets/createTicket/a")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());*/
    }
    @Test
    @Rollback
    @Order(2)
    public void testLogin() throws Exception {
        ObjectMapper objectMapper=new ObjectMapper();
        LoginDTO studentLogin= new LoginDTO(studentReg.getEmail(),studentReg.getPassword());
        LoginDTO teacherLogin= new LoginDTO(teacherReg.getEmail(),teacherReg.getPassword());


        //Test API Call
        MvcResult res=mockMvc.perform(MockMvcRequestBuilders.post("/API/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentLogin)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").exists())
                .andReturn();
        String token=JsonParser.parseString(res.getResponse().getContentAsString()).getAsJsonObject().get("token").getAsString();
        mockMvc.perform(MockMvcRequestBuilders.get("/API/testAuth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("jwt",token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));

    }


}

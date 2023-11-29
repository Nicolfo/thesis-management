package it.polito.se2.g04.thesismanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.polito.se2.g04.thesismanagement.application.ApplicationRepository;
import it.polito.se2.g04.thesismanagement.department.DepartmentRepository;
import it.polito.se2.g04.thesismanagement.group.GroupRepository;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
import it.polito.se2.g04.thesismanagement.student.StudentRepository;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import it.polito.se2.g04.thesismanagement.teacher.TeacherRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ArchiveProposalTest {
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
    public void archiveProposal() throws Exception {
        Teacher teacher=new Teacher("Massimo", "Potenza", "m.potenza@example.com",null,null);
        teacherRepository.save(teacher);
        Proposal proposal=new Proposal("test1",teacher, null, "parola", "type", null, "descrizione", "poca", "notes",null,"level", "cds");
        proposalRepository.save(proposal);
        Proposal proposal2=new Proposal("test2",teacher, null, "parola", "type", null, "descrizione", "poca", "notes",null,"level", "cds");
        proposalRepository.save(proposal2);

        mockMvc.perform(MockMvcRequestBuilders.delete("/API/proposal/archive/{id}",1f)
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
        assertEquals(1, proposalOutput.length, "proposalOutput should be 1 long");


        mockMvc.perform(MockMvcRequestBuilders.delete("/API/proposal/archive/{id}",2f)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        res= mockMvc.perform(MockMvcRequestBuilders.get("/API/proposal/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        proposalOutput = mapper.readValue(json, Proposal[].class);
        assertEquals(0, proposalOutput.length, "proposalOutput should be empty");



        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/archive/a")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposal/archive/" + new Random().nextLong(3,100))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}

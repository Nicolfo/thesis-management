package it.polito.se2.g04.thesismanagement;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.polito.se2.g04.thesismanagement.degree.Degree;
import it.polito.se2.g04.thesismanagement.degree.DegreeRepository;
import it.polito.se2.g04.thesismanagement.proposalOnRequest.ProposalOnRequest;
import it.polito.se2.g04.thesismanagement.proposalOnRequest.ProposalOnRequestDTO;
import it.polito.se2.g04.thesismanagement.proposalOnRequest.ProposalOnRequestRepository;
import it.polito.se2.g04.thesismanagement.proposalOnRequest.ProposalOnRequestService;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
public class ProposalOnRequestTest {
    @Autowired
    private DegreeRepository degreeRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private ProposalOnRequestRepository proposalOnRequestRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ProposalOnRequestService proposalOnRequestService;

    @Autowired
    private MockMvc mockMvc;

    private List<ProposalOnRequest> proposal = new ArrayList<ProposalOnRequest>();

    @BeforeAll
    public void setup() {
        Degree degree = new Degree("ingegneria informatica");
        degreeRepository.save(degree);
        Student student1 = new Student("rossi", "marco", "male", "ita", "m.rossi@example.com", degree, 2020);
        student1 = studentRepository.save(student1);
        Student student2 = new Student("viola", "marta", "female", "ita", "m.viola@example.com", degree, 2018);
        student2 = studentRepository.save(student2);
        Student student3 = new Student("verde", "antonio", "male", "ita", "a.verde@example.com", degree, 2019);
        student3 = studentRepository.save(student3);
        Teacher teacher = new Teacher("Massimo", "Potenza", "m.potenza@example.com", null, null);
        teacher = teacherRepository.save(teacher);

        proposal.add(proposalOnRequestRepository.save(new ProposalOnRequest(
                "titolo", "descrizione", teacher, student1, null, null
        )));
        proposal.add(proposalOnRequestRepository.save(new ProposalOnRequest(
                "titolo2", "descrizione2", teacher, student2, null, null
        )));
        proposal.add(proposalOnRequestRepository.save(new ProposalOnRequest(
                "titolo3", "descrizione3", teacher, student3, null, null
        )));

    }

    @AfterAll
    public void CleanUp() {
        proposalOnRequestRepository.deleteAll();
        studentRepository.deleteAll();
        degreeRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    @Test
    @Rollback
    @WithMockUser(username = "m.potenza@example.com", roles = {"SECRETARY"})
    public void getAllPendingTest() throws Exception {
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/API/proposalOnRequest/getAllPending")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProposalOnRequestDTO[] result = mapper.readValue(json, ProposalOnRequestDTO[].class);
        assertEquals(3, result.length, "this proposal should be 3 long");
        assertEquals(result[0].getId(),proposal.get(0).getId(),"id should be the same");
        assertEquals(result[1].getId(),proposal.get(1).getId(),"id should be the same");
        assertEquals(result[2].getId(),proposal.get(2).getId(),"id should be the same");

        proposalOnRequestService.proposalOnRequestSecretaryRejected(proposal.get(0).getId());
        res = mockMvc.perform(MockMvcRequestBuilders.get("/API/proposalOnRequest/getAllPending")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        result = mapper.readValue(json, ProposalOnRequestDTO[].class);
        assertEquals(2, result.length, "this proposal should be 2 long");
        assertEquals(result[0].getId(),proposal.get(1).getId(),"id should be the same");
        assertEquals(result[1].getId(),proposal.get(2).getId(),"id should be the same");

        proposalOnRequestService.proposalOnRequestSecretaryAccepted(proposal.get(1).getId());
        proposalOnRequestService.proposalOnRequestTeacherRejected((proposal.get(2).getId()));
        res = mockMvc.perform(MockMvcRequestBuilders.get("/API/proposalOnRequest/getAllPending")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        result = mapper.readValue(json, ProposalOnRequestDTO[].class);
        assertEquals(0, result.length, "this proposal should be empty");
    }

    @Test
    @Rollback
    @WithMockUser(username = "m.potenza@example.com", roles = {"TEACHER"})
    public void teacherAcceptedTest() throws Exception {
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/teacherAccepted/{id}", proposal.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProposalOnRequestDTO result = mapper.readValue(json, ProposalOnRequestDTO.class);
        assertEquals(result.getStatus(), ProposalOnRequest.Status.TEACHER_ACCEPTED, "this proposal should be accepted");
        LocalDate localDate1 = result.getApprovalDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        assertEquals(localDate1, localDate2, "this date should be equal");

        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/teacherAccepted/" + new Random().nextLong(4, 100))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/teacherAccepted")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/teacherAccepted/a")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    @Rollback
    @WithMockUser(username = "m.potenza@example.com", roles = {"TEACHER"})
    public void teacherRejectTest() throws Exception {
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/teacherRejected/{id}", proposal.get(1).getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProposalOnRequestDTO result = mapper.readValue(json, ProposalOnRequestDTO.class);
        assertEquals(result.getStatus(), ProposalOnRequest.Status.TEACHER_REJECTED, "this proposal should be rejected");

        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/teacherRejected/" + new Random().nextLong(4, 100))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/teacherRejected")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/teacherRejected/a")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Rollback
    @WithMockUser(username = "m.potenza@example.com", roles = {"TEACHER"})
    public void teacherChangeTest() throws Exception {
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/teacherRequestChange/{id}", proposal.get(2).getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProposalOnRequestDTO result = mapper.readValue(json, ProposalOnRequestDTO.class);
        assertEquals(result.getStatus(), ProposalOnRequest.Status.TEACHER_REVIEW, "this proposal should be rejected");

        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/teacherRequestChange/" + new Random().nextLong(4, 100))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/teacherRequestChange")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/teacherRequestChange/a")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }



    @Test
    @Rollback
    @WithMockUser(username = "m.potenza@example.com", roles = {"SECRETARY"})
    public void secretaryAcceptedTest() throws Exception {
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/secretaryAccepted/{id}", proposal.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProposalOnRequestDTO result = mapper.readValue(json, ProposalOnRequestDTO.class);
        assertEquals(result.getStatus(), ProposalOnRequest.Status.SECRETARY_ACCEPTED, "this proposal should be accepted");

        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/secretaryAccepted/" + new Random().nextLong(4, 100))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/secretaryAccepted")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/secretaryAccepted/a")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Rollback
    @WithMockUser(username = "m.potenza@example.com", roles = {"SECRETARY"})
    public void secretaryRejectedTest() throws Exception {
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/secretaryRejected/{id}", proposal.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProposalOnRequestDTO result = mapper.readValue(json, ProposalOnRequestDTO.class);
        assertEquals(result.getStatus(), ProposalOnRequest.Status.SECRETARY_REJECTED, "this proposal should be accepted");

        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/secretaryRejected/" + new Random().nextLong(4, 100))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/secretaryRejected")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/secretaryRejected/a")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
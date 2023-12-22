package it.polito.se2.g04.thesismanagement;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.polito.se2.g04.thesismanagement.application.Application;
import it.polito.se2.g04.thesismanagement.application.ApplicationRepository;
import it.polito.se2.g04.thesismanagement.application.ApplicationStatus;
import it.polito.se2.g04.thesismanagement.degree.Degree;
import it.polito.se2.g04.thesismanagement.degree.DegreeRepository;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.proposal.ProposalDTO;
import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
import it.polito.se2.g04.thesismanagement.proposal_on_request.*;
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

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.keycloak.util.JsonSerialization.mapper;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class ProposalOnRequestTest {
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
    private ProposalRepository proposalRepository;
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private MockMvc mockMvc;

    private List<ProposalOnRequest> proposalOnRequests = new ArrayList<ProposalOnRequest>();
    private Student student1;
    private Teacher teacher;

    @BeforeAll
    public void setup() {
        Degree degree = new Degree("ingegneria informatica");
        degreeRepository.save(degree);
        student1 = new Student("rossi", "marco", "male", "ita", "m.rossi@example.com", degree, 2020);
        student1 = studentRepository.save(student1);
        Student student2 = new Student("viola", "marta", "female", "ita", "m.viola@example.com", degree, 2018);
        student2 = studentRepository.save(student2);
        Student student3 = new Student("verde", "antonio", "male", "ita", "a.verde@example.com", degree, 2019);
        student3 = studentRepository.save(student3);
        teacher = new Teacher("Massimo", "Potenza", "m.potenza@example.com", null, null);
        teacher = teacherRepository.save(teacher);

        proposalOnRequests.add(proposalOnRequestRepository.save(new ProposalOnRequest(
                "titolo", "descrizione", teacher, student1, null, null
        )));
        proposalOnRequests.add(proposalOnRequestRepository.save(new ProposalOnRequest(
                "titolo2", "descrizione2", teacher, student2, null, null
        )));
        proposalOnRequests.add(proposalOnRequestRepository.save(new ProposalOnRequest(
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
    void getAllPendingTest() throws Exception {
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/API/proposalOnRequest/getAllPending")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProposalOnRequestFullDTO[] result = mapper.readValue(json, ProposalOnRequestFullDTO[].class);
        assertEquals(3, result.length, "this proposal should be 3 long");
        assertEquals(result[0].getId(), proposalOnRequests.get(0).getId(),"id should be the same");
        assertEquals(result[1].getId(), proposalOnRequests.get(1).getId(),"id should be the same");
        assertEquals(result[2].getId(), proposalOnRequests.get(2).getId(),"id should be the same");

        proposalOnRequestService.proposalOnRequestSecretaryRejected(proposalOnRequests.get(0).getId());
        res = mockMvc.perform(MockMvcRequestBuilders.get("/API/proposalOnRequest/getAllPending")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        result = mapper.readValue(json, ProposalOnRequestFullDTO[].class);
        assertEquals(2, result.length, "this proposal should be 2 long");
        assertEquals(result[0].getId(), proposalOnRequests.get(1).getId(),"id should be the same");
        assertEquals(result[1].getId(), proposalOnRequests.get(2).getId(),"id should be the same");

        proposalOnRequestService.proposalOnRequestSecretaryAccepted(proposalOnRequests.get(1).getId());
        proposalOnRequestService.proposalOnRequestSecretaryRejected((proposalOnRequests.get(2).getId()));
        res = mockMvc.perform(MockMvcRequestBuilders.get("/API/proposalOnRequest/getAllPending")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        result = mapper.readValue(json, ProposalOnRequestFullDTO[].class);
        assertEquals(0, result.length, "this proposal should be empty");
    }

    @Test
    @Rollback
    @WithMockUser(username = "m.potenza@example.com", roles = {"TEACHER"})
    void teacherAcceptedTest() throws Exception {
        proposalOnRequestService.proposalOnRequestSecretaryAccepted(proposalOnRequests.get(0).getId());
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/teacherAccepted/{id}", proposalOnRequests.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProposalOnRequestDTO result = mapper.readValue(json, ProposalOnRequestDTO.class);
        assertEquals(ProposalOnRequest.Status.TEACHER_ACCEPTED, result.getStatus(),  "this proposal should be accepted");
        LocalDate localDate1 = result.getApprovalDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        assertEquals(localDate1, localDate2, "this date should be equal");
       /* Optional<Proposal> proposal=proposalRepository.findAllByStatus(Proposal.Status.ACCEPTED).stream().filter(it->it.getTitle().compareTo(proposalOnRequests.get(0).getTitle())==0).findFirst();
        assertEquals(proposal.isPresent(),true,"Proposal Request is not getting added to proposal table");

        Optional<Application> application=applicationRepository.getApplicationByProposal_Id(proposal.get().getId()).stream().findFirst();
        assertEquals(application.isPresent(),true,"Proposal Request is not getting added to application table");
        assertEquals(application.get().getStudent().getId(), proposalOnRequests.get(0).getStudent().getId(),"Student of the application is mismatching");
        assertEquals(application.get().getStatus(), ApplicationStatus.ACCEPTED,"Application is not being accepted");*/

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
    void teacherRejectTest() throws Exception {
        proposalOnRequestService.proposalOnRequestSecretaryAccepted(proposalOnRequests.get(1).getId());
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/teacherRejected/{id}", proposalOnRequests.get(1).getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProposalOnRequestDTO result = mapper.readValue(json, ProposalOnRequestDTO.class);
        assertEquals( ProposalOnRequest.Status.TEACHER_REJECTED,result.getStatus(), "this proposal should be rejected");

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
    void teacherChangeTest() throws Exception {
        proposalOnRequestService.proposalOnRequestSecretaryAccepted(proposalOnRequests.get(2).getId());
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/teacherRequestChange/{id}", proposalOnRequests.get(2).getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProposalOnRequestDTO result = mapper.readValue(json, ProposalOnRequestDTO.class);
        assertEquals(ProposalOnRequest.Status.TEACHER_REVIEW,result.getStatus(),  "this proposal should be rejected");

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
    void secretaryAcceptedTest() throws Exception {
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/secretaryAccepted/{id}", proposalOnRequests.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProposalOnRequestDTO result = mapper.readValue(json, ProposalOnRequestDTO.class);
        assertEquals(ProposalOnRequest.Status.SECRETARY_ACCEPTED,result.getStatus(),  "this proposal should be accepted");

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
    void secretaryRejectedTest() throws Exception {
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/updateStatus/secretaryRejected/{id}", proposalOnRequests.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProposalOnRequestDTO result = mapper.readValue(json, ProposalOnRequestDTO.class);
        assertEquals(ProposalOnRequest.Status.SECRETARY_REJECTED,result.getStatus(),  "this proposal should be accepted");

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

    @Test
    @Rollback
    @WithMockUser(username = "m.rossi@example.com", roles = {"STUDENT"})
    void createProposalOnRequest() throws Exception{
        ProposalOnRequestDTO proposalOnRequestDTO=new ProposalOnRequestDTO(null, student1.getId(), "test","test", teacher.getId(), new ArrayList<>(),new Date(),null);
        proposalOnRequests.get(0).setStatus(ProposalOnRequest.Status.SECRETARY_REJECTED);
        proposalOnRequestRepository.save(proposalOnRequests.get(0));
        MvcResult res=mockMvc.perform(MockMvcRequestBuilders.post("/API/proposalOnRequest/create/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(proposalOnRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProposalOnRequestDTO result = mapper.readValue(json, ProposalOnRequestDTO.class);

        ProposalOnRequest proposalOnRequest=proposalOnRequestRepository.getReferenceById(result.getId());
        assertEquals(proposalOnRequest.getTitle(),proposalOnRequestDTO.getTitle(),"Title not matching");
        assertEquals(ProposalOnRequest.Status.PENDING,proposalOnRequest.getStatus(), "status is not PENDING");
        //Try to start a second proposalOnRequest (should get conflict)
        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposalOnRequest/create/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(proposalOnRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
        //reject the added proposalOnRequest
        ProposalOnRequest toChange= proposalOnRequestRepository.getReferenceById(result.getId());
        toChange.setStatus(ProposalOnRequest.Status.SECRETARY_REJECTED);
        proposalOnRequestRepository.save(toChange);

        //randomTeacherID
        proposalOnRequestDTO.setSupervisor(444l);
        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposalOnRequest/create/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(proposalOnRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        //random cosupervisor
        proposalOnRequestDTO.setSupervisor(teacher.getId());
        proposalOnRequestDTO.getCoSupervisors().add(444l);
        mockMvc.perform(MockMvcRequestBuilders.post("/API/proposalOnRequest/create/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(proposalOnRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }
    @Test
    @Rollback
    @WithMockUser(username = "m.rossi@example.com", roles = {"STUDENT"})
    void changeProposalOnRequestValid() throws Exception{
        //set the proposal to TEACHER_REVIEW
        proposalOnRequests.get(0).setStatus(ProposalOnRequest.Status.TEACHER_REVIEW);
        proposalOnRequestRepository.save(proposalOnRequests.get(0));
        //change the proposal on request
        ProposalOnRequestDTO proposalOnRequestDTO=new ProposalOnRequestDTO(proposalOnRequests.get(0).getId(), student1.getId(), "test2","test2", teacher.getId(), List.of(teacher.getId()),new Date(),null);
        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/makeChanges/{proposalId}",proposalOnRequestDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(proposalOnRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Rollback
    @WithMockUser(username = "m.rossi@example.com", roles = {"STUDENT"})
    void changeProposalOnRequestInvalidStatus() throws Exception{
        //change the proposal on request
        ProposalOnRequestDTO proposalOnRequestDTO=new ProposalOnRequestDTO(proposalOnRequests.get(0).getId(), student1.getId(), "test2","test2", teacher.getId(), List.of(teacher.getId()),new Date(),null);
        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/makeChanges/{proposalId}",proposalOnRequestDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(proposalOnRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    @Rollback
    @WithMockUser(username = "m.rossi@example.com", roles = {"STUDENT"})
    void changeProposalOnRequestTeacherNotFound() throws Exception{
        //set the proposal to TEACHER_REVIEW
        proposalOnRequests.get(0).setStatus(ProposalOnRequest.Status.TEACHER_REVIEW);
        proposalOnRequestRepository.save(proposalOnRequests.get(0));
        //change the proposal on request
        ProposalOnRequestDTO proposalOnRequestDTO=new ProposalOnRequestDTO(proposalOnRequests.get(0).getId(), student1.getId(), "test2","test2", teacher.getId(), List.of(1112332L),new Date(),null);
        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/makeChanges/{proposalId}",proposalOnRequestDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(proposalOnRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Rollback
    @WithMockUser(username = "m.viola@example.com", roles = {"STUDENT"})
    void changeProposalOnRequestWithUnAuthorizedUser() throws Exception{
        //set the proposal to TEACHER_REVIEW
        proposalOnRequests.get(0).setStatus(ProposalOnRequest.Status.TEACHER_REVIEW);
        proposalOnRequestRepository.save(proposalOnRequests.get(0));
        //change the proposal on request
        ProposalOnRequestDTO proposalOnRequestDTO=new ProposalOnRequestDTO(proposalOnRequests.get(0).getId(), student1.getId(), "test2","test2", teacher.getId(), List.of(teacher.getId()),new Date(),null);
        mockMvc.perform(MockMvcRequestBuilders.put("/API/proposalOnRequest/makeChanges/{proposalId}",proposalOnRequestDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(proposalOnRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }


}
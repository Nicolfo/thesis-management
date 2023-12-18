package it.polito.se2.g04.thesismanagement;




import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import io.jsonwebtoken.lang.Assert;
import it.polito.se2.g04.thesismanagement.application.ApplicationDTO;
import it.polito.se2.g04.thesismanagement.attachment.AttachmentDTO;
import it.polito.se2.g04.thesismanagement.degree.Degree;
import it.polito.se2.g04.thesismanagement.degree.DegreeRepository;
import it.polito.se2.g04.thesismanagement.department.Department;
import it.polito.se2.g04.thesismanagement.department.DepartmentRepository;
import it.polito.se2.g04.thesismanagement.group.Group;
import it.polito.se2.g04.thesismanagement.group.GroupRepository;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
import it.polito.se2.g04.thesismanagement.student.Student;
import it.polito.se2.g04.thesismanagement.student.StudentRepository;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import it.polito.se2.g04.thesismanagement.teacher.TeacherRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ApplyForProposalTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DegreeRepository degreeRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private ProposalRepository proposalRepository;
    @Autowired
    private StudentRepository studentRepository;
    private Proposal proposal;
    // You can still mock your service if needed



    @BeforeAll
    public void setup() throws Exception {
        Group group=groupRepository.save(new Group("Group 1"));
        Department department=departmentRepository.save(new Department("Department 1"));
        Degree degree=degreeRepository.save(new Degree("Degree 1"));
        //create a student
        Student toAdd = new Student("test","test","male","IT","email@example.com",degree,2022);
        studentRepository.save(toAdd);
        // Create a Teacher object for the supervisor
        Teacher supervisor = new Teacher("Supervisor Surname", "Supervisor Name", "supervisor@example.com", group, department);
        teacherRepository.save(supervisor);
        // Create a list of Teacher objects for co-supervisors
        List<Teacher> coSupervisors = new ArrayList<>();
        coSupervisors.add(new Teacher("CoSupervisor1 Surname", "CoSupervisor1 Name", "cosupervisor1@example.com", group, department));
        coSupervisors.add(new Teacher("CoSupervisor2 Surname", "CoSupervisor2 Name", "cosupervisor2@example.com", group, department));
        coSupervisors.forEach(it-> teacherRepository.save(it));


        // Create a Date object for the expiration date
        Date expirationDate = new Date();

        // Create a Proposal object using the constructor
        proposal = new Proposal();
        proposal.setTitle("Proposal Title");
        proposal.setSupervisor(supervisor);
        proposal.setCoSupervisors(coSupervisors);
        proposal.setKeywords("Keywords");
        proposal.setType("Proposal Type");
        proposal.setGroups(new ArrayList<>());
        proposal.setDescription("Description");
        proposal.setRequiredKnowledge("Required Knowledge");
        proposal.setNotes("Notes");
        proposal.setExpiration(expirationDate);
        proposal.setLevel("Proposal Level");
        proposal.setCds("CdS Value");
        proposal.getGroups().add(group);
        proposalRepository.save(proposal);
    }

    @AfterAll
    public void cleanup(){
        proposalRepository.deleteAll();
        teacherRepository.deleteAll();
        studentRepository.deleteAll();
        groupRepository.deleteAll();
        departmentRepository.deleteAll();
        degreeRepository.deleteAll();


    }
    @Test
    @Rollback
    @WithMockUser(username = "email@example.com", roles = {"STUDENT"})
    public void testApplyProposalAndUploadFile() throws Exception {
        Path tempFile = Files.createTempFile("test-file", ".txt");
        Files.write(tempFile, "Test file content".getBytes());
        MockMultipartFile file = new MockMultipartFile("file", "test-file.txt", MediaType.TEXT_PLAIN_VALUE, Files.readAllBytes(tempFile));

        // Upload a file to get the AttachmentDTO
        MvcResult uploadResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/API/uploadFile")
                        .file(file))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract attachmentID from the uploadResult
        JsonObject jsonAttachmentDTO = JsonParser.parseString(uploadResult.getResponse().getContentAsString()).getAsJsonObject();
        Assert.notNull(jsonAttachmentDTO);
        Assert.notNull(jsonAttachmentDTO.get("id").getAsLong());
        Assert.notNull(jsonAttachmentDTO.get("filename").getAsString());
        AttachmentDTO attachmentDTO = new AttachmentDTO(jsonAttachmentDTO.get("id").getAsLong(),jsonAttachmentDTO.get("filename").getAsString());
        Long attachmentID = attachmentDTO.getId();

        // Prepare a sample ApplicationDTO with the obtained attachmentID
        ApplicationDTO applicationDTO = new ApplicationDTO();
        applicationDTO.setAttachmentId(attachmentID);
        applicationDTO.setApplyDate(new Date());
        applicationDTO.setProposalId(proposal.getId());
        //ApplicationDTO applicationDTO = new ApplicationDTO(attachmentID,new Date(), proposal.getId());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/API/application/insert/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(applicationDTO));

        // Perform the request and assert the status
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk()) // Change this based on your expected behavior
                .andReturn();
        // Clean up: Delete the temporary file
        Files.delete(tempFile);
    }
}


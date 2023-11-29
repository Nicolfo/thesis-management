package it.polito.se2.g04.thesismanagement;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import io.jsonwebtoken.lang.Assert;
import it.polito.se2.g04.thesismanagement.application.Application;
import it.polito.se2.g04.thesismanagement.application.ApplicationRepository;
import it.polito.se2.g04.thesismanagement.attachment.Attachment;
import it.polito.se2.g04.thesismanagement.attachment.AttachmentDTO;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc(addFilters = false)
public class AttachmentTest {
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

    private Teacher teacher;
    private Student student;
    private Proposal proposal;
    private Proposal proposal1;
    private Proposal proposal2;
    private Application application1;
    private Application application2;
    private AttachmentDTO attachmentDTO;
    private MockMultipartFile file;

    @BeforeAll
    @Test
    public void testUploadFile() throws Exception {
        Path tempFile = Files.createTempFile("test-file", ".txt");
        Files.write(tempFile, "Test file content".getBytes());
         file = new MockMultipartFile("file", "test-file.txt", MediaType.TEXT_PLAIN_VALUE, Files.readAllBytes(tempFile));

        // Upload a file to get the AttachmentDTO
        MvcResult uploadResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/API/uploadFile")
                        .file(file)
                        )
                .andExpect(status().isCreated())
                .andReturn();
        // Extract attachmentID from the uploadResult
        JsonObject jsonAttachmentDTO = JsonParser.parseString(uploadResult.getResponse().getContentAsString()).getAsJsonObject();
        Assert.notNull(jsonAttachmentDTO);
        Assert.notNull(jsonAttachmentDTO.get("id").getAsLong());
        Assert.notNull(jsonAttachmentDTO.get("filename").getAsString());
        attachmentDTO = new AttachmentDTO(jsonAttachmentDTO.get("id").getAsLong(),jsonAttachmentDTO.get("filename").getAsString());
        Assert.isTrue(attachmentDTO.getFilename().compareTo("test-file.txt")==0);
    }

    @AfterAll
    public void cleanup() {

    }


    @Test
    @Rollback
    public void testDownlaodFile() throws Exception {
// Perform the GET request
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/API/getFile/{id}", attachmentDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.status().isOk())
                 .andReturn();
        byte[] fileContent=result.getResponse().getContentAsByteArray();
        for(int i =0; i<file.getBytes().length;i++)
            assertEquals(fileContent[i],file.getBytes()[i]);
    }



}

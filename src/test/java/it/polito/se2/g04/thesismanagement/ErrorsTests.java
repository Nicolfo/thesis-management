package it.polito.se2.g04.thesismanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.se2.g04.thesismanagement.application.*;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ErrorsTests {
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
    @Autowired
    private ApplicationService applicationService;

    private Teacher teacher;
    private Student student;
    private Proposal proposal1;
    private Application application1;
    private Application application2;
    @BeforeAll
    public void setup() {
        Group saved=groupRepository.save(new Group("Group 1"));
        Department department=departmentRepository.save(new Department("Department 1"));

        teacher = new Teacher("Gerald", "Juarez","test@example.com",null,null);
        teacherRepository.save(teacher);

        student = new Student("Georgina","Ferrell","female","italian","georgina.ferrell@example.com",null,2020);
        student=studentRepository.save(student);

        proposal1 = new Proposal();
        proposal1.setTitle("Patch-based discriminative learning for Iris Presentation Attack Detection");
        proposal1.setSupervisor(teacher);
        proposal1.setCoSupervisors(null);
        proposal1.setKeywords("Iris, PAD, Recognition, Detection, Spoofing");
        proposal1.setType("Bachelor Thesis");
        proposal1.setGroups(null);
        proposal1.setDescription("Iris recognition is considered a prominent biometric authentication method. The accuracy, usability, and touchless acquisition of iris recognition have led to their wide deployments.");
        proposal1.setRequiredKnowledge("Good programming skills, at least 2.0 in AuD, Basic Knowledge about AI");
        proposal1.setNotes(null);
        proposal1.setExpiration(new Date(2024, Calendar.DECEMBER, 10));
        proposal1.setLevel(null);
        proposal1.setCds(null);

        proposal1 = proposalRepository.save(proposal1);

        application1 = new Application(student,null,new Date(2023,Calendar.NOVEMBER,13),proposal1);
        application1.setStatus(ApplicationStatus.CANCELLED);
        application1 =applicationRepository.save(application1);
        application2 = new Application(student,null,new Date(2023,Calendar.NOVEMBER,13),proposal1);


    }

    @AfterAll
    public void CleanUp() {
        applicationRepository.deleteAll();
        proposalRepository.deleteAll();
        teacherRepository.deleteAll();
        studentRepository.deleteAll();
        groupRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    @Test
    @Rollback
    @WithMockUser(username = "test@example.com", roles = {"TEACHER"})
    void ApplicationDeletedExceptionTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/API/application/getApplicationById/{applicationId}",application1.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Rollback
    @WithMockUser(username = "georgina.ferrell@example.com", roles = {"STUDENT"})
    void DuplicateApplicationExceptionTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        application1.setStatus(ApplicationStatus.PENDING);
        application1=applicationRepository.save(application1);
        ApplicationDTO applicationDTO=applicationService.getApplicationDTO(application2);
        mockMvc.perform(MockMvcRequestBuilders.post("/API/application/insert/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(applicationDTO)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }
}
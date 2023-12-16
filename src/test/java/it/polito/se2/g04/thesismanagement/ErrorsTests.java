package it.polito.se2.g04.thesismanagement;

import it.polito.se2.g04.thesismanagement.application.Application;
import it.polito.se2.g04.thesismanagement.application.ApplicationRepository;
import it.polito.se2.g04.thesismanagement.application.ApplicationStatus;
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
public class ErrorsTests {
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
    private Proposal proposal1;
    private Application application1;
    @BeforeAll
    public void setup() {
        Group saved=groupRepository.save(new Group("Group 1"));
        Department department=departmentRepository.save(new Department("Department 1"));

        teacher = new Teacher("Gerald", "Juarez","test@example.com",null,null);
        teacherRepository.save(teacher);

        student = new Student("Georgina","Ferrell","female","italian","georgina.ferrell@example.com",null,2020);
        student=studentRepository.save(student);

        proposal1 = new Proposal("Patch-based discriminative learning for Iris Presentation Attack Detection",teacher,null,"Iris, PAD, Recognition, Detection, Spoofing","Bachelor Thesis",null,"Iris recognition is considered a prominent biometric authentication method. The accuracy, usability and touchless acquisition of iris recognition have led to their wide deployments.", "Good programming skills, atleast 2.0 in AuD, Basic Knowledge about AI",null,new Date(2024, Calendar.DECEMBER,10),null,null);

        proposal1 = proposalRepository.save(proposal1);

        application1 = new Application(student,null,new Date(2023,Calendar.NOVEMBER,13),proposal1);
        application1.setStatus(ApplicationStatus.CANCELLED);
        application1 =applicationRepository.save(application1);

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
    public void ApplicationDeletedExceptionTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/API/application/getApplicationById/{applicationId}",application1.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
package it.polito.se2.g04.thesismanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.se2.g04.thesismanagement.application.Application;
import it.polito.se2.g04.thesismanagement.application.ApplicationRepository;
import it.polito.se2.g04.thesismanagement.application.ApplicationStatus;
import it.polito.se2.g04.thesismanagement.career.CareerRepository;
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

public class BrowseDecisionsTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ProposalRepository proposalRepository;
    @Autowired
    private CareerRepository careerRepository;
    @Autowired
    private ApplicationRepository applicationRepository;
    private Teacher teacher;
    private Student student;

    private Proposal proposal1;
    private Proposal proposal2;

    private Application application1;
    private Application application2;

    @BeforeAll
    public void setup() throws Exception {

        applicationRepository.deleteAll();
        careerRepository.deleteAll();
        proposalRepository.deleteAll();
        teacherRepository.deleteAll();
        studentRepository.deleteAll();
        departmentRepository.deleteAll();
        groupRepository.deleteAll();

        ObjectMapper objectMapper=new ObjectMapper();

        Group group=groupRepository.save(new Group("SOFTENG"));
        Department department=departmentRepository.save(new Department("Automation and Computer Science"));

        teacher = new Teacher("Bianchi","Marco","marco.bianchi@example.com",group, department);
        teacherRepository.save(teacher);

        student = new Student("Ferrari","Alessia","Female","Italy","alessia.ferrari@example.com",null,2020);
        studentRepository.save(student);

        proposal1 = new Proposal("Patch-based discriminative learning for Iris Presentation Attack Detection",teacher,null,"Iris, PAD, Recognition, Detection, Spoofing","Bachelor Thesis",null,"Iris recognition is considered a prominent biometric authentication method. The accuracy, usability and touchless acquisition of iris recognition have led to their wide deployments.", "Good programming skills, atleast 2.0 in AuD, Basic Knowledge about AI",null,new Date(2024, Calendar.DECEMBER,10),null,null);
        proposal2 = new Proposal("Advanced driver assistance systems (ADASs) and autonomous vehicles rely on different types of sensors, such as camera, radar, ultrasonic, and LiDAR, to sense the surrounding environment.", teacher, null, "Radar, Radar Imaging, Radar Detection, Doppler Radar, Doppler Effect, Laser Radar, Radar Antennas", "Development", null, "Advanced driver assistance systems (ADASs) and autonomous vehicles rely on different types of sensors, such as camera, radar, ultrasonic, and LiDAR, to sense the surrounding environment.", "Sensor integration, Machine learning, Software development.", "notes", null, "Bachelor's", "Computer Engineering");

        proposal1 = proposalRepository.save(proposal1);
        proposal2 = proposalRepository.save(proposal2);

        application1 = new Application(student,null,new Date(2023,Calendar.NOVEMBER,13),proposal2);
        application1.setStatus(ApplicationStatus.PENDING);
        application2 = new Application(student,null,new Date(2023,Calendar.NOVEMBER,7),proposal1);
        applicationRepository.save(application1);
        application2.setStatus(ApplicationStatus.REJECTED);
        applicationRepository.save(application2);
    }

    @AfterAll
    void cleanup(){
        applicationRepository.deleteAll();
        careerRepository.deleteAll();
        proposalRepository.deleteAll();
        teacherRepository.deleteAll();
        studentRepository.deleteAll();
        departmentRepository.deleteAll();
        groupRepository.deleteAll();
    }

    @Test
    @Rollback
    @WithMockUser(username = "alessia.ferrari@example.com", roles = {"STUDENT"})
    public void getAllApplicationProf() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/API/application/getByStudent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value(ApplicationStatus.PENDING.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].status").value(ApplicationStatus.REJECTED.toString()));
    }
}

package it.polito.se2.g04.thesismanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.se2.g04.thesismanagement.application.Application;
import it.polito.se2.g04.thesismanagement.application.ApplicationRepository;
import it.polito.se2.g04.thesismanagement.application.ApplicationService;
import it.polito.se2.g04.thesismanagement.application.ApplicationStatus;
import it.polito.se2.g04.thesismanagement.department.Department;
import it.polito.se2.g04.thesismanagement.department.DepartmentRepository;
import it.polito.se2.g04.thesismanagement.group.Group;
import it.polito.se2.g04.thesismanagement.group.GroupRepository;
import it.polito.se2.g04.thesismanagement.notification.EmailService;
import it.polito.se2.g04.thesismanagement.notification.Notification;
import it.polito.se2.g04.thesismanagement.notification.NotificationRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.type.TypeReference;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.Thread.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NotificationTest {
    @Autowired
    private ProposalRepository proposalRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    ApplicationRepository applicationRepository;
    @Autowired
    ApplicationService applicationService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    NotificationRepository notificationRepository;

    private Application application1;
    private Application application2;
    private Application application3;


    @BeforeAll
    public void setup() throws Exception {
        groupRepository.save(new Group("Group 1"));
        departmentRepository.save(new Department("Department 1"));

        Teacher teacher = new Teacher("Gerald", "Juarez", "test@example.com", null, null);
        teacherRepository.save(teacher);

        Student student = new Student("Georgina", "Ferrell", "female", "italian", "georgina.ferrell@example.com", null, 2020);
        student = studentRepository.save(student);
        Student student2 = new Student("Munz", "Marco", "male", "italian", "marco.munz@example.com", null, 2022);
        student2 = studentRepository.save(student2);

        Proposal proposal1 = new Proposal("Patch-based discriminative learning for Iris Presentation Attack Detection", teacher, null, "Iris, PAD, Recognition, Detection, Spoofing", "Bachelor Thesis", null, "Iris recognition is considered a prominent biometric authentication method. The accuracy, usability and touchless acquisition of iris recognition have led to their wide deployments.", "Good programming skills, atleast 2.0 in AuD, Basic Knowledge about AI", null, new Date(2024, Calendar.DECEMBER, 10), null, null);
        Proposal proposal2 = new Proposal("Proposal 2", teacher, null, "keywords", "type", null, "Description 2", "requiredKnowledge", "notes", null, "level", "CdS");

        proposal1 = proposalRepository.save(proposal1);
        proposal2 = proposalRepository.save(proposal2);

        application1 = new Application(student, null, new Date(2023, Calendar.NOVEMBER, 13), proposal1);
        application2 = new Application(student2, null, new Date(2023, Calendar.NOVEMBER, 7), proposal1);
        application3 = new Application(student, null, new Date(2023, Calendar.OCTOBER, 26), proposal2);

        applicationRepository.save(application1);
        applicationRepository.save(application2);
        applicationRepository.save(application3);
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
    @Order(1)
    @WithMockUser(username = "georgina.ferrell@example.com", roles = {"STUDENT"})
    public void testEmptyNotification() throws Exception {
        application1.setStatus(ApplicationStatus.PENDING);
        application2.setStatus(ApplicationStatus.PENDING);
        application3.setStatus(ApplicationStatus.PENDING);
        application1 = applicationRepository.save(application1);
        application2 = applicationRepository.save(application2);
        application3 = applicationRepository.save(application3);

        //get result
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/API/notification/getAllNotificationsOfCurrentUser/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();

        assertEquals("[]", json);

        notificationRepository.save(new Notification("test@example.com","Application status changed","Application status changed","\"Application status changed\"","new.png", new Date()));
        notificationRepository.save(new Notification("georgina.ferrell@example.com","Application status changed","Application status changed","\"Application status changed\"","new.png", new Date()));
        notificationRepository.save(new Notification("georgina.ferrell@example.com","Application status changed","Application status changed","\"Application status changed\"","new.png", new Date()));

        //get result
        res = mockMvc.perform(MockMvcRequestBuilders.get("/API/notification/getAllNotificationsOfCurrentUser/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        List<Notification> notifications = objectMapper.readValue(json, new TypeReference<>() {});

        assertEquals(2, notifications.size());

        for (Notification notification : notifications) {
            assertEquals("georgina.ferrell@example.com", notification.getRecipient());
            assertFalse(notification.isRead());
        }

        res = mockMvc.perform(MockMvcRequestBuilders.get("/API/notification/getUnreadNotificationsCount/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();

        assertEquals(json,"2");


        Notification referenceNotification = notifications.get(0);

        mockMvc.perform(MockMvcRequestBuilders.put("/API/notification/markNotificationAsRead/"+referenceNotification.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();


        res = mockMvc.perform(MockMvcRequestBuilders.get("/API/notification/getSingleNotifications/"+referenceNotification.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();

        Notification notification = objectMapper.readValue(json, new TypeReference<>() {});
        assertEquals(notification.getId(),referenceNotification.getId());
        assertTrue(notification.isRead());
    }

}

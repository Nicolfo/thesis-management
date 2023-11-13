package it.polito.se2.g04.thesismanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.polito.se2.g04.thesismanagement.security.user.User;
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
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TeacherControllerTest {

    private Teacher g1;
    private Teacher g2;
    private Teacher g3;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public void setup() {
        g1 = new Teacher("Gerald", "Juarez","test@example.com",null,null);
        g1 = teacherRepository.save(g1);
        g2 = new Teacher("Rossi", "Mario","m.rossi@example.com",null,null);
        g2 = teacherRepository.save(g2);
        g3 = new Teacher("Verdi", "Luigi","l.verdi@example.com",null,null);
        g3 = teacherRepository.save(g3);

        //mock logged in user
        User user = new User("test@example.com", "password", "TEACHER");
        Authentication auth = new TestingAuthenticationToken(user, "password");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterAll
    public void CleanUp(){
        teacherRepository.deleteAll();
    }

    @Test
    @Rollback
    public void getAllTeachers() throws Exception {
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/API/teacher/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Teacher[] teachers = mapper.readValue(json, Teacher[].class);

        // Check that size matches
        assertEquals(3, teachers.length, "Teachers' getAll should return 3 values");

        // Check that surnames match
        assertEquals(g1.getSurname(), teachers[0].getSurname(), "Wrong Teacher surname");
        assertEquals(g2.getSurname(), teachers[1].getSurname(), "Wrong Teacher surname");
        assertEquals(g3.getSurname(), teachers[2].getSurname(), "Wrong Teacher surname");

        // Add a Teacher
        Teacher g4 = new Teacher("Bianchi", "Valerio","v.bianchi@example.com", null, null);
        g4 = teacherRepository.save(g4);

        // Perform request again: now I should get 4 Teachers
        res = mockMvc.perform(MockMvcRequestBuilders.get("/API/teacher/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        teachers = mapper.readValue(json, Teacher[].class);

        // Check that size matches
        assertEquals(4, teachers.length, "Teachers' getAll should return 4 values after insert");

        // Check that new title matches
        assertEquals(g4.getSurname(), teachers[3].getSurname(), "Wrong Teacher surname");
    }

}

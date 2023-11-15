package it.polito.se2.g04.thesismanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.polito.se2.g04.thesismanagement.security.user.User;
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
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc(addFilters = false) //addFilters = false: no authetification needed to call methods

public class TeacherControllerTest {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public void setup() {
        //mock logged in user
        /*
        User user = new User("test@example.com", "password", "TEACHER");
        Authentication auth = new TestingAuthenticationToken(user, "password");
        SecurityContextHolder.getContext().setAuthentication(auth);*/
    }

    @AfterAll
    public void CleanUp() {
        teacherRepository.deleteAll();
    }

    @Test
    @Rollback
    public void getAll() throws Exception {
        Teacher teacher1 = new Teacher("Gerald", "Juarez","test@example.com",null,null);
        teacherRepository.save(teacher1);
        Teacher teacher2 = new Teacher("Massimo", "Potenza","test2@example.com",null,null);
        teacherRepository.save(teacher2);

        MvcResult res= mockMvc.perform(MockMvcRequestBuilders.get("API/teacher/getAll/").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Teacher[] teachers = mapper.readValue(json, Teacher[].class);

        assertEquals(2,teachers.length,"getAll() should return 2");

        List<Long> teacherIds = Arrays.stream(teachers)
                .map(Teacher::getId)
                .toList();
        List<Long> expectedIds = List.of(teacher1.getId(), teacher2.getId());
        assertTrue(teacherIds.containsAll(expectedIds), "getAll does not return the correct values");


        Teacher teacher3 = new Teacher("Mario", "Rossi","test3@example.com",null,null);
        teacherRepository.save(teacher3);
        Teacher teacher4 = new Teacher("Georgina", "Ferrell","test4@example.com",null,null);
        teacherRepository.save(teacher4);

        res= mockMvc.perform(MockMvcRequestBuilders.get("API/teacher/getAll/").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json= res.getResponse().getContentAsString();
        Teacher[] teachers2 = mapper.readValue(json, Teacher[].class);

        assertEquals(4,teachers2.length,"getAll() should return 4");

        List<Long> teacherIds2 = Arrays.stream(teachers)
                .map(Teacher::getId)
                .toList();
        List<Long> expectedIds2 = List.of(teacher1.getId(), teacher2.getId(),teacher3.getId(), teacher4.getId());
        assertTrue(teacherIds2.containsAll(expectedIds2), "getAll does not return the correct values");


        teacherRepository.deleteAll();

        res= mockMvc.perform(MockMvcRequestBuilders.get("API/teacher/getAll/").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json= res.getResponse().getContentAsString();
        Teacher[] teachersEmpty = mapper.readValue(json, Teacher[].class);

        assertEquals(0,teachers2.length,"getAll() should return 0");
    }

    @Test
    @Rollback
    public void getById() throws Exception {
        Teacher teacher1 = new Teacher("Gerald", "Juarez","test@example.com",null,null);
        teacherRepository.save(teacher1);
        Teacher teacher2 = new Teacher("Massimo", "Potenza","test@example.com",null,null);
        teacherRepository.save(teacher2);

        MvcResult res= mockMvc.perform(MockMvcRequestBuilders.get("API/teacher/getById/{id}","1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Teacher[] teachers = mapper.readValue(json, Teacher[].class);

        assertEquals(1,teachers.length,"getById() should return 1");

        List<Long> teacherIds = Arrays.stream(teachers)
                .map(Teacher::getId)
                .toList();
        Long expectedIds = teacher1.getId();
        assertTrue(teacherIds.contains(expectedIds), "getById() does not return the correct values");

        mockMvc.perform(MockMvcRequestBuilders.post("/API/teacher/getById/").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Rollback
    public void getByEmail() throws Exception {
        Teacher teacher1 = new Teacher("Gerald", "Juarez","test@example.com",null,null);
        teacherRepository.save(teacher1);
        Teacher teacher2 = new Teacher("Massimo", "Potenza","test2@example.com",null,null);
        teacherRepository.save(teacher2);

        MvcResult res= mockMvc.perform(MockMvcRequestBuilders.get("API/teacher/getByEmail/{email}","test@example.com").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Teacher[] teachers = mapper.readValue(json, Teacher[].class);

        assertEquals(1,teachers.length,"getByEmail() should return 1");

        List<String> teacherEmails = Arrays.stream(teachers)
                .map(Teacher::getEmail)
                .toList();
        String expectedEmail = teacher1.getEmail();
        assertTrue(teacherEmails.contains(expectedEmail), "getByEmail() does not return the correct values");

        mockMvc.perform(MockMvcRequestBuilders.post("/API/teacher/getByEmail/").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
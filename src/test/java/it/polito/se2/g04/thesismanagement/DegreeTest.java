package it.polito.se2.g04.thesismanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.polito.se2.g04.thesismanagement.degree.Degree;
import it.polito.se2.g04.thesismanagement.degree.DegreeRepository;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class DegreeTest {
    @Autowired
    private DegreeRepository degreeRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private MockMvc mockMvc;

    private List<Degree> degree = new ArrayList<Degree>();

    @BeforeAll
    public void setup() {
        degree.add(degreeRepository.save(new Degree("ingegneria informatica")));
        degree.add(degreeRepository.save(new Degree("ingegneria elettronica")));
        degree.add(degreeRepository.save(new Degree("ingegneria aereospaziale")));
        degree.add(degreeRepository.save(new Degree("ingegneria gestionale")));


        Teacher teacher = new Teacher("Massimo", "Potenza", "m.potenza@example.com", null, null);
        teacher = teacherRepository.save(teacher);
    }

    @AfterAll
    public void CleanUp() {
        degreeRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    @Test
    @Rollback
    @WithMockUser(username = "m.potenza@example.com", roles = {"TEACHER"})
    void getAllNameTest() throws Exception {
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/API/Degree/getAllNames")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String[] finalResult = mapper.readValue(json, String[].class);

        assertEquals(4, finalResult.length, "we should have 4 names");

        assertEquals("ingegneria informatica", finalResult[0], "this should be ingegneria informatica");
        assertEquals("ingegneria elettronica", finalResult[1], "this should be ingegneria elettronica");
        assertEquals("ingegneria aereospaziale", finalResult[2], "this should be ingegneria aereospaziale");
        assertEquals("ingegneria gestionale", finalResult[3], "this should be ingegneria gestionale");
    }
}
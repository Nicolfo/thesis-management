package it.polito.se2.g04.thesismanagement;

import it.polito.se2.g04.thesismanagement.career.Career;
import it.polito.se2.g04.thesismanagement.career.CareerRepository;
import it.polito.se2.g04.thesismanagement.degree.Degree;
import it.polito.se2.g04.thesismanagement.degree.DegreeRepository;
import it.polito.se2.g04.thesismanagement.student.Student;
import it.polito.se2.g04.thesismanagement.student.StudentRepository;
import it.polito.se2.g04.thesismanagement.student.StudentService;
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
public class StudentTest {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private DegreeRepository degreeRepository;
    @Autowired
    private StudentService studentService;
    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private MockMvc mockMvc;

    private final List<Student> students=new ArrayList<Student>();
    private Degree degree;

    @BeforeAll
    public void setup() {
        degree = new Degree("ingegneria informatica");
        degree=degreeRepository.save(degree);

        Student student1 = new Student("rossi", "marco", "male", "ita", "m.rossi@example.com", degree, 2020);
        Student student2 = new Student("viola", "marta", "female", "ita", "m.viola@example.com", degree, 2018);

        students.addAll(List.of(studentRepository.save(student1),studentRepository.save(student2)));

        Career career1=new Career("cod1","titolo1",6,22,null,student1);
        Career career2=new Career("cod2","titolo2",6,23,null,student1);
        Career career3=new Career("cod3","titolo3",6,24,null,student1);
        careerRepository.saveAll(List.of(career1,career2,career3));
        Career career4=new Career("cod1","titolo1",6,25,null,student2);
        Career career5=new Career("cod2","titolo2",6,21,null,student2);
        Career career6=new Career("cod3","titolo3",6,18,null,student2);
        Career career7=new Career("cod3","titolo4",6,22,null,student2);
        careerRepository.saveAll(List.of(career4,career5,career6,career7));

    }

    @AfterAll
    public void CleanUp() {
        careerRepository.deleteAll();
        studentRepository.deleteAll();
        degreeRepository.deleteAll();
    }

    @Test
    @Rollback
    public void getCdsTest() {
        assertEquals(degree.getTitleDegree(),studentService.getCdS(students.get(0).getEmail()),"student1 degree is correct");
        assertEquals(degree.getTitleDegree(),studentService.getCdS(students.get(1).getEmail()),"student2 degree is correct");
    }

    @Test
    @Rollback
    public void getAverageMarksTest() throws Exception {
        MvcResult res=mockMvc.perform(MockMvcRequestBuilders.get("/API/student/getAverageMarks/{studentId}", students.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        assertEquals("23.0",json,"student1 average should be 23");

        res=mockMvc.perform(MockMvcRequestBuilders.get("/API/student/getAverageMarks/{studentId}", students.get(1).getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();
        assertEquals("21.5",json,"student2 average should be 21.5");
    }

    @Test
    @Rollback
    public void getNameByIdTest() throws Exception {
        MvcResult res=mockMvc.perform(MockMvcRequestBuilders.get("/API/student/getNameById/{studentId}", students.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();

        assertEquals("marco rossi", json, "the name of student1 should be Marco rossi");
    }
}

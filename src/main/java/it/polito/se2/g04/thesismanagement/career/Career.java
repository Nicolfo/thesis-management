package it.polito.se2.g04.thesismanagement.career;

import it.polito.se2.g04.thesismanagement.student.Student;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Career {
    @Id
    @GeneratedValue
    private Long id;
    //maybe we should add a course table
    private String codCourse;
    private String titleCourse;
    private int cfu;
    private Integer grade;
    private Date date;
    @ManyToOne
    private Student student;

    public Career(String codCourse, String titleCourse, int cfu, Integer grade, Date date, Student student){
        this.codCourse=codCourse;
        this.titleCourse=titleCourse;
        this.cfu = cfu;
        this.grade=grade;
        this.date=date;
        this.student=student;
    }
}

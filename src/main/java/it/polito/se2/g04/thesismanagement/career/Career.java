package it.polito.se2.g04.thesismanagement.career;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Career {
    @Id
    @GeneratedValue
    private Long id;
    //maybe we should add a course table
    private Long codCourse;
    private String titleCourse;
    private int CFU;
    private Integer grade;
    private Date date;
}

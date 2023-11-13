package it.polito.se2.g04.thesismanagement.student;

import it.polito.se2.g04.thesismanagement.career.Career;
import it.polito.se2.g04.thesismanagement.degree.Degree;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Student {
    public Student(String surname, String name, String gender, String nationality, String email, Degree degree, int enrollmentYear) {
        this.surname = surname;
        this.name = name;
        this.gender = gender;
        this.nationality = nationality;
        this.email = email;
        this.degree = degree;
        this.enrollmentYear = enrollmentYear;
    }

    @Id
    @GeneratedValue
    private Long id;
    private String surname;
    private String name;
    private String gender;
    private String nationality;
    private String email;
    @ManyToOne
    @JoinColumn(name = "COD_DEGREE")
    private Degree degree;
    private int enrollmentYear;


}

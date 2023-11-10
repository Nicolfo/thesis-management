package it.polito.se2.g04.thesismanagement.student;

import it.polito.se2.g04.thesismanagement.degree.Degree;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Student {
    @Id
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

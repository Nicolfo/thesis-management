package it.polito.se2.g04.thesismanagement.student;

import it.polito.se2.g04.thesismanagement.degree.Degree;
import it.polito.se2.g04.thesismanagement.degree.DegreeDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {
    private String fullName;
    private Long id;
    private String surname;
    private String name;
    private String gender;
    private String nationality;
    private String email;
    private DegreeDTO degree;

    public StudentDTO(String fullName) {
        this.fullName = fullName;
    }

    public static StudentDTO fromStudent(Student student) {
        if (student == null)
            return null;
        return new StudentDTO(
                student.getName() + " " + student.getSurname(),
                student.getId(),
                student.getSurname(),
                student.getName(),
                student.getGender(),
                student.getNationality(),
                student.getEmail(),
                DegreeDTO.fromDegree(student.getDegree())
        );
    }
}

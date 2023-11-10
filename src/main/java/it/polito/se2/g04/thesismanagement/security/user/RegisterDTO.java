package it.polito.se2.g04.thesismanagement.security.user;

import it.polito.se2.g04.thesismanagement.degree.Degree;
import it.polito.se2.g04.thesismanagement.department.Department;
import it.polito.se2.g04.thesismanagement.group.Group;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RegisterDTO {
    //shared
    private String surname;
    private String name;
    private String email;
    private String role;
    private String password;
    //professor info
    private String codGroup;
    private String codDepartment;
    //student info
    private String gender;
    private String nationality;
    private Long codDegree;
    private int enrollmentYear;
}

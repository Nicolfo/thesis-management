package it.polito.se2.g04.thesismanagement.teacher;

import it.polito.se2.g04.thesismanagement.department.Department;
import it.polito.se2.g04.thesismanagement.group.Group;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TeacherDTO {
    private Long id;
    private String surname;
    private String name;
    private String email;
    private Group group;
    private Department department;
}

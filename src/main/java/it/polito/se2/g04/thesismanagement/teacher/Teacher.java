package it.polito.se2.g04.thesismanagement.teacher;

import it.polito.se2.g04.thesismanagement.department.Department;
import it.polito.se2.g04.thesismanagement.group.Group;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Teacher {
    @Id
    @GeneratedValue
    private Long id;
    private String surname;
    private String name;
    private String email;
    @ManyToOne
    @JoinColumn(name = "COD_GROUP")
    private Group group;
    @ManyToOne
    @JoinColumn(name = "COD_DEPARTMENT")
    private Department department;

}

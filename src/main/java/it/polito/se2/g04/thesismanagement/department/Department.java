package it.polito.se2.g04.thesismanagement.department;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Department {
    @Id
    private String codDepartment;
    private String name;
}

package it.polito.se2.g04.thesismanagement.department;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    private Long codDepartment;
    private String name;

    public static DepartmentDTO fromDepartment(Department department) {
        if (department == null)
            return null;
        return new DepartmentDTO(department.getCodDepartment(), department.getName());
    }
}

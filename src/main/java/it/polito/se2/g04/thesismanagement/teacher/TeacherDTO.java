package it.polito.se2.g04.thesismanagement.teacher;

import it.polito.se2.g04.thesismanagement.department.DepartmentDTO;
import it.polito.se2.g04.thesismanagement.group.GroupDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class TeacherDTO {
    private Long id;
    private String surname;
    private String name;
    private String email;
    private GroupDTO group;
    private DepartmentDTO department;

    public static TeacherDTO fromTeacher(Teacher teacher) {
        if (teacher == null)
            return null;
        return new TeacherDTO(teacher.getId(), teacher.getSurname(), teacher.getName(), teacher.getEmail(), GroupDTO.fromGroup(teacher.getGroup()), DepartmentDTO.fromDepartment(teacher.getDepartment()));
    }
}

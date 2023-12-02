package it.polito.se2.g04.thesismanagement.career;

import it.polito.se2.g04.thesismanagement.student.StudentDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CareerDTO {
    private Long id;
    //maybe we should add a course table
    private String codCourse;
    private String titleCourse;
    private int cfu;
    private Integer grade;
    private Date date;
    private StudentDTO student;

    public static CareerDTO fromCareer(Career career) {
        if (career == null)
            return null;
        return new CareerDTO(career.getId(), career.getCodCourse(), career.getTitleCourse(), career.getCfu(), career.getGrade(), career.getDate(), StudentDTO.fromStudent(career.getStudent()));
    }
}

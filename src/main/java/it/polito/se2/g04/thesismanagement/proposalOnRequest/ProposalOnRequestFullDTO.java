package it.polito.se2.g04.thesismanagement.proposalOnRequest;

import it.polito.se2.g04.thesismanagement.student.StudentDTO;
import it.polito.se2.g04.thesismanagement.teacher.TeacherDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProposalOnRequestFullDTO {
    private Long id;
    private StudentDTO student;
    private String title;
    private String description;
    private TeacherDTO supervisor;
    private List<TeacherDTO> coSupervisors;
    private Date approvalDate;
    private ProposalOnRequest.Status status;
}

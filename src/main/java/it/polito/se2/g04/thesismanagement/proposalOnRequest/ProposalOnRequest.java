package it.polito.se2.g04.thesismanagement.proposalOnRequest;

import it.polito.se2.g04.thesismanagement.student.Student;
import it.polito.se2.g04.thesismanagement.student.StudentDTO;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import it.polito.se2.g04.thesismanagement.teacher.TeacherDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ProposalOnRequest {

    public enum Status {
        PENDING,
        SECRETARY_ACCEPTED,
        SECRETARY_REJECTED,
        TEACHER_ACCEPTED,
        TEACHER_REJECTED,
        TEACHER_REVIEW,
        STUDENT_REVIEWED
    }

    public ProposalOnRequest(String title, String description, Teacher supervisor, Student student, List<Teacher> coSupervisors, Date approvalDate){
        this.title=title;
        this.description=description;
        this.supervisor=supervisor;
        this.student=student;
        this.coSupervisors=coSupervisors;
        this.approvalDate=approvalDate;
        this.status=Status.PENDING;
    }

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String description;
    @ManyToOne
    private Teacher supervisor;
    @ManyToOne
    private Student student;
    @ManyToMany
    private List<Teacher> coSupervisors;

    private Date approvalDate;
    @Enumerated(EnumType.STRING)
    private Status status;



    public ProposalOnRequestDTO toDTO() {
        return new ProposalOnRequestDTO(this.id,
                this.student.getId(),
                this.title,
                this.description,
                this.supervisor.getId(),
                this.coSupervisors.stream().map(Teacher::getId).toList(),
                this.approvalDate,
                this.status);
    }
    public ProposalOnRequestFullDTO toFullDTO() {
        return new ProposalOnRequestFullDTO(this.id,
                StudentDTO.fromStudent(this.student),
                this.title,
                this.description,
                TeacherDTO.fromTeacher(this.supervisor),
                this.coSupervisors.stream().map(it-> TeacherDTO.fromTeacher(it)).toList(),
                this.approvalDate,
                this.status);
    }


}

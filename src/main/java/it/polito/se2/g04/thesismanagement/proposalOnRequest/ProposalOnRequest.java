package it.polito.se2.g04.thesismanagement.proposalOnRequest;

import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import it.polito.se2.g04.thesismanagement.student.Student;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ProposalOnRequest {
    public ProposalOnRequest(String title, String description, Teacher supervisor, Student student, List<Teacher> coSupervisors, Date approvalDate, Status status) {
        this.title = title;
        this.description = description;
        this.supervisor = supervisor;
        this.student = student;
        this.coSupervisors = coSupervisors;
        this.approvalDate = approvalDate;
        this.status = status;
    }

    public enum Status{
        PENDING,
        SECRETARY_ACCEPTED,
        SECRETARY_REJECTED,
        TEACHER_ACCEPTED,
        TEACHER_REJECTED,
        TEACHER_REVIEW,
        STUDENT_REVIEWED
    }
    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String description;
    @ManyToOne
    private Teacher supervisor;
    @OneToOne
    private Student student;
   @ManyToMany
    private List<Teacher> coSupervisors;

   private Date approvalDate;
   @Enumerated(EnumType.STRING)
   private Status status;



   public ProposalOnRequestDTO toDTO(){
       return new ProposalOnRequestDTO(
               this.id,
               this.student.getId(),
               this.title,
               this.description,
               this.supervisor.getId(),
               this.coSupervisors.stream().map(it->it.getId()).toList(),
               this.approvalDate,
               this.status);
   }
}

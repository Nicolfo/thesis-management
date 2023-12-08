package it.polito.se2.g04.thesismanagement.proposalOnRequest;

import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class ProposalOnRequest {
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
   @ManyToMany
    private List<Teacher> coSupervisors;
   private Date approvalDate;
   @Enumerated(EnumType.STRING)
   private Status status;
   public ProposalOnRequestDTO toDTO(){
       return new ProposalOnRequestDTO(this.id,
               this.title,
               this.description,
               this.supervisor.getId(),
               this.coSupervisors.stream().map(it->it.getId()).toList(),
               this.approvalDate,
               this.status);
   }
}

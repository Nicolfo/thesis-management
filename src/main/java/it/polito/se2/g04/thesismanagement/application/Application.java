package it.polito.se2.g04.thesismanagement.application;

import it.polito.se2.g04.thesismanagement.attachment.Attachment;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.student.Student;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Application {
    //Sets accepted automatically to false on entity creation
    public Application(Student student, Attachment attachment, Date applyDate, Proposal proposal) {
        this.student = student;
        this.attachment = attachment;
        this.applyDate = applyDate;
        this.proposal = proposal;
    }

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Student student;
    @OneToOne
    private Attachment attachment;
    private Date applyDate;
    @ManyToOne
    private Proposal proposal;
    private String status= "PENDING"; //status of the application (PENDING/ACCEPTED/REJECTED)
}

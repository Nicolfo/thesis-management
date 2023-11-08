package it.polito.se2.g04.thesismanagement.application;

import it.polito.se2.g04.thesismanagement.attachment.Attachment;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.student.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Application {
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

    public Application(Student student, Attachment attachment, Date applyDate, Proposal proposal) {
        this.attachment = attachment;
        this.applyDate = applyDate;
        this.proposal = proposal;
    }
//Add more if u wish
}

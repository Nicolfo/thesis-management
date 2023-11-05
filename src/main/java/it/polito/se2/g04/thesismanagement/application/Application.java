package it.polito.se2.g04.thesismanagement.application;

import it.polito.se2.g04.thesismanagement.attachment.Attachment;
import it.polito.se2.g04.thesismanagement.student.Student;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Date;

@Entity
@Getter
public class Application {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Student student;
    @OneToOne
    private Attachment attachment;
    private Date applyDate;

    //Add more if u wish
}

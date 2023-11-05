package it.polito.se2.g04.thesismanagement.application;

import it.polito.se2.g04.thesismanagement.attachment.Attachment;
import it.polito.se2.g04.thesismanagement.student.Student;
import jakarta.persistence.*;
import lombok.Getter;

import java.sql.Timestamp;

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
    private Timestamp applyDate;

    //Add more if u wish
}

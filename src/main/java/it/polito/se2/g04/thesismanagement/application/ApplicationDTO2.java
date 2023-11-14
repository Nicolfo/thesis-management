package it.polito.se2.g04.thesismanagement.application;

import it.polito.se2.g04.thesismanagement.attachment.Attachment;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.student.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Getter
public class ApplicationDTO2 {

    private Long id;

    private Long studentId;
    private String studentName;
    private String studentSurname;
    private double studentAverageGrades;

    private Long attachmentId;
    private Date applyDate;
    private Long proposalId;
    private String proposalTitle;

    private String status;



//Add more if u wish
}
package it.polito.se2.g04.thesismanagement.application;

import it.polito.se2.g04.thesismanagement.attachment.Attachment;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.student.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;
@Getter
@AllArgsConstructor
public class ApplicationDTO4 {
    private Long id;
    private Student student;
    private Long attachmentId;
    private Date applyDate;
    private Proposal proposal;
    private BigDecimal studentAverageGrades;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;

}

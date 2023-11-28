package it.polito.se2.g04.thesismanagement.application;

import it.polito.se2.g04.thesismanagement.proposal.ProposalFullDTO;
import it.polito.se2.g04.thesismanagement.student.StudentDTO;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApplicationDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentSurname;
    private BigDecimal studentAverageGrades;
    private Long attachmentId;
    private Long proposalId;
    private String proposalTitle;
    private Date applyDate;
    private String supervisorName;
    private String supervisorSurname;
    private StudentDTO student;
    private ProposalFullDTO proposal;
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;
}

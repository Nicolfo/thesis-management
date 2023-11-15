package it.polito.se2.g04.thesismanagement.application;

import it.polito.se2.g04.thesismanagement.attachment.Attachment;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.student.Student;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
@AllArgsConstructor
@Getter
public class ApplicationDTO {
    private Long attachmentID;
    private Date applyDate;
    private Long proposalID;
}

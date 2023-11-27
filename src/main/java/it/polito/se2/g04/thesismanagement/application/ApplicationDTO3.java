package it.polito.se2.g04.thesismanagement.application;

import it.polito.se2.g04.thesismanagement.attachment.Attachment;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.student.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class ApplicationDTO3 {

    private Long id;

    private Long proposalId;
    private String proposalTitle;

    private String supervisorName;
    private String supervisorSurname;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
}
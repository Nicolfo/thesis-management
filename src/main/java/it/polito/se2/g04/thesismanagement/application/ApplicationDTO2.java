package it.polito.se2.g04.thesismanagement.application;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

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

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;



//Add more if u wish
}
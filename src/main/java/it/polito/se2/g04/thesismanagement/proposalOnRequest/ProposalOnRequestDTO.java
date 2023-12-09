package it.polito.se2.g04.thesismanagement.proposalOnRequest;

import it.polito.se2.g04.thesismanagement.student.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ProposalOnRequestDTO{
    private Long id;
    private Student student;
    private String title;
    private String description;


    private Long supervisor;

    private List<Long> coSupervisors;
    private Date approvalDate;
    private ProposalOnRequest.Status status;
}
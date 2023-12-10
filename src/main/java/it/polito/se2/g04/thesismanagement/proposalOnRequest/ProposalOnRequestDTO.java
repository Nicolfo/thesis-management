package it.polito.se2.g04.thesismanagement.proposalOnRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProposalOnRequestDTO{
    private Long id;
    private Long studentId;
    private String title;
    private String description;
    private Long supervisor;
    private List<Long> coSupervisors;
    private Date approvalDate;
    private ProposalOnRequest.Status status;
}
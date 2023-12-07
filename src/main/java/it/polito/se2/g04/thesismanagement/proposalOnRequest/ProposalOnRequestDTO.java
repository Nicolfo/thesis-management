package it.polito.se2.g04.thesismanagement.proposalOnRequest;

import lombok.AllArgsConstructor;

import java.util.Date;
import java.util.List;
@AllArgsConstructor
public class ProposalOnRequestDTO{
    private Long id;
    private String title;
    private String description;

    private Long supervisor;

    private List<Long> coSupervisors;
    private Date approvalDate;
    private ProposalOnRequest.Status status;
}
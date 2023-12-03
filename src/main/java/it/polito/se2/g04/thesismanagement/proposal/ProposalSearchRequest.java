package it.polito.se2.g04.thesismanagement.proposal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProposalSearchRequest {
    private String title;
    private List<Long> supervisorIdList;
    private List<Long> coSupervisorIdList;
    private String keywords;
    private String type;
    private List<Long> codGroupList;
    private String description;
    private String requiredKnowledge;
    private String notes;
    private Date minExpiration;
    private Date maxExpiration;
    private String level;//to check
    private String cds;//to check
}
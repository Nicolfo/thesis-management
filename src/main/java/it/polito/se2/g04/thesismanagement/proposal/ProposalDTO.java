package it.polito.se2.g04.thesismanagement.proposal;

import it.polito.se2.g04.thesismanagement.group.Group;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.List;
@AllArgsConstructor
@Getter
public class ProposalDTO {
    private String title;
    private Long supervisorId;
    private List<Long> coSupervisors;
    private String description;
    private String requiredKnowledge;
    private String notes;
    private Date expiration;
    private String level;//to check
    private String CdS;//to check
    private String keywords;
    private String type;

}

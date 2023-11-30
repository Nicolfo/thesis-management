package it.polito.se2.g04.thesismanagement.proposal;

import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ProposalDTO {
    private String title;
    private Long supervisorId;
    private List<Long> coSupervisors;
    private String description;
    private String requiredKnowledge;
    private String notes;
    private Date expiration;
    private String level;//to check
    private String cds;//to check
    private String keywords;
    private String type;

    public static ProposalDTO fromProposal(Proposal proposal) {
        if (proposal == null)
            return null;
        return new ProposalDTO(
                proposal.getTitle(),
                proposal.getSupervisor() != null ? proposal.getSupervisor().getId() : null,
                proposal.getCoSupervisors() != null ? proposal.getCoSupervisors().stream().map(Teacher::getId).toList() : null,
                proposal.getDescription(),
                proposal.getRequiredKnowledge(),
                proposal.getNotes(),
                proposal.getExpiration(),
                proposal.getLevel(),
                proposal.getCdS(),
                proposal.getKeywords(),
                proposal.getType()
        );
    }

}

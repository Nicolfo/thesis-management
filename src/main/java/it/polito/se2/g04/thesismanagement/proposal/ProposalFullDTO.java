package it.polito.se2.g04.thesismanagement.proposal;

import it.polito.se2.g04.thesismanagement.group.GroupDTO;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import it.polito.se2.g04.thesismanagement.teacher.TeacherDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.parameters.P;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProposalFullDTO {
    private Long id;
    private String title;
    private TeacherDTO supervisor;
    private List<TeacherDTO> coSupervisors;
    private List<GroupDTO> groups;
    private String description;
    private String requiredKnowledge;
    private String notes;
    private Date expiration;
    private String level;//to check
    private String CdS;//to check
    private String keywords;
    private String type;

    public static ProposalFullDTO fromProposal(Proposal proposal) {
        if (proposal == null)
            return null;
        return new ProposalFullDTO(
                proposal.getId(),
                proposal.getTitle(),
                TeacherDTO.fromTeacher(proposal.getSupervisor()),
                proposal.getCoSupervisors() != null ? proposal.getCoSupervisors().stream().map(TeacherDTO::fromTeacher).toList() : new ArrayList<>(),
                proposal.getGroups() != null ? proposal.getGroups().stream().map(GroupDTO::fromGroup).toList() : new ArrayList<>(),
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

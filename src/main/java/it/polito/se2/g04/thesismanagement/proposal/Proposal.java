package it.polito.se2.g04.thesismanagement.proposal;

import it.polito.se2.g04.thesismanagement.group.Group;
import it.polito.se2.g04.thesismanagement.proposal_on_request.ProposalOnRequest;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Proposal {

    public boolean getNotifiedAboutExpiration() {
        return notifiedAboutExpiration;
    }

    public enum Status{
        ACTIVE,
        DELETED,
        ARCHIVED,
        ACCEPTED
    }

    //sets archived automatically to false, when object is created using this constructor
    public Proposal(String title, Teacher supervisor, List<Teacher> coSupervisors, String keywords, String type, List<Group> groups, String description, String requiredKnowledge, String notes, Date expiration, String level, String cds) {
        this.title = title;
        this.supervisor = supervisor;
        this.coSupervisors = coSupervisors;
        this.keywords = keywords;
        this.type = type;
        this.groups = groups;
        this.description = description;
        this.requiredKnowledge = requiredKnowledge;
        this.notes = notes;
        this.expiration = expiration;
        this.level = level;
        this.cds = cds;
    }

    @Id
    @GeneratedValue
    private Long id;
    private String title;
    @ManyToOne
    private Teacher supervisor;
    @ManyToMany
    private List<Teacher> coSupervisors;
    private String keywords;
    private String type;
    @ManyToMany
    private List<Group> groups;
    @Column(columnDefinition = "varchar(5000)")
    private String description;
    private String requiredKnowledge;
    private String notes;
    private Date expiration;
    private String level;//to check
    private String cds;//to check
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;
    private boolean notifiedAboutExpiration = false;
}

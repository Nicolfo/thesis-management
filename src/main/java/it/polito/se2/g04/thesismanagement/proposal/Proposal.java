package it.polito.se2.g04.thesismanagement.proposal;

import it.polito.se2.g04.thesismanagement.group.Group;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Proposal {
    public enum Status{
        ACTIVE,
        DELETED,
        ARCHIVED,
        ACCEPTED
    }

    //sets archived automatically to false, when object is created using this constructor
    public Proposal(String title, Teacher supervisor, List<Teacher> coSupervisors, String keywords, String type, List<Group> groups, String description, String requiredKnowledge, String notes, Date expiration, String level, String cdS) {
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
        CdS = cdS;
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
    private String CdS;//to check
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proposal proposal = (Proposal) o;
        return Objects.equals(id, proposal.id) && Objects.equals(title, proposal.title) && Objects.equals(supervisor, proposal.supervisor) && Objects.equals(coSupervisors, proposal.coSupervisors) && Objects.equals(keywords, proposal.keywords) && Objects.equals(type, proposal.type) && Objects.equals(groups, proposal.groups) && Objects.equals(description, proposal.description) && Objects.equals(requiredKnowledge, proposal.requiredKnowledge) && Objects.equals(notes, proposal.notes) && Objects.equals(expiration, proposal.expiration) && Objects.equals(level, proposal.level) && Objects.equals(CdS, proposal.CdS);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, supervisor, coSupervisors, keywords, type, groups, description, requiredKnowledge, notes, expiration, level, CdS);
    }
}

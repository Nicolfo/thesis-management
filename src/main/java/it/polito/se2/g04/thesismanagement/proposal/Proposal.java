package it.polito.se2.g04.thesismanagement.proposal;

import it.polito.se2.g04.thesismanagement.group.Group;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

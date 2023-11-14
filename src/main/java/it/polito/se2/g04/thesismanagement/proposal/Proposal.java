package it.polito.se2.g04.thesismanagement.proposal;

import it.polito.se2.g04.thesismanagement.group.Group;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Proposal {

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
        this.CdS = cdS;
    }


    public boolean Update(Proposal proposal){
        Class<?> clazz=this.getClass();
        boolean result=false;

        Field[] fields=clazz.getDeclaredFields();
        for (Field field: fields){
            field.setAccessible(true);
            try{
                Object thisVariable=field.get(this);
                Object otherVariable=field.get(proposal);
                if(!Objects.equals(thisVariable,otherVariable)){
                    field.set(this,otherVariable);
                    result=true;
                }
            }catch(IllegalAccessException e){
                e.printStackTrace();
            }
        }
        return result;
    }


    @Id
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
    private String description;
    private String requiredKnowledge;
    private String notes;
    private Date expiration;
    private String level;//to check
    private String CdS;//to check
}

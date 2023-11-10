package it.polito.se2.g04.thesismanagement.group;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "group_table")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codGroup;
    private String name;

    public Group(String name){
        this.name=name;
    }
}

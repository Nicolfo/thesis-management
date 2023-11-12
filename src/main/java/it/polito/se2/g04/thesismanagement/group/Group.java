package it.polito.se2.g04.thesismanagement.group;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "group_table")
public class Group {
    @Id
    @GeneratedValue
    private Long codGroup;
    private String name;
}

package it.polito.se2.g04.thesismanagement.degree;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Degree {
    @Id
    private String codDegree;
    private String titleDegree;
}

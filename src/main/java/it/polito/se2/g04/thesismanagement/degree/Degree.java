package it.polito.se2.g04.thesismanagement.degree;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Degree {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codDegree;
    private String titleDegree;

    public Degree(String titleDegree) {
        this.titleDegree = titleDegree;
    }
}

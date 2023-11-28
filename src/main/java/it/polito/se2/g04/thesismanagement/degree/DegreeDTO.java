package it.polito.se2.g04.thesismanagement.degree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DegreeDTO {
    private Long codDegree;
    private String titleDegree;

    public static DegreeDTO fromDegree(Degree degree) {
        if (degree == null)
            return null;
        return new DegreeDTO(degree.getCodDegree(), degree.getTitleDegree());
    }
}

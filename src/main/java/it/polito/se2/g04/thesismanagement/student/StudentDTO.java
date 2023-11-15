package it.polito.se2.g04.thesismanagement.student;

import lombok.Getter;

@Getter
public class StudentDTO {
    private String fullName;

    public StudentDTO(String fullName) {
        this.fullName = fullName;
    }
}

package it.polito.se2.g04.thesismanagement.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JwtDTO {
    private String token;
    private String email;
}

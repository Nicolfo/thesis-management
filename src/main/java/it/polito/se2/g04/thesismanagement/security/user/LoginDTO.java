package it.polito.se2.g04.thesismanagement.security.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginDTO {
    private String username;
    private String password;
}

package it.polito.se2.g04.thesismanagement.security.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RegisterDTO {
    private String username;
    private String password;
    private String role;
}

package it.polito.se2.g04.thesismanagement.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class SecurityController {
    @GetMapping("/API/testLogin")
    @PreAuthorize("hasRole('TEACHER')")
    public String testLogin(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}

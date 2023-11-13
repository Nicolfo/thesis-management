package it.polito.se2.g04.thesismanagement.security.user;

import it.polito.se2.g04.thesismanagement.security.jwt.JwtDTO;
import it.polito.se2.g04.thesismanagement.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    @PostMapping("/API/register")
    public String addNewUser(@RequestBody RegisterDTO userInfo) {
        return userService.addUser(userInfo);
    }

    @PostMapping("/API/login")
    public JwtDTO authenticateAndGetToken(@RequestBody LoginDTO authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {

            return new JwtDTO(jwtService.generateToken(authRequest.getUsername()),authRequest.getUsername(), authentication.getAuthorities().stream().findFirst().get().toString());
        } else {
            System.out.println("User not found");
            throw new UsernameNotFoundException("invalid user request !");
        }
    }

    @GetMapping("/API/testAuth")
    @ResponseStatus(HttpStatus.OK)
    public String tryAuth(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "You are logged in as student : "+auth.getName()+" and role "+auth.getAuthorities();
    }
}

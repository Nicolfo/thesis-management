package it.polito.se2.g04.thesismanagement.security.user;

import it.polito.se2.g04.thesismanagement.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

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
    public String authenticateAndGetToken(@RequestBody LoginDTO authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authRequest.getUsername());
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }
    }

    @GetMapping("/API/tryauth")
    public String tryAuth(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(""+auth.getAuthorities());
        return "You are logged in as student : "+auth.getName()+" and role "+auth.getAuthorities();
    }
}

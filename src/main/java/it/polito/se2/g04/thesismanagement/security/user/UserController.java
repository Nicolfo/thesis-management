package it.polito.se2.g04.thesismanagement.security.user;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/API/login")
    public String login(@RequestBody LoginDTO loginDTO){
        try {
            return userService.login(loginDTO.getUsername(),loginDTO.getPassword());
        } catch (Exception e) {
            throw new RuntimeException(e); //change exc type
        }
    }

    @PostMapping("/API/register")
    public String register(@RequestBody RegisterDTO registerDTO){
         if(userService.register(registerDTO.getUsername(),registerDTO.getPassword(),registerDTO.getRole())!=null){
             return "User registered correctly";
         }
         return "Error in register";
    }
}

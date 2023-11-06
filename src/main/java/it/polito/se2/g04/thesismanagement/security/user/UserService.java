package it.polito.se2.g04.thesismanagement.security.user;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;

public interface UserService {
    public String login(String username, String password) throws Exception;
    public User register(String username, String password,String role);
}

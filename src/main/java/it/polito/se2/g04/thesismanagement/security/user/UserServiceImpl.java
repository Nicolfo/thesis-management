package it.polito.se2.g04.thesismanagement.security.user;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String login(String username, String password) throws Exception{
        User user = userRepository.findUserByUsername(username).orElseThrow();
        if(passwordEncoder.matches(password,user.getPassword()))
            return generateJWT(username,"thesis-app",1000,"Kfu98xHShL@#Gd0AeWc^T2p$m5Xp1YzQrL8nJt1rBfAeDv8gNtWk7zEjM");
        else throw new Exception();
    }

    @Override
    public User register(String username, String password, String role) {
        if(userRepository.findUserByUsername(username).isPresent()){
            return null;
        }
        else{
            User toAdd = new User(username,passwordEncoder.encode(password),role);
            userRepository.save(toAdd);
            return toAdd;
        }
}



    private static String generateJWT(String subject, String issuer, long expirationTimeInSeconds, String secretKey) throws JOSEException {
        // Define JWT claims
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(subject)          // Subject (typically a user identifier)
                .issuer(issuer)            // Issuer of the token
                .issueTime(new Date())    // Issued at time
                .expirationTime(new Date(System.currentTimeMillis() + expirationTimeInSeconds * 1000)) // Expiration time
                .build();

        // Create JWS header with HMAC-SHA256 algorithm
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
                .contentType("JWT")
                .build();

        // Create a signed JWT
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);

        // Sign the JWT with the secret key
        MACSigner signer = new MACSigner(secretKey);
        signedJWT.sign(signer);

        // Serialize the JWT to a string
        return signedJWT.serialize();
    }
}

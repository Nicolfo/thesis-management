package it.polito.se2.g04.thesismanagement.security.user;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import it.polito.se2.g04.thesismanagement.degree.DegreeRepository;
import it.polito.se2.g04.thesismanagement.department.DepartmentRepository;
import it.polito.se2.g04.thesismanagement.group.GroupRepository;
import it.polito.se2.g04.thesismanagement.student.Student;
import it.polito.se2.g04.thesismanagement.student.StudentRepository;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import it.polito.se2.g04.thesismanagement.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final DegreeRepository degreeRepository;
    private final DepartmentRepository departmentRepository;
    private final GroupRepository groupRepository;







    public String addUser(RegisterDTO userInfo) {
        if(userRepository.findUserByUsername(userInfo.getEmail()).isPresent()){
            return "user not added";
        }
        else{
            if(userInfo.getRole().compareTo("STUDENT")==0){
                Student toAdd= new Student(userInfo.getSurname(),userInfo.getName(),userInfo.getGender(),userInfo.getNationality(),userInfo.getEmail(),degreeRepository.getReferenceById(userInfo.getCodDegree()),userInfo.getEnrollmentYear());
                studentRepository.save(toAdd);
            }
            if(userInfo.getRole().compareTo("TEACHER")==0){
                Teacher toAdd = new Teacher(userInfo.getSurname(),userInfo.getName(),userInfo.getEmail(),groupRepository.getReferenceById(userInfo.getCodGroup()),departmentRepository.getReferenceById(userInfo.getCodDepartment()));
                teacherRepository.save(toAdd);
            }
            User toAdd = new User(userInfo.getEmail(),passwordEncoder.encode(userInfo.getPassword()),userInfo.getRole());
            userRepository.save(toAdd);
            return "user added";
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

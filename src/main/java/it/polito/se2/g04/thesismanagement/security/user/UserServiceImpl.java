package it.polito.se2.g04.thesismanagement.security.user;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import it.polito.se2.g04.thesismanagement.degree.DegreeException;
import it.polito.se2.g04.thesismanagement.degree.DegreeNotFoundException;
import it.polito.se2.g04.thesismanagement.degree.DegreeRepository;
import it.polito.se2.g04.thesismanagement.department.DepartmentNotFoundException;
import it.polito.se2.g04.thesismanagement.department.DepartmentRepository;
import it.polito.se2.g04.thesismanagement.group.GroupNotFoundException;
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
                //maybe add a check to every field
                if(userInfo.getCodDegree()==null)
                    throw new DegreeNotFoundException("Degree field is null");
                if(!degreeRepository.existsById(userInfo.getCodDegree()))
                    throw new DegreeNotFoundException("Can't find a degree with the specified ID");
                Student toAdd= new Student(userInfo.getSurname(),userInfo.getName(),userInfo.getGender(),userInfo.getNationality(),userInfo.getEmail(),degreeRepository.getReferenceById(userInfo.getCodDegree()),userInfo.getEnrollmentYear());
                studentRepository.save(toAdd);
                User userToAdd = new User(userInfo.getEmail(),passwordEncoder.encode(userInfo.getPassword()),userInfo.getRole());
                userRepository.save(userToAdd);
                return "user added";
            }
            if(userInfo.getRole().compareTo("TEACHER")==0){
                //maybe add a check to every field
                if(userInfo.getCodGroup()==null)
                    throw new GroupNotFoundException("Group field is null");
                if(!groupRepository.existsById(userInfo.getCodGroup()))
                    throw new GroupNotFoundException("Can't find a group with the specified ID");
                if(userInfo.getCodDepartment()==null)
                    throw new DepartmentNotFoundException("Department field is null");
                if(!departmentRepository.existsById(userInfo.getCodDepartment()))
                    throw new DepartmentNotFoundException("Can't find a department with the specified ID");
                Teacher toAdd = new Teacher(userInfo.getSurname(),userInfo.getName(),userInfo.getEmail(),groupRepository.getReferenceById(userInfo.getCodGroup()),departmentRepository.getReferenceById(userInfo.getCodDepartment()));
                teacherRepository.save(toAdd);
                User userToAdd = new User(userInfo.getEmail(),passwordEncoder.encode(userInfo.getPassword()),userInfo.getRole());
                userRepository.save(userToAdd);
                return "user added";
            }
            return "user not added";
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
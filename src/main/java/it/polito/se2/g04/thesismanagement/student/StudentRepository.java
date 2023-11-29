package it.polito.se2.g04.thesismanagement.student;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student,Long> {
    public Boolean existsByEmail(String email);
    public Student getStudentByEmail(String email);
}

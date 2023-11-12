package it.polito.se2.g04.thesismanagement.teacher;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher,Long> {
    Teacher findByEmail(String email);

    public List<Teacher> getAll();
}

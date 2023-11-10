package it.polito.se2.g04.thesismanagement.teacher;

import it.polito.se2.g04.thesismanagement.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher,Long> {
}

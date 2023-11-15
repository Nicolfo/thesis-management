package it.polito.se2.g04.thesismanagement.career;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareerRepository extends JpaRepository<Career,Long> {
    public List<Career> getAllByStudentId(Long studentId);
    public List<Career> getCareersByStudent_Id(Long id);
}

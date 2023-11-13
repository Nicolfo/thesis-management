package it.polito.se2.g04.thesismanagement.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


public interface class StudentService {

    public String getStudentFullName(Long studentId);

    public double getAverageMarks(Long studentId);

}

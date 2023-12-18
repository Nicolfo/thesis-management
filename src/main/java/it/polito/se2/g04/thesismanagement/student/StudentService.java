package it.polito.se2.g04.thesismanagement.student;



public interface StudentService {

    public String getStudentFullName(Long studentId);

    public double getAverageMarks(Long studentId);

    public String getCdS(String email);


}

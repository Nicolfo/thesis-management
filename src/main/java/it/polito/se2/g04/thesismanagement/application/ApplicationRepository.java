package it.polito.se2.g04.thesismanagement.application;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application,Long> {
    public List<Application> getApplicationByProposal_Supervisor_Email(String email);
    public List<Application> getApplicationByProposal_Id(Long proposalId);
    public Application getApplicationById(Long id);
    public List<Application> getApplicationByStudentEmail(String email);

}

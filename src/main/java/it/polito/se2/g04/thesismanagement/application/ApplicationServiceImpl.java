package it.polito.se2.g04.thesismanagement.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService{
    private final ApplicationRepository applicationRepository;

    @Override
    public List<Application> getApplicationsByProf(String profEmail) {
        return applicationRepository.getApplicationByProposal_Supervisor_Email(profEmail);
    }


    //implement method here
}

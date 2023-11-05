package it.polito.se2.g04.thesismanagement.application;

import org.springframework.stereotype.Service;

@Service
public class ApplicationServiceImpl implements ApplicationService{
    private final ApplicationRepository applicationRepository;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    //implement method here
}

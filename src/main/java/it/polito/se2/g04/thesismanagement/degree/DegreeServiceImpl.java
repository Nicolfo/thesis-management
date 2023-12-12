package it.polito.se2.g04.thesismanagement.degree;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DegreeServiceImpl implements DegreeService{


    private final DegreeRepository degreeRepository;



    @Override
    public List<String> getAllName(){
        return degreeRepository.findAll().stream().map(it->it.getTitleDegree()).toList();
    }
}

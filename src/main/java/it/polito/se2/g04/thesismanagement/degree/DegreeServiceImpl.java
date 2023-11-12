package it.polito.se2.g04.thesismanagement.degree;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DegreeServiceImpl implements DegreeService{

    @Autowired
    private final DegreeRepository degreeRepository;

    public DegreeServiceImpl(DegreeRepository degreeRepository){
        this.degreeRepository=degreeRepository;
    }

    @Override
    public List<String> getAllName(){
        List<String> names=new ArrayList<>();
        for (Degree d: degreeRepository.findAll()){
            names.add(d.getTitleDegree());
        }
        return names;
    }
}

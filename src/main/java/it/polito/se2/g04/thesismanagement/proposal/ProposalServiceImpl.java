package it.polito.se2.g04.thesismanagement.proposal;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProposalServiceImpl implements ProposalService{

    @Autowired
    private final ProposalRepository proposalRepository;

    public ProposalServiceImpl(ProposalRepository proposalRepository){
        this.proposalRepository=proposalRepository;
    }

    @Override
    public Proposal createProposal(String jsonProposal){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Proposal newProposal = objectMapper.readValue(jsonProposal, Proposal.class);
            proposalRepository.save(newProposal);
            return newProposal;
        }catch (Exception e) {
            throw new JsonStringCantDeserialize(e.getMessage());
        }
    }
}

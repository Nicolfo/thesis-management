package it.polito.se2.g04.thesismanagement.proposal;

import org.springframework.beans.factory.annotation.Autowired;
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
    public void createProposal(String jsonProposal){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Proposal newProposal = objectMapper.readValue(jsonProposal, Proposal.class);
            proposalRepository.save(newProposal);
        }catch (Exception e) {
            // gestire l'errore
            return;
        }
    }
}
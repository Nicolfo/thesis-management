package it.polito.se2.g04.thesismanagement.proposal;


import com.fasterxml.jackson.core.JsonProcessingException;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import it.polito.se2.g04.thesismanagement.teacher.TeacherRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.stream.Stream;

@Service
public class ProposalServiceImpl implements ProposalService{


    private final ProposalRepository proposalRepository;
    private final TeacherRepository teacherRepository;

    public ProposalServiceImpl(ProposalRepository proposalRepository, TeacherRepository teacherRepository){
        this.proposalRepository=proposalRepository;
        this.teacherRepository=teacherRepository;
    }

    @Override
    public Proposal createProposal(String jsonProposal){
            /*
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonProposal);
            Proposal proposal=new Proposal();
            Teacher supervisor=teacherRepository.findById(jsonNode.get("supervisor").asLong()).orElseThrow();
            List<Teacher> coSupervisor=StreamSupport.stream(jsonNode.get("coSupervisors").spliterator(),false).map(JsonNode::asLong).toList().stream().map(it-> teacherRepository.findById(it).orElseThrow()).toList();
            proposal.setTitle(jsonNode.get("title").asText());
            proposal.setSupervisor(supervisor);
            proposal.setCoSupervisors(coSupervisor);
            proposal.setKeywords(jsonNode.get("keywords").asText());
            proposal.setType(jsonNode.get("type").asText());
            proposal.setGroups(Stream.concat(Stream.of(supervisor.getGroup()),coSupervisor.stream().map(Teacher::getGroup)).toList());
            proposal.setDescription(jsonNode.get("description").asText());
            proposal.setRequiredKnowledge(jsonNode.get("requiredKnowledge").asText());
            proposal.setNotes(jsonNode.get("notes").asText());
            proposal.setExpiration(new SimpleDateFormat("dd-MM-yyyy").parse(jsonNode.get("expiration").asText()));
            proposal.setLevel(jsonNode.get("level").asText());
            proposal.setCdS(jsonNode.get("CdS").asText());*/

        Proposal proposal=deserializateJsonProposal(jsonProposal);
        proposalRepository.save(proposal);
        return proposal;
    }

    @Override
    public Proposal updateProposal(Long id,String jsonProposal){
        Proposal proposal=proposalRepository.findById(id).orElseThrow();
        Proposal modifiedProposal = deserializateJsonProposal(jsonProposal);
        if (proposal.Update(modifiedProposal))
            proposalRepository.save(proposal);
        return proposal;
    }

    private Proposal deserializateJsonProposal(String jsonProposal){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Proposal proposal = objectMapper.readValue(jsonProposal, Proposal.class);
            proposal.setSupervisor(teacherRepository.findById(proposal.getSupervisor().getId()).orElseThrow());
            proposal.setCoSupervisors(proposal.getCoSupervisors().stream().map(it -> teacherRepository.findById(it.getId()).orElseThrow()).toList());
            proposal.setGroups(Stream.concat(Stream.of(proposal.getSupervisor().getGroup()), proposal.getCoSupervisors().stream().map(Teacher::getGroup)).toList());
            return proposal;
        }catch (Exception e){
            throw new JsonStringCantDeserialize(e.getMessage());
        }
    }
}

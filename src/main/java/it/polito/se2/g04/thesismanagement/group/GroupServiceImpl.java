package it.polito.se2.g04.thesismanagement.group;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    public GroupServiceImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    @Query
    public List<GroupDTO> getAllGroups() {
        return groupRepository.getAll().stream().map(g -> new GroupDTO(g.getCodGroup(), g.getName())).toList();
    }
}

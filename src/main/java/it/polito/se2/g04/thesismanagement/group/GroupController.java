package it.polito.se2.g04.thesismanagement.group;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/API/group/getAll")
    @ResponseStatus(HttpStatus.OK)
    public List<GroupDTO> getAll() {
        return groupService.getAllGroups();
    }
}

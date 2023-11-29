package it.polito.se2.g04.thesismanagement.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class GroupDTO {

    private Long codGroup;

    private String name;

    public static GroupDTO fromGroup(Group group) {
        if (group == null)
            return null;
        return new GroupDTO(group.getCodGroup(), group.getName());
    }

}

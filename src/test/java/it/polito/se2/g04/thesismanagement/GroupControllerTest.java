package it.polito.se2.g04.thesismanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.polito.se2.g04.thesismanagement.group.Group;
import it.polito.se2.g04.thesismanagement.group.GroupDTO;
import it.polito.se2.g04.thesismanagement.group.GroupRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GroupControllerTest {

    private Group g1;
    private Group g2;
    private Group g3;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public void setup() {
        g1 = new Group("Group 1");
        g1 = groupRepository.save(g1);
        g2 = new Group("Group 2");
        g2 = groupRepository.save(g2);
        g3 = new Group("Group 3");
        g3 = groupRepository.save(g3);
    }

    @AfterAll
    public void CleanUp(){
        groupRepository.deleteAll();
    }

    @Test
    @Rollback
    void getAllGroups() throws Exception {
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/API/group/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn();
        String json = res.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        GroupDTO[] groups = mapper.readValue(json, GroupDTO[].class);

        // Check that size matches
        assertEquals(3, groups.length, "Groups' getAll should return 3 values");

        // Check that titles match
        assertEquals(g1.getName(), groups[0].getName(), "Wrong group name");
        assertEquals(g2.getName(), groups[1].getName(), "Wrong group name");
        assertEquals(g3.getName(), groups[2].getName(), "Wrong group name");

        // Add a group
        Group g4 = new Group("Group 4");
        g4 = groupRepository.save(g4);

        // Perform request again: now I should get 4 groups
        res = mockMvc.perform(MockMvcRequestBuilders.get("/API/group/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = res.getResponse().getContentAsString();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        groups = mapper.readValue(json, GroupDTO[].class);

        // Check that size matches
        assertEquals(4, groups.length, "Groups' getAll should return 4 values after insert");

        // Check that new title matches
        assertEquals(g4.getName(), groups[3].getName(), "Wrong group name");
    }

}

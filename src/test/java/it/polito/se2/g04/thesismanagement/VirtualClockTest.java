package it.polito.se2.g04.thesismanagement;

import it.polito.se2.g04.thesismanagement.virtualclock.VirtualClockController;
import it.polito.se2.g04.thesismanagement.virtualclock.VirtualClockRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.keycloak.util.JsonSerialization.mapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class VirtualClockTest {
    @Autowired
    private MockMvc mockMvc;
    @Test
    @Rollback
    @WithMockUser(username = "m.rossi@example.com", roles = {"STUDENT"})
    public void virtualClockTest() throws Exception{
        VirtualClockRequest virtualClockRequest= new VirtualClockRequest(3);
        mockMvc.perform(MockMvcRequestBuilders.post("/API/virtualTimer/changeOffset/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(virtualClockRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        assertEquals(3,VirtualClockController.getOffset());
    }
}

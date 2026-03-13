package net.leozeballos.FastFood.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void indexIsPublic() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    public void branchListIsProtected() throws Exception {
        mockMvc.perform(get("/branch/list"))
                .andExpect(status().isUnauthorized()); 
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void branchListIsAccessibleWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/branch/list"))
                .andExpect(status().isOk());
    }

    @Test
    public void apiIsProtected() throws Exception {
        mockMvc.perform(get("/api/v1/branches"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void apiIsAccessibleWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/branches"))
                .andExpect(status().isOk());
    }
}

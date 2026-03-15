package net.leozeballos.FastFood.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryRestController.class)
@WithMockUser
class InventoryRestControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private InventoryService inventoryService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void getByBranchReturnsList() throws Exception {
        // given
        Inventory inventory = Inventory.builder().id(1L).stockQuantity(50).build();
        when(inventoryService.findByBranch(1L)).thenReturn(List.of(inventory));

        // when & then
        mockMvc.perform(get("/api/v1/inventory/branch/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].stockQuantity").value(50));
    }

    @Test
    void updateStockReturnsUpdatedInventory() throws Exception {
        // given
        Inventory inventory = Inventory.builder().id(1L).stockQuantity(75).build();
        when(inventoryService.save(any(Inventory.class))).thenReturn(inventory);

        // when & then
        mockMvc.perform(post("/api/v1/inventory/update")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(75));
    }
}

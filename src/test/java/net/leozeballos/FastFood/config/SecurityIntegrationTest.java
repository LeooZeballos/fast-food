package net.leozeballos.FastFood.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import net.leozeballos.FastFood.branch.BranchRestController;
import net.leozeballos.FastFood.branch.BranchService;
import net.leozeballos.FastFood.inventory.InventoryRestController;
import net.leozeballos.FastFood.inventory.InventoryService;
import net.leozeballos.FastFood.mapper.BranchMapper;
import net.leozeballos.FastFood.mapper.MenuMapper;
import net.leozeballos.FastFood.mapper.ProductMapper;
import net.leozeballos.FastFood.menu.MenuRestController;
import net.leozeballos.FastFood.menu.MenuService;
import net.leozeballos.FastFood.product.ProductRestController;
import net.leozeballos.FastFood.product.ProductService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ProductRestController.class, MenuRestController.class, BranchRestController.class, InventoryRestController.class})
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private MenuService menuService;

    @MockitoBean
    private BranchService branchService;

    @MockitoBean
    private InventoryService inventoryService;

    @MockitoBean
    private ProductMapper productMapper;

    @MockitoBean
    private MenuMapper menuMapper;

    @MockitoBean
    private BranchMapper branchMapper;

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

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_asAdmin_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/products/1").with(csrf()))
               .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteProduct_asUser_returns403() throws Exception {
        mockMvc.perform(delete("/api/v1/products/1").with(csrf()))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createProduct_asUser_returns403() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test Product\", \"price\": 10.0}")
                .with(csrf()))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteMenu_asUser_returns403() throws Exception {
        mockMvc.perform(delete("/api/v1/menus/1").with(csrf()))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteBranch_asUser_returns403() throws Exception {
        mockMvc.perform(delete("/api/v1/branches/1").with(csrf()))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void updateInventory_asUser_returns403() throws Exception {
        // User with no branchId in CustomUserDetails (mocked) will have effectiveBranchId = null
        // If we provide branchId=1, and effectiveBranchId is null, it should fail if getEffectiveBranchId returns non-null
        // Wait, WithMockUser uses a simple User principal. InventoryRestController.getEffectiveBranchId expects CustomUserDetails.
        // It might return null if it's not CustomUserDetails.
        
        mockMvc.perform(post("/api/v1/inventory/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"branch\": {\"id\": 1}, \"stockQuantity\": 10}")
                .with(csrf()))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateInventory_asAdmin_returnsOk() throws Exception {
        mockMvc.perform(post("/api/v1/inventory/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"branch\": {\"id\": 1}, \"stockQuantity\": 10}")
                .with(csrf()))
               .andExpect(status().isOk());
    }
}

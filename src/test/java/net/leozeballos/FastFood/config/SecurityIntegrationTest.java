package net.leozeballos.FastFood.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import net.leozeballos.FastFood.branch.BranchRestController;
import net.leozeballos.FastFood.branch.BranchService;
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

import org.springframework.http.MediaType;

@WebMvcTest({ProductRestController.class, MenuRestController.class, BranchRestController.class})
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private MenuService menuService;

    @MockBean
    private BranchService branchService;

    @MockBean
    private ProductMapper productMapper;

    @MockBean
    private MenuMapper menuMapper;

    @MockBean
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
}

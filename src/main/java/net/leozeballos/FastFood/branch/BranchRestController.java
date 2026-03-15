package net.leozeballos.FastFood.branch;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.address.Address;
import net.leozeballos.FastFood.auth.CustomUserDetails;
import net.leozeballos.FastFood.mapper.BranchMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
@Tag(name = "Branches", description = "Management of physical store locations")
public class BranchRestController {

    private final BranchService branchService;
    private final BranchMapper branchMapper;

    @GetMapping("/me")
    @Operation(summary = "Get current user info", description = "Returns information about the currently authenticated user")
    public Map<String, Object> getMe(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken auth) {
            if (auth.getPrincipal() instanceof CustomUserDetails userDetails) {
                return Map.of(
                    "name", userDetails.getUsername(),
                    "branchId", userDetails.getBranchId() != null ? userDetails.getBranchId() : "none",
                    "roles", userDetails.getAuthorities().stream()
                        .map(a -> a.getAuthority().replace("ROLE_", ""))
                        .toList()
                );
            }
        }
        return Map.of("name", principal != null ? principal.getName() : "anonymous", "branchId", "none", "roles", List.of());
    }

    @GetMapping
    @Operation(summary = "Get all branches", description = "Returns a list of all available fast food branches")
    public List<BranchDTO> getAll() {
        return branchService.findAllDTO();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get branch by ID", description = "Returns a single branch based on its unique identifier")
    @ApiResponse(responseCode = "200", description = "Branch found")
    @ApiResponse(responseCode = "404", description = "Branch not found")
    public BranchDTO getOne(@Parameter(description = "ID of the branch to be retrieved") @PathVariable Long id) {
        return branchService.findByIdDTO(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new branch", description = "Registers a new branch in the system")
    @ApiResponse(responseCode = "201", description = "Branch created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public BranchDTO create(@Valid @RequestBody BranchDTO dto) {
        Branch branch = Branch.builder()
                .name(dto.name())
                .address(Address.builder()
                        .city(dto.city())
                        .street(dto.street())
                        .build())
                .build();
        return branchMapper.toDTO(branchService.save(branch));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a branch", description = "Updates the details of an existing branch")
    @ApiResponse(responseCode = "200", description = "Branch updated successfully")
    @ApiResponse(responseCode = "404", description = "Branch not found")
    public BranchDTO update(
            @Parameter(description = "ID of the branch to be updated") @PathVariable Long id,
            @Valid @RequestBody BranchDTO dto) {
        Branch branch = branchService.findById(id);
        branch.setName(dto.name());
        if (branch.getAddress() != null) {
            branch.getAddress().setCity(dto.city());
            branch.getAddress().setStreet(dto.street());
        } else {
            branch.setAddress(Address.builder()
                    .city(dto.city())
                    .street(dto.street())
                    .build());
        }
        return branchMapper.toDTO(branchService.save(branch));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a branch", description = "Removes a branch from the system")
    @ApiResponse(responseCode = "204", description = "Branch deleted successfully")
    @ApiResponse(responseCode = "404", description = "Branch not found")
    public void delete(@Parameter(description = "ID of the branch to be deleted") @PathVariable Long id) {
        branchService.deleteById(id);
    }
}

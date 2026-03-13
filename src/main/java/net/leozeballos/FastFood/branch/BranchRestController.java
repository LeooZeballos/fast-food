package net.leozeballos.FastFood.branch;

import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.address.Address;
import net.leozeballos.FastFood.mapper.BranchMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
public class BranchRestController {

    private final BranchService branchService;
    private final BranchMapper branchMapper;

    @GetMapping("/me")
    public Map<String, String> getMe(Principal principal) {
        return Map.of("name", principal != null ? principal.getName() : "anonymous");
    }

    @GetMapping
    public List<BranchDTO> getAll() {
        return branchService.findAllDTO();
    }

    @GetMapping("/{id}")
    public BranchDTO getOne(@PathVariable Long id) {
        return branchService.findByIdDTO(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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
    public BranchDTO update(@PathVariable Long id, @Valid @RequestBody BranchDTO dto) {
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        branchService.deleteById(id);
    }
}

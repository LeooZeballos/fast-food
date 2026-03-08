package net.leozeballos.FastFood.branch;

import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.address.Address;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
public class BranchRestController {

    private final BranchService branchService;

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
    public BranchDTO create(@RequestBody BranchDTO dto) {
        Branch branch = Branch.builder()
                .name(dto.getName())
                .address(Address.builder()
                        .city(dto.getCity())
                        .street(dto.getStreet())
                        .build())
                .build();
        return branchService.convertToDTO(branchService.save(branch));
    }

    @PutMapping("/{id}")
    public BranchDTO update(@PathVariable Long id, @RequestBody BranchDTO dto) {
        Branch branch = branchService.findById(id);
        if (branch != null) {
            branch.setName(dto.getName());
            if (branch.getAddress() != null) {
                branch.getAddress().setCity(dto.getCity());
                branch.getAddress().setStreet(dto.getStreet());
            } else {
                branch.setAddress(Address.builder()
                        .city(dto.getCity())
                        .street(dto.getStreet())
                        .build());
            }
            return branchService.convertToDTO(branchService.save(branch));
        }
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        branchService.deleteById(id);
    }
}

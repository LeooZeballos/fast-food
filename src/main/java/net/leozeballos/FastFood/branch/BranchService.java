package net.leozeballos.FastFood.branch;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BranchService {

    private final BranchRepository branchRepository;

    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    public List<BranchDTO> findAllDTO() {
        return branchRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<Branch> findAll() {
        return branchRepository.findAll();
    }

    public BranchDTO findByIdDTO(Long id) {
        return branchRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public Branch findById(Long id) {
        return branchRepository.findById(id).orElse(null);
    }

    public BranchDTO convertToDTO(Branch branch) {
        return BranchDTO.builder()
                .id(branch.getId())
                .name(branch.getName())
                .street(branch.getAddress() != null ? branch.getAddress().getStreet() : "Unknown")
                .city(branch.getAddress() != null ? branch.getAddress().getCity() : "Unknown")
                .build();
    }

    public Branch save(Branch branch) {
        return branchRepository.save(branch);
    }

    public void delete(Branch branch) {
        branchRepository.delete(branch);
    }

    public void deleteById(Long id) {
        branchRepository.deleteById(id);
    }

    public void deleteAll() {
        branchRepository.deleteAll();
    }

}

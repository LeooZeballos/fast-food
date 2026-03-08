package net.leozeballos.FastFood.branch;

import net.leozeballos.FastFood.error.ResourceNotFoundException;
import net.leozeballos.FastFood.mapper.BranchMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BranchService {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;

    public BranchService(BranchRepository branchRepository, BranchMapper branchMapper) {
        this.branchRepository = branchRepository;
        this.branchMapper = branchMapper;
    }

    public List<BranchDTO> findAllDTO() {
        return branchRepository.findAll().stream()
                .map(branchMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<Branch> findAll() {
        return branchRepository.findAll();
    }

    public BranchDTO findByIdDTO(Long id) {
        return branchRepository.findById(id)
                .map(branchMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + id));
    }

    public Branch findById(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + id));
    }

    public Branch save(Branch branch) {
        return branchRepository.save(branch);
    }

    public void delete(Branch branch) {
        branchRepository.delete(branch);
    }

    public void deleteById(Long id) {
        if (!branchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Branch not found with id: " + id);
        }
        branchRepository.deleteById(id);
    }

    public void deleteAll() {
        branchRepository.deleteAll();
    }

}

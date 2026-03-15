package net.leozeballos.FastFood.branch;

import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.error.ResourceNotFoundException;
import net.leozeballos.FastFood.mapper.BranchMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BranchService {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;

    @Cacheable(value = "branches")
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

    @CacheEvict(value = "branches", allEntries = true)
    public Branch save(Branch branch) {
        return branchRepository.save(branch);
    }

    @CacheEvict(value = "branches", allEntries = true)
    public void delete(Branch branch) {
        branchRepository.delete(branch);
    }

    @CacheEvict(value = "branches", allEntries = true)
    public void deleteById(Long id) {
        if (!branchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Branch not found with id: " + id);
        }
        branchRepository.deleteById(id);
    }

    @CacheEvict(value = "branches", allEntries = true)
    public void deleteAll() {
        branchRepository.deleteAll();
    }

}

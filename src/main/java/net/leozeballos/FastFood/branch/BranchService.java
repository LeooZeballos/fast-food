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
    private final net.leozeballos.FastFood.util.AuditService auditService;

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
    @Transactional
    public Branch save(Branch branch) {
        String action = (branch.getId() == null) ? "CREATE_BRANCH" : "UPDATE_BRANCH";
        Branch saved = branchRepository.save(branch);
        auditService.logAction(action, "ID=" + saved.getId() + ", Name=" + saved.getName());
        return saved;
    }

    @CacheEvict(value = "branches", allEntries = true)
    @Transactional
    public void delete(Branch branch) {
        auditService.logAction("DELETE_BRANCH", "ID=" + branch.getId() + ", Name=" + branch.getName());
        branchRepository.delete(branch);
    }

    @CacheEvict(value = "branches", allEntries = true)
    @Transactional
    public void deleteById(Long id) {
        Branch branch = findById(id);
        auditService.logAction("DELETE_BRANCH", "ID=" + id + ", Name=" + branch.getName());
        branchRepository.deleteById(id);
    }

    @CacheEvict(value = "branches", allEntries = true)
    public void deleteAll() {
        branchRepository.deleteAll();
    }

}

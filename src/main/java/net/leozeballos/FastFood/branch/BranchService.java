package net.leozeballos.FastFood.branch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchService {

    BranchRepository branchRepository;

    @Autowired
    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    public List<Branch> findAll() {
        return branchRepository.findAll();
    }

    public Branch findById(Long id) {
        return branchRepository.findById(id).orElse(null);
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

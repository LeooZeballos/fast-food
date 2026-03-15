package net.leozeballos.FastFood.mapper;

import net.leozeballos.FastFood.branch.Branch;
import net.leozeballos.FastFood.branch.BranchDTO;
import org.springframework.stereotype.Component;

@Component
public class BranchMapper {

    public BranchDTO toDTO(Branch branch) {
        if (branch == null) {
            return null;
        }
        return BranchDTO.builder()
                .id(branch.getId())
                .name(branch.getName())
                .street(branch.getAddress() != null ? branch.getAddress().getStreet() : "Unknown")
                .city(branch.getAddress() != null ? branch.getAddress().getCity() : "Unknown")
                .build();
    }
}

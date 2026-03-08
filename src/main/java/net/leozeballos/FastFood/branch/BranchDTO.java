package net.leozeballos.FastFood.branch;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record BranchDTO(
    Long id,
    @NotBlank(message = "Branch name is required")
    @Size(min = 1, max = 50, message = "Branch name must be between 1 and 50 characters")
    String name,
    @NotBlank(message = "Street is required")
    String street,
    @NotBlank(message = "City is required")
    String city
) {}

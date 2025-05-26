package com.systems.backend.users.resquests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoleRequest {
    @NotBlank(message = "Role name can't be empty")
    private String name;
    @NotBlank(message = "Description can't be empty")
    private String description;
}

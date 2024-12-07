package com.systems.backend.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDocUserRequest {
    @NotBlank(message = "Name is required!")
    private String name;
    @NotBlank(message = "Email is required!")
    @Size(message = "Email", max = 100, min = 6)
    private String email;
    @Size(max = 20, min = 10, message = "Phone length should in range 10-20")
    private String phone;
    @NotNull(message = "Date of birth is required!")
    private String birthday;
    @ColumnDefault("0")
    private Boolean gender;
}

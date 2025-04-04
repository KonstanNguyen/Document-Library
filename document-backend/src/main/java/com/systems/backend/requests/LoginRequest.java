package com.systems.backend.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class  LoginRequest  {
    @NotBlank(message = "Username is required!")
    public String username;
    @NotBlank(message = "Password is required!")
    public String password;
}

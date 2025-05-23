package com.systems.backend.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest extends CreateDocUserRequest {
    @NotBlank(message = "Username is required!")
    private String username;
    @NotBlank(message = "Password is required!")
    private String password;

    public RegisterRequest(String name, String email, String phone, String birthday, Boolean gender, String username, String password) {
        super(name, email, phone, birthday, gender);
        this.username = username;
        this.password = password;
    }
}

package com.systems.backend.users.responses;

import lombok.*;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String username;
    private String token;
    private long expiresIn;
}

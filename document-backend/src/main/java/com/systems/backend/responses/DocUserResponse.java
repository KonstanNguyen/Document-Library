package com.systems.backend.responses;

import java.time.LocalDate;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocUserResponse {
    private Long id;
    @Column(name = "dateOfBirth", nullable = false)
    private LocalDate dateOfBirth;
    private String name;
    private String phone;
    private Boolean gender;
    private String address;
    private String email;
    private Long accountId;
}

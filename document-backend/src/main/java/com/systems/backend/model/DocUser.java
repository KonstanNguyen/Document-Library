package com.systems.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @JsonFormat(pattern="dd-MM-yyyy")
    @Column(name = "dateOfBirth", nullable = false)
    private LocalDate dateOfBirth;

    @Nationalized
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone", length = 20)
    private String phone;

    @ColumnDefault("0")
    @Column(name = "gender", nullable = false)
    private Boolean gender = false;

    @Lob
    @Column(name = "address")
    private String address;

    @Column(name = "email", length = 100)
    private String email;

    @OneToOne(mappedBy = "user")
    private Account account;
}
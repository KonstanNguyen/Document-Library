package com.systems.backend.repository;

import com.systems.backend.model.DocUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocUserRepository extends JpaRepository<DocUser, Long> {
}

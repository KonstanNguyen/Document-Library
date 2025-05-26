package com.systems.backend.users.repositories;

import com.systems.backend.users.models.DocUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocUserRepository extends JpaRepository<DocUser, Long> {
}

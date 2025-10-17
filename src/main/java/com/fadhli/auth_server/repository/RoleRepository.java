package com.fadhli.auth_server.repository;

import com.fadhli.auth_server.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Boolean existsByName(String name);
    Boolean existsByNameAndIdNot(String name, Long id);
}

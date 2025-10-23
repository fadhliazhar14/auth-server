package com.fadhli.auth_server.repository;

import com.fadhli.auth_server.entity.Role;
import com.fadhli.auth_server.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Boolean existsByName(String name);
    Boolean existsByNameAndIdNot(String name, Long id);

    @Query("""
            SELECT r
            FROM Role r
            WHERE :search IS NULL OR :search = ''
            OR r.name LIKE CONCAT(:search, '%')
            OR r.description LIKE CONCAT(:search, '%')
            """)
    Page<Role> findAllWithPagination(@Param("search") String search, Pageable pageable);
}

package com.fadhli.auth_server.repository;

import com.fadhli.auth_server.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByUsernameAndIdNot(String username, Long id);
    Boolean existsByEmailAndIdNot(String email, Long id);

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    @Query("""
            SELECT u
            FROM User u
            WHERE :search IS NULL OR :search = ''
            OR u.name LIKE CONCAT(:search, '%')
            OR u.username LIKE CONCAT(:search, '%')
            OR u.email LIKE CONCAT(:search, '%')
            """)
    Page<User> findAllWithPagination(@Param("search") String search, Pageable pageable);

    @Query("""
        SELECT u 
        FROM User u 
        LEFT JOIN FETCH u.userRoles ur 
        LEFT JOIN FETCH ur.role r 
        WHERE u.username = :username 
          AND u.isActive = true
          AND (r.isActive = true OR r IS NULL)
    """)
    Optional<User> findByUsernameWithActiveRoles(@Param("username") String username);
}

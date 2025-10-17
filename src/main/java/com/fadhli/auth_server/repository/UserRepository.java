package com.fadhli.auth_server.repository;

import com.fadhli.auth_server.entity.User;
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

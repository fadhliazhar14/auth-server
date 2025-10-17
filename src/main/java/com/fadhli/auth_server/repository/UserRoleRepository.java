package com.fadhli.auth_server.repository;

import com.fadhli.auth_server.entity.UserRole;
import com.fadhli.auth_server.repository.projection.RoleProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    @Query("""
            SELECT r.id AS roleId, r.name AS name, r.description AS description, ur.createdAt AS createdAt
            FROM Role r
            INNER JOIN UserRole ur ON r.id = ur.role.id
            WHERE ur.user.id = :userId
            ORDER BY r.name ASC
            """)
    List<RoleProjection> findByUserIdWithDetail(@Param("userId") Long UserId);

    List<UserRole> findByUserId(Long userId);

    Optional<UserRole> findByRoleId(Long roleId);

    void deleteByRoleId(Long roleId);
}

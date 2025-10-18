package com.fadhli.auth_server.repository;

import com.fadhli.auth_server.entity.JwksKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JwksKeyRepository extends JpaRepository<JwksKey, Long> {
    @Query("""
            SELECT jk
            FROM JwksKey jk
            WHERE jk.isActive = true
            ORDER BY jk.createdAt DESC
            """)
    List<JwksKey> findActiveJwksKeys();

    @Query("""
            SELECT jk
            FROM JwksKey jk
            WHERE jk.isActive = true
            AND jk.expiresAt > CURRENT_TIMESTAMP
            ORDER BY jk.createdAt DESC
            """)
    Optional<JwksKey> findActiveJwksKeyWithValidExpiry();

    Optional<JwksKey> findByKid(String kid);

    @Modifying
    @Query("UPDATE JwksKey k SET k.isActive = false WHERE k.isActive = true")
    void deactivateAllActiveKeys();
}

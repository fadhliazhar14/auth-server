package com.fadhli.auth_server.repository;

import com.fadhli.auth_server.entity.PasswordResetToken;
import com.fadhli.auth_server.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    List<PasswordResetToken> findByUser(User user);

    @Modifying
    @Transactional
    @Query("""
            DELETE FROM PasswordResetToken p WHERE p.expiryDate < :expiryDate
            """)
    void deleteExpiredTokens(@Param("currentTime") LocalDateTime currenTime);

    @Query("""
            SELECT COUNT(p)
            FROM PasswordResetToken p
            WHERE p.user = :user
            AND p.expiryDate > :currentTime 
            """)
    long countValidTokensByUser(@Param("user") User user, @Param("currentTime") LocalDateTime currentTime);
}
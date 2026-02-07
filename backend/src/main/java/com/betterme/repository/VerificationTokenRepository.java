package com.betterme.repository;

import com.betterme.model.User;
import com.betterme.model.VerificationToken;
import com.betterme.model.VerificationToken.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByTokenAndTokenType(String token, TokenType tokenType);

    Optional<VerificationToken> findByUserAndTokenTypeAndUsedFalse(User user, TokenType tokenType);

    @Modifying
    @Query("DELETE FROM VerificationToken t WHERE t.expiryDate < ?1")
    void deleteExpiredTokens(LocalDateTime now);

    @Modifying
    @Query("DELETE FROM VerificationToken t WHERE t.user = ?1 AND t.tokenType = ?2")
    void deleteByUserAndTokenType(User user, TokenType tokenType);
}

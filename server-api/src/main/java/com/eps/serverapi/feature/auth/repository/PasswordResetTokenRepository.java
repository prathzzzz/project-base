package com.eps.serverapi.feature.auth.repository;

import com.eps.serverapi.feature.auth.entity.PasswordResetToken;
import com.eps.serverapi.feature.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(User user);
}

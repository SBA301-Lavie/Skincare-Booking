package lavie.skincare_booking.repository;

import lavie.skincare_booking.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, String> {
    Optional<AccountEntity> findByEmail(String email);
    Optional<AccountEntity> findByPhone(String phone);
    Optional<AccountEntity> findByRefreshToken(String refreshToken);
    AccountEntity findByAccountId(Long accountId);
    Optional<AccountEntity> findByVerificationToken(String token);
    boolean existsByEmail(String email);
}


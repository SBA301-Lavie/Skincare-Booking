package lavie.skincare_booking.service;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lavie.skincare_booking.entity.AccountEntity;
import lavie.skincare_booking.enums.AccountRole;
import lavie.skincare_booking.enums.TokenType;
import lavie.skincare_booking.model.AuthenticationRequest;
import lavie.skincare_booking.model.AuthenticationResponse;
import lavie.skincare_booking.model.RegisterRequest;
import lavie.skincare_booking.model.RegisterResponse;
import lavie.skincare_booking.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService implements LogoutHandler {

    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        AccountEntity accountEntity = accountRepository.findByEmail(request.getEmail()).orElseThrow();

        Map<String, Object> extraClaims = new HashMap<>();
        String jwtToken = jwtService.generateAccessToken(extraClaims, accountEntity.getEmail(), String.valueOf(accountEntity.getRole()));
        String refreshToken = jwtService.generateRefreshToken(accountEntity.getEmail());
        // Update the refresh token in the database
        accountEntity.setRefreshToken(refreshToken);
        accountRepository.save(accountEntity);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();

    }

    public RegisterResponse register(RegisterRequest request) throws MessagingException {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        String verificationToken = generateVerificationToken();

        AccountEntity accountEntity = AccountEntity.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .birthday(request.getBirthday())
                .isEnable(false)
                .isBlocked(false)
                .role(AccountRole.CLIENT)
                .verificationToken(verificationToken)
                .verificationTokenExpiry(LocalDateTime.now().plusMinutes(10))
                .build();

        AccountEntity savedAccount = accountRepository.save(accountEntity);
        emailService.sendVerificationEmail(request.getEmail(), verificationToken, 10);

        return RegisterResponse.builder()
                .accountId(savedAccount.getAccountId())
                .email(savedAccount.getEmail())
                .phone(savedAccount.getPhone())
                .birthday(savedAccount.getBirthday())
                .isEnable(savedAccount.getIsEnable())
                .createdDate(savedAccount.getCreatedDate())
                .build();
    }

    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }

    public void verifyEmail(String token) {
        AccountEntity account = accountRepository.findByVerificationToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid or already used verification token"));

        if (account.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification token has expired");
        }

        account.setIsEnable(true);
        account.setVerificationToken(null);
        account.setVerificationTokenExpiry(null);
        accountRepository.save(account);
    }

    public void resendVerificationEmail(String email) throws MessagingException {
        AccountEntity account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        if (account.getIsEnable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account already verified");
        }

        String newToken = generateVerificationToken();
        account.setVerificationToken(newToken);
        account.setVerificationTokenExpiry(LocalDateTime.now().plusHours(10));
        accountRepository.save(account);

        emailService.sendVerificationEmail(email, newToken, 10);
    }

    public AuthenticationResponse refreshToken(HttpServletRequest request) {
        final String authenticationHeader = request.getHeader("Authorization");
        final String refreshToken;

        if (authenticationHeader == null || !authenticationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Refresh token is missing");
        }
        refreshToken = authenticationHeader.replace("Bearer ", "");
        final AccountEntity accountEntity = accountRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (jwtService.isTokenValid(refreshToken, accountEntity, TokenType.REFRESH)) {
            Map<String, Object> extraClaims = new HashMap<>();
            String newAccessToken = jwtService.generateAccessToken(extraClaims, accountEntity.getEmail(), String.valueOf(AccountRole.CLIENT));

            return AuthenticationResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
        throw new RuntimeException("Refresh token is invalid");
    }


    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

    }
}

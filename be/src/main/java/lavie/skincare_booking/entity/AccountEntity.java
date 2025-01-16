package lavie.skincare_booking.entity;

import jakarta.persistence.*;
import lavie.skincare_booking.enums.AccountRole;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "Account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AccountEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "email", length = 50, nullable = false, unique = true)
    private String email;

    @Column(name = "phone", length = 50, nullable = false, unique = true)
    private String phone;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "is_enable", nullable = false)
    private Boolean isEnable;

    @Column(name = "is_blocked", nullable = false)
    private Boolean isBlocked;

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedBy
    @Column(name = "updated_by", insertable = false)
    private String updatedBy;

    @LastModifiedDate
    @Column(name = "updated_date", insertable = false)
    private LocalDateTime updatedDate;

    @Column(name = "role", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountRole role;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "verification_token_expiry")
    private LocalDateTime verificationTokenExpiry;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isBlocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnable;
    }
}


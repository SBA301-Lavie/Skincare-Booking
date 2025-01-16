package lavie.skincare_booking.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lavie.skincare_booking.enums.AccountRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotNull(message = "Email can not be null")
    @NotBlank(message = "Email can not be blank")
    @Email
    private String email;

    @NotNull(message = "Phone can not be null")
    @NotBlank(message = "Phone can not be blank")
    @Pattern(regexp = "^\\+?[0-9]{10,12}$", message = "Invalid phone number format")
    private String phone;

    @NotNull(message = "Password can not be null")
    @NotBlank(message = "Password can not be blank")
    private String password;

    @NotNull(message = "Role can not be null")
    @Pattern(regexp = "CLIENT|EMPLOYEE", message = "Role must be either CLIENT or EMPLOYEE")
    private String role;

    private LocalDate birthday;
}

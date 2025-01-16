package lavie.skincare_booking.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {
    @NotNull(message = "can not be null")
    @NotBlank(message = "can not be blank")
    @Email
    private String email;

    @NotNull(message = "can not be null")
    @NotBlank(message = "can not be blank")
    private String password;
}

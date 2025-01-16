package lavie.skincare_booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private Long accountId;
    private String email;
    private String phone;
    private LocalDate birthday;
    private Boolean isEnable;
    private LocalDateTime createdDate;
}

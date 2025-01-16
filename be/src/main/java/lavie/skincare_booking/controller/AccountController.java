package lavie.skincare_booking.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lavie.skincare_booking.model.AuthenticationRequest;
import lavie.skincare_booking.model.AuthenticationResponse;
import lavie.skincare_booking.model.RegisterRequest;
import lavie.skincare_booking.model.RegisterResponse;
import lavie.skincare_booking.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
@Validated
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) throws MessagingException {
        RegisterResponse response = accountService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = accountService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String token, Model model) {
        try {
            accountService.verifyEmail(token);
            model.addAttribute("success", true);
            model.addAttribute("message", "Email verification successful! You can now login to your account.");
        } catch (ResponseStatusException e) {
            model.addAttribute("success", false);
            model.addAttribute("message", e.getReason());
        }
        return "verification-result";
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(@RequestParam String email) throws MessagingException {
        accountService.resendVerificationEmail(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request) {
        AuthenticationResponse refreshTokenResponse = accountService.refreshToken(request);
        return ResponseEntity.ok(refreshTokenResponse);
    }

//    @GetMapping("/test-authentication")
    public ResponseEntity<String> testAuthentication() {
        return ResponseEntity.ok("Authentication successful");
    }

}

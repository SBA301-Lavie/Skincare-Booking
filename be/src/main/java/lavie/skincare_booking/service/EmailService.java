package lavie.skincare_booking.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.application.base-url}")
    private String baseUrl;

    @Async
    public void sendVerificationEmail(String to, String token, long expirationMinutes) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                true,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariable("verificationLink", baseUrl + "/account/verify?token=" + token);
        context.setVariable("expirationMinutes", expirationMinutes);

        String htmlContent = templateEngine.process("email-verification-template", context);

        helper.setTo(to);
        helper.setSubject("Verify Your Email - Lavie Skincare");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

}

package lavie.skincare_booking.config;


import lavie.skincare_booking.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AccountService accountService) throws Exception {
        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authReq -> authReq.requestMatchers(
                                        "/account/register",
                                        "/account/login",
                                        "/account/logout",
                                        "/account/refresh-token",
                                        "/account/resend-verification",
                                        "/account/verify"
                                ).permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(
                        logout -> logout.logoutUrl("/account/logout")
                                .addLogoutHandler(accountService)
                                .logoutSuccessHandler(
                                        (request, response, authentication) -> SecurityContextHolder.clearContext()
                                )
                )
                .build();
    }
}

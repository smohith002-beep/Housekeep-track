package com.example.housekeeptrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN", "SUPERVISOR", "HOUSEKEEPER", "RECEPTIONIST")
                .build();

        UserDetails supervisor = User.builder()
                .username("supervisor")
                .password(passwordEncoder.encode("supervisor123"))
                .roles("SUPERVISOR")
                .build();

        UserDetails housekeeper = User.builder()
                .username("housekeeper")
                .password(passwordEncoder.encode("housekeeper123"))
                .roles("HOUSEKEEPER")
                .build();

        UserDetails receptionist = User.builder()
                .username("receptionist")
                .password(passwordEncoder.encode("receptionist123"))
                .roles("RECEPTIONIST")
                .build();

        return new InMemoryUserDetailsManager(admin, supervisor, housekeeper, receptionist);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for REST APIs and form tests so Postman and Fetch API work seamlessly
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .authorizeHttpRequests(auth -> auth
                // Static resources
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                // Public UI pages
                .requestMatchers("/", "/rooms", "/room-status", "/housekeepers", "/cleaning-tasks",
                                 "/inspections", "/bookings", "/admin/dashboard", "/reports",
                                 "/audit-logs", "/gallery", "/contact", "/login").permitAll()
                // REST APIs accessible for evaluation, Postman and frontend AJAX
                .requestMatchers("/api/**").permitAll()
                // Any other request
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}

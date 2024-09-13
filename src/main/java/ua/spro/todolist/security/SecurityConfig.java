package ua.spro.todolist.security;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ua.spro.todolist.repository.UserRepository;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

  private final UserRepository userRepository;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            authorizeRequests ->
                authorizeRequests
                    .requestMatchers(
                        "/login",
                        "/logout",
                        "/api/user/login",
                        "/api/user/logout",
                        "/api/user/register",
                        "/h2-console/**")
                    .permitAll() // Allow login, logout, and registration for all users
                    .anyRequest()
                    .authenticated() // All other endpoints require authentication
            )
        .formLogin(
            formLogin ->
                formLogin
                    .loginPage("/login") // Custom login page endpoint
                    .permitAll()
                    .defaultSuccessUrl("/api/task", true) // No redirect, login will return 200 OK
            )
        .logout(
            logout ->
                logout
                    .logoutUrl("/logout") // Endpoint for logging out
                    .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())// Return 200 OK
                    .invalidateHttpSession(true) // Invalidate the session after logout
                    .deleteCookies("JSESSIONID") // Clear the session cookie
                    .permitAll())
        .exceptionHandling(
            exceptionHandling ->
                exceptionHandling
                    // Return 401 Unauthorized instead of redirecting to /login
                    .authenticationEntryPoint(
                    (request, response, authException) ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
        .headers(
            headers ->
                headers.addHeaderWriter(
                    (request, response) ->
                        response.addHeader(
                            "Content-Security-Policy",
                            "frame-ancestors 'self' http://localhost:8080")))
        .authenticationProvider(authenticationProvider())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable);

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("*"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(false);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return new CustomUserDetailsService(userRepository);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService());
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}

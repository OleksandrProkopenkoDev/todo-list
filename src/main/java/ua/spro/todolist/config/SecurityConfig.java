package ua.spro.todolist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/login", "/logout", "/register")
                    .permitAll() // Allow login, logout, and registration for all users
                    .anyRequest()
                    .authenticated() // All other endpoints require authentication
            )
        .formLogin(
            formLogin ->
                formLogin
                    .loginPage("/login") // Use default Spring Security login page or customize it
                    .permitAll() // Allow everyone to access the login page
                    .defaultSuccessUrl("/tasks", true) // Redirect to the task list after login
            )
        .logout(
            logout ->
                logout
                    .logoutUrl("/logout") // Customize the logout URL
                    .logoutSuccessUrl(
                        "/login?logout") // Redirect to login page after successful logout
                    .permitAll() // Allow logout for all users
            )
        .csrf(AbstractHttpConfigurer::disable);// Disable CSRF for testing purposes (especially if no frontend or forms are
                    // used)

    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails user =
        User.withUsername("user")
            .password(passwordEncoder().encode("password"))
            .roles("USER")
            .build();

    return new InMemoryUserDetailsManager(user);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}

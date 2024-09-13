package ua.spro.todolist.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.spro.todolist.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.info("loadUserByUsername {}", username);
    // Find the user by username from the database
    UserDetails userDetails = userRepository
        .findByUsername(username)
        .map(
            user ->
                User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles("USER")
                    .build())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    log.info("loaded UserByUsername {}", userDetails);
    return userDetails;
  }
}

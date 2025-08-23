package technikal.task.fishmarket.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("ADMIN")
                .build();

        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("user"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http
                .authorizeHttpRequests((requests) -> requests
                            .requestMatchers("/fish/create", "/fish/delete/**").hasRole("ADMIN")
                            .requestMatchers("/fish").hasAnyRole("ADMIN", "USER")
                            .requestMatchers("/login", "/").permitAll()
                            .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                            .loginPage("/login")
                            .permitAll()
                            .defaultSuccessUrl("/fish", true)
                            .failureUrl("/login?error")
                )
                .logout((logout) -> logout
                        .permitAll()
                        .logoutSuccessUrl("/login")
                )
               .exceptionHandling((exception) -> exception
                       .accessDeniedPage("/login")
               );

            return http.build();
    }
}


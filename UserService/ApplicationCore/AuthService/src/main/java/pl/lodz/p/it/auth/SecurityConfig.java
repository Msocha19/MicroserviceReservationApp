package pl.lodz.p.it.auth;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

    @Bean
    public Filter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .cors().and().csrf().disable()
                .authorizeHttpRequests((auth) -> {
                            try {
                                auth
                                //--------------------USERS---------------------
                                .requestMatchers(HttpMethod.GET, "/customer").hasAnyRole("ADMINISTRATOR","CUSTOMER")
                                .requestMatchers(HttpMethod.GET, "/moderator").hasAnyRole("ADMINISTRATOR", "MODERATOR")
                                .requestMatchers(HttpMethod.GET, "/administrator").hasRole("ADMINISTRATOR")
                                .requestMatchers(HttpMethod.GET, "/customer/{id}").hasAnyRole("CUSTOMER", "ADMINISTRATOR")
                                .requestMatchers(HttpMethod.GET, "/moderator/{id}").hasAnyRole("MODERATOR", "ADMINISTRATOR")
                                .requestMatchers(HttpMethod.GET, "/administrator/{id}").hasRole("ADMINISTRATOR")
                                .requestMatchers(HttpMethod.PUT, "/customer/{id}/activate").hasRole("ADMINISTRATOR")
                                .requestMatchers(HttpMethod.PUT, "/customer/{id}/deactivate").hasRole("ADMINISTRATOR")
                                .requestMatchers(HttpMethod.PUT, "/administrator").hasRole("ADMINISTRATOR")
                                .requestMatchers(HttpMethod.PUT, "/moderator").hasRole("ADMINISTRATOR")
                                .requestMatchers(HttpMethod.PUT, "/customer").hasRole("ANONYMOUS")
                                .requestMatchers(HttpMethod.PUT, "/customer/update").hasAnyRole("CUSTOMER", "ADMINISTRATOR")
                                .requestMatchers(HttpMethod.PUT, "/passwordChange").hasAnyRole("MODERATOR", "ADMINISTRATOR", "CUSTOMER")
                                .requestMatchers(HttpMethod.PUT, "/moderator/update").hasAnyRole("MODERATOR", "ADMINISTRATOR")
                                .requestMatchers(HttpMethod.POST, "/login").hasRole("ANONYMOUS")
                                .anyRequest().permitAll();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                ).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

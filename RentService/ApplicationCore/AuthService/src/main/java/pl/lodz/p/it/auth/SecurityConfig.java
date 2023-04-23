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
                                    //--------------------PRODUCTS---------------------
                                    .requestMatchers(HttpMethod.GET, "/product").hasAnyRole("CUSTOMER", "MODERATOR", "ADMINISTRATOR", "ANONYMOUS")
                                    .requestMatchers(HttpMethod.GET, "/product/{id}").hasAnyRole("CUSTOMER", "MODERATOR", "ADMINISTRATOR")
                                    .requestMatchers(HttpMethod.GET, "/product/{id}/reservations").hasAnyRole("MODERATOR", "ADMINISTRATOR")
                                    .requestMatchers(HttpMethod.PUT, "/ski").hasRole("MODERATOR")
                                    .requestMatchers(HttpMethod.PUT, "/skiboot").hasRole("MODERATOR")
                                    .requestMatchers(HttpMethod.DELETE, "/product/{id}").hasRole("MODERATOR")
                                    .requestMatchers(HttpMethod.PUT, "/product/update/ski").hasRole("MODERATOR")
                                    .requestMatchers(HttpMethod.PUT, "/product/update/skiboot").hasRole("MODERATOR")
                                    //--------------------RESERVATION---------------------
                                    .requestMatchers(HttpMethod.GET, "/reservation").hasAnyRole("MODERATOR", "ADMINISTRATOR")
                                    .requestMatchers(HttpMethod.GET, "/reservation/{id}").hasAnyRole("CUSTOMER","MODERATOR", "ADMINISTRATOR")
                                    .requestMatchers(HttpMethod.PUT, "/reservation").hasRole("CUSTOMER")
                                    .requestMatchers(HttpMethod.DELETE, "/reservation/{id}").hasAnyRole("CUSTOMER", "MODERATOR", "ADMINISTRATOR")
                                    .requestMatchers(HttpMethod.DELETE, "/reservation/forced/{id}").hasRole("ADMINISTRATOR")
                                    .requestMatchers(HttpMethod.PUT, "/reservation/update").hasRole("ADMINISTRATOR")
                                    .requestMatchers(HttpMethod.GET, "/reservation/client").hasAnyRole("CUSTOMER", "ADMINISTRATOR")
                                    .requestMatchers(HttpMethod.GET, "/product/{id}").hasAnyRole("MODERATOR", "ADMINISTRATOR")
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

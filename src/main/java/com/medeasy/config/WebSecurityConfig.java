package com.medeasy.config;

import com.medeasy.common.filter.JwtAuthenticationFilter;
import com.medeasy.domain.auth.util.JwtTokenHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtTokenHelper jwtTokenHelper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests((authorizeRequests)->
                    authorizeRequests
                            .requestMatchers(
                                    "/open-api/**",
                                    "/api/swagger",
                                    "/api/swagger-ui/index.html",
                                    "/api/swagger-ui/**",
                                    "/v3/api-docs/**", // swagger 호출하는 주소
                                    "/",
                                    "/favicon.ico"
                                    )
                            .permitAll()
                            .anyRequest().authenticated()
                );

        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenHelper), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

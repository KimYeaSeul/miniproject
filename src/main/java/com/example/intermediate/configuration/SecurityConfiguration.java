package com.example.intermediate.configuration;

import com.example.intermediate.jwt.AccessDeniedHandlerException;
import com.example.intermediate.jwt.AuthenticationEntryPointException;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfiguration {

  @Value("${jwt.secret}")
  String SECRET_KEY;
  private final TokenProvider tokenProvider;
  private final UserDetailsServiceImpl userDetailsService;
  private final AuthenticationEntryPointException authenticationEntryPointException;
  private final AccessDeniedHandlerException accessDeniedHandlerException;
  private static final String[] PERMIT_URL_ARRAY={
          "/user/signup",
          "/user/login",
          "/user/username",
          "/user/nickname",
          "/user/kakao/callback",
          "/favicon.ico",
          "/api/member/**",
          "/api/post/**",
          "/api/posts",
          "/api/comment/**",
          "/user/google/**",
          "/h2-console/**"
  };
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @Order(SecurityProperties.BASIC_AUTH_ORDER)
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors().configurationSource(corsConfigurationSource());

    http.headers().frameOptions().disable(); // 이거 하니까 h2됨.

    http.csrf()
        .ignoringAntMatchers("/h2-console/**", "/favicon.ico")
        .disable()

        .exceptionHandling()
        .authenticationEntryPoint(authenticationEntryPointException)
        .accessDeniedHandler(accessDeniedHandlerException)

        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        .and()
        .authorizeRequests()
        .antMatchers(PERMIT_URL_ARRAY).permitAll()
        .anyRequest().authenticated()

        .and()
        .apply(new JwtSecurityConfiguration(SECRET_KEY, tokenProvider, userDetailsService));

    return http.build();
  }


  @Bean
  CorsConfigurationSource corsConfigurationSource(){
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOriginPatterns(Arrays.asList("*"));
//    configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000"));
    configuration.setAllowedMethods(Arrays.asList("HEAD","POST","GET","DELETE","PUT"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);


    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}

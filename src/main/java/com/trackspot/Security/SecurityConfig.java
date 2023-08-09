package com.trackspot.Security;


import com.trackspot.Jwtutils.JwtAuthenticationEntryPoint;
import com.trackspot.Jwtutils.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtFilter filter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws
            Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/emulator/*").permitAll() // Allow unauthenticated access to "/emulator"
                .antMatchers("/emulator/trip/*").permitAll() // Allow unauthenticated access to "/emulator"
                .antMatchers("/emulator/sync/*").permitAll() // Allow unauthenticated access to "/emulator"
                .antMatchers("/emulator/sync/*").permitAll() // Allow unauthenticated access to "/emulator"
                .antMatchers("/admin/sign-in").permitAll() // Allow unauthenticated access to "/emulator"
                .antMatchers("/admin/log-in").permitAll() // Allow unauthenticated access to "/emulator"
                .antMatchers("/log-in").permitAll() // Allow unauthenticated access to "/emulator"
                .antMatchers("/download/apkLink").permitAll() // Allow unauthenticated access to "/emulator"
                .antMatchers("/user/*").permitAll() // Allow unauthenticated access to "/emulator"
                .antMatchers("/admin/current").hasRole("ADMIN") // Allow unauthenticated access to "/emulator"
                .anyRequest().authenticated()
                .and().httpBasic()
                .and().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        // Configure CORS
        http.cors().configurationSource(corsConfigurationSource());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://example.com", "http://localhost:3000", "*")); // Add your allowed origins or patterns here
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
/*
 * TO CHECK CORS response..
    curl -v -H "Access-Control-Request-Method: GET" -H "Origin: http://localhost:3000/" -X OPTIONS http://192.168.1.112:8080/emulator
    curl -v -H "Origin: http://localhost:3000" -H "Content-Type: application/json" -d '{"username": "Admin", "password": "admin@112"}' -X POST http://192.168.1.112:8080/admin/log-in
    curl -v -H "Origin: http://localhost:3000" -H "Content-Type: application/json" http://192.168.1.112:8080/admin/log-in
    curl -v -H "Origin: http://localhost:3000" -H "Content-Type: application/json" http://192.168.1.112:8080/emulator/create
 * */

}
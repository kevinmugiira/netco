package com.netco.microservices.discoveryserver.config;


//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@Configuration
public class SecurityConfig {

//    @Value("${eureka.username}")
//    private String username;
//    @Value("${eureka.password}")
//    private String password;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(new UsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public InMemoryUserDetailsManager userDetailsManager() {
//        UserDetails user = User.builder()
//                .username("eureka")
//                .password(passwordEncoder().encode("password"))
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(user);
//    }
}

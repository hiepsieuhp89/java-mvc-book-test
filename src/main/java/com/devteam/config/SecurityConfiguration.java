package com.devteam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Lazy
	@Autowired
	private UserDetailsService userDetailsService;

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService)
			.passwordEncoder(passwordEncoder());
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeRequests(authorize -> authorize
				.antMatchers("/api/books/secure/**", "/api/reviews/secure/**", "/api/messages/secure/**", "/api/admin/secure/**")
				.authenticated()
				.antMatchers("/", "/home", "/search", "/book/**", "/index", "/css/**", "/js/**", "/images/**", 
							 "/register", "/api/books", "/api/books/**", "/favicon.ico")
				.permitAll()
				.antMatchers("/shelf", "/fees").hasAnyRole("USER", "ADMIN")
				.antMatchers("/admin").hasRole("ADMIN")
				.anyRequest().authenticated()
			)
			.formLogin(form -> form
				.loginPage("/login")
				.defaultSuccessUrl("/home", true)
				.permitAll()
			)
			.logout(logout -> logout
				.permitAll()
				.logoutSuccessUrl("/login?logout")
			)
			.exceptionHandling(ex -> ex
				.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
			);

		// Add CORS filters
		http.cors();

		return http.build();
	}
}

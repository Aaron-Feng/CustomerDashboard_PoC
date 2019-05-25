package com.vonage.vnet.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.vonage.vnet.repo.UserRepository;
import com.vonage.vnet.services.CustomUserDetailsService;

@Configuration
@EnableJpaRepositories(basePackageClasses= UserRepository.class)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private CustomUserDetailsService userDetailsService;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.userDetailsService(userDetailsService)
		.passwordEncoder(bCryptPasswordEncoder);
//		auth.inMemoryAuthentication()
//		.withUser("test").password(passwordEncoder().encode("test")).roles("USER");
	}
	protected void configure(HttpSecurity http) throws Exception{
		http.csrf().disable()
				.authorizeRequests()
				.antMatchers("/product/**","/shop/**").authenticated()
				.anyRequest().permitAll()
				.and()
				.formLogin().loginPage("/login").permitAll()
				.failureUrl("/login?error=true")
				.defaultSuccessUrl("/welcome")
				.and()
				.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/").and().exceptionHandling()
                .accessDeniedPage("/access-denied");
		
		
	}

}

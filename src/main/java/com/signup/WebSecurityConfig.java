package com.signup;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        	.csrf().disable()
            .authorizeRequests()
	            .antMatchers("/assets/**").permitAll()
	            .antMatchers("/components/**").permitAll()
	            .antMatchers("/docs/**").permitAll()
	            .antMatchers("/excel/**").permitAll()
	            .antMatchers("/excel/user/**").permitAll()
	            .antMatchers("/imgupload/**").permitAll()
	            .antMatchers("/detail").permitAll()
	            .antMatchers("/","/admin","/**").authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/admin")
                .permitAll()
                .and()
            .logout()
                .permitAll();
     // 禁用缓存
     http.headers().cacheControl();
        	
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails user =
             User.withDefaultPasswordEncoder()
                .username("admin")
                .password("yogo")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}
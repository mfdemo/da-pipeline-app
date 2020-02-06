/*
        Simple Secure App

        Copyright (C) 2020 Micro Focus or one of its affiliates

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.microfocus.example.config;

import com.microfocus.example.web.UrlAuthenticationSuccessHandler;
import com.microfocus.example.web.LoggingAccessDeniedHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Configure Spring Security for custom application
 * @author Kevin A. Lee
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfiguration.class);

    @Autowired
    private LoggingAccessDeniedHandler accessDeniedHandler;

    @Value("${spring.profiles.active:Unknown}")
    private String activeProfile;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        if (activeProfile.contains("dev")) {
            log.info("Running development profile");
            httpSecurity.csrf().disable();
            httpSecurity.headers().frameOptions().disable();
        }

        httpSecurity
                .authorizeRequests()
                .antMatchers(
                    "/",
                    "/login",
                    "/logout",
                    "/register",
                    "/js/**/*",
                    "/css/**/*",
                    "/img/**/*",
                    "/webjars/**/*").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().fullyAuthenticated();

        httpSecurity.authorizeRequests().and().exceptionHandling().accessDeniedPage("/access-denied");

        httpSecurity.authorizeRequests().and().httpBasic();

        httpSecurity.authorizeRequests().and().formLogin()
                .loginProcessingUrl("/j_spring_security_check")
                .successHandler(JWAAuthenticationSuccessHandler())
                .loginPage("/login")
                .failureUrl("/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password");

        httpSecurity.authorizeRequests().and().logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .permitAll();
    }

    @Bean
    public AuthenticationSuccessHandler JWAAuthenticationSuccessHandler(){
        return new UrlAuthenticationSuccessHandler();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        Collection<UserDetails> users = new ArrayList<>();
        users.add(User.withDefaultPasswordEncoder()
                .username("admin")
                .password("password")
                .roles("ADMIN","USER")
                .build());
        users.add(User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build());
        return new InMemoryUserDetailsManager(users);
    }
}

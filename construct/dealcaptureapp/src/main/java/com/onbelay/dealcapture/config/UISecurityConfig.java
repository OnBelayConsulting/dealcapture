package com.onbelay.dealcapture.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SimpleSavedRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity()
@Order(2) //Load this rule after the api rule
@Profile("!test")
@Configuration
public class UISecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and().authorizeRequests()
                .antMatchers("/actuator/health/**").permitAll()
                .antMatchers("/actuator/prometheus").permitAll()
                .anyRequest().authenticated()
                .and()
                .headers(headers -> headers
                    .contentSecurityPolicy(contentSecurityPolicy -> contentSecurityPolicy
                            .policyDirectives("script-src 'self'; default-src 'self'; frame-ancestors 'self'; object-src 'none'")
                    )
                )
                .oauth2Login().userInfoEndpoint();
    }

    @Bean
    @Profile("dev")
    public RequestCache refererRequestCache() {
        return new HttpSessionRequestCache() {
            @Override
            public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
                String referrer = request.getHeader("referer");
                if (referrer != null) {
                    request.getSession().setAttribute("SPRING_SECURITY_SAVED_REQUEST", new SimpleSavedRequest(referrer));
                }
            }
        };
    }
}
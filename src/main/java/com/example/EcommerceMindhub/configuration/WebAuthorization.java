package com.example.EcommerceMindhub.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@EnableWebSecurity
@Configuration
public class WebAuthorization extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {


        http.authorizeRequests()
                //.antMatchers("/web/index.html").permitAll()
                //.antMatchers("/web/css/", "/web/img/", "/web/js/").permitAll()
                        .antMatchers(HttpMethod.POST,"/api/clients").permitAll()
                        .antMatchers("/h2-console/").hasAuthority("ADMIN")
                        .antMatchers("/clients").hasAuthority("ADMIN")
                        .antMatchers(HttpMethod.POST,"/api/purchaseOrders/**").hasAnyAuthority("ADMIN", "CLIENT")
                        .antMatchers("/api/purchaseReminder").hasAnyAuthority("ADMIN","CLIENT")
                        .antMatchers(HttpMethod.POST, "/api/shoppingCart/current/bills/payment/cash").hasAnyAuthority("ADMIN","CLIENT")
                        .antMatchers(HttpMethod.POST, "api/shoppingCart/current/bills/payment/card").hasAnyAuthority("ADMIN", "CLIENT")
                        .antMatchers(HttpMethod.POST,"api/products/newProducts").hasAnyAuthority("ADMIN", "CLIENT")
                        .antMatchers(HttpMethod.DELETE, "api/purchaseOrders").hasAnyAuthority("ADMIN", "CLIENT")
                        .antMatchers(HttpMethod.DELETE, "api/products").hasAuthority("ADMIN")
                        .antMatchers(HttpMethod.DELETE, "api/clients").hasAuthority("ADMIN")
                        .antMatchers("/**").hasAuthority("ADMIN");





        http.formLogin().usernameParameter("email")
                .passwordParameter("password")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout");

        //disabling frameOptions so h2-console can be accessed
        http.headers().frameOptions().disable();


        http.csrf().disable();

        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}


package net.explorviz.eaas.security;

import net.explorviz.eaas.frontend.view.LoginView;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

/**
 * @see <a href="https://vaadin.com/tutorials/securing-your-app-with-spring-security/setting-up-spring-security">Setting
 * up Spring Security for Vaadin applications</a>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    public static final String LOGIN_URL = "/" + LoginView.ROUTE;
    public static final String LOGOUT_URL = "/logout";
    public static final String HOME_URL = "/";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Vaadin has built-in CSRF
        // TODO: This also allows to logout via GET requests, this should be fixed
        http.csrf().disable();

        // Saves unauthorized access attempts, so the user is redirected after login
        HttpSessionRequestCache cache = new HttpSessionRequestCache();
        // Avoid saving internal vaadin requests
        cache.setRequestMatcher(request -> !SecurityUtils.isVaadinInternalRequest(request));
        http.requestCache().requestCache(cache);

        // Restrict access to our application
        http.authorizeRequests()
            // Allow all flow internal requests
            .requestMatchers(SecurityUtils::isVaadinInternalRequest).permitAll()
            // Allows requests to the API, it uses secrets for authentications
            .antMatchers("/api/v1/**").permitAll()
            // Allow all requests, because we're doing fine-grained authentication in Vaadin views on BeforeEnterEvent
            .anyRequest().permitAll()
            // Configure the login page
            .and().formLogin().loginPage(LOGIN_URL).permitAll()
            .loginProcessingUrl(LOGIN_URL).failureUrl(LOGIN_URL + "?error")
            // Configure logout
            .and().logout().logoutUrl(LOGOUT_URL).logoutSuccessUrl(HOME_URL);
    }

    @Override
    public void configure(WebSecurity web) {
        // Unlike the authorizeRequests matchers above, these completely bypass Spring Security
        web.ignoring().antMatchers(
            // Vaadin Flow static resources
            "/VAADIN/**",

            // the standard favicon URI
            "/favicon.ico",

            // the robots exclusion standard
            "/robots.txt",

            // web application manifest
            "/manifest.webmanifest",
            "/sw.js",
            "/offline-page.html",

            // icons and images
            "/icons/**",
            "/images/**",

            // (development mode) static resources
            "/frontend/**",

            // (development mode) webjars
            "/webjars/**",

            // (development mode) H2 debugging console
            "/h2-console/**",

            // (production mode) static resources
            "/frontend-es5/**", "/frontend-es6/**");
    }
}

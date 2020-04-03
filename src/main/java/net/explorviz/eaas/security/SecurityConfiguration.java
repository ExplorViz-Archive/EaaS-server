package net.explorviz.eaas.security;

import com.vaadin.flow.server.ServletHelper;
import com.vaadin.flow.shared.ApplicationConstants;
import net.explorviz.eaas.frontend.view.LoginView;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import javax.servlet.ServletRequest;
import java.util.stream.Stream;

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

    /**
     * Allow requests to the API, it uses secrets for authentications, not spring security
     */
    private static final String[] API_PATHS = { "/api/v1/**" };
    /**
     * Whitelist pages that *might be* public, we're doing fine-grained auth in Views on BeforeEnterEvent
     */
    private static final String[] POTENTIALLY_PUBLIC_VIEWS = { "/", "/explore", "/explore/", "/builds/**" };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Vaadin has built-in CSRF
        // TODO: This also allows to logout via GET requests, this should be fixed
        http.csrf().disable();

        // Saves unauthorized access attempts, so the user is redirected after login
        HttpSessionRequestCache cache = new HttpSessionRequestCache();
        // Avoid saving internal vaadin requests
        cache.setRequestMatcher(request -> !isVaadinInternalRequest(request)
            && !request.getRequestURI().startsWith(LOGIN_URL)
            && !request.getRequestURI().startsWith(LOGOUT_URL));
        http.requestCache().requestCache(cache);

        // Restrict access to our application
        http.authorizeRequests()
            // Allow all flow internal requests
            .requestMatchers(SecurityConfiguration::isVaadinInternalRequest).permitAll()
            .antMatchers(API_PATHS).permitAll()
            .antMatchers(HttpMethod.HEAD, POTENTIALLY_PUBLIC_VIEWS).permitAll()
            .antMatchers(HttpMethod.GET, POTENTIALLY_PUBLIC_VIEWS).permitAll()
            /*
             * All other paths are not known to ever be publicly accessible without login. We wouldn't *need* to
             * prevent access here, because we also have to verify permission in the Views anyway, but better be safe
             * than sorry. Note that these restrictions only apply on the first page load; subsequent page loads are
             * done through Vaadins SPA frontend framework using internal requests (which we allow above).
             */
            .anyRequest().authenticated()
            // Configure the login page
            .and().formLogin().loginPage(LOGIN_URL).permitAll()
            .loginProcessingUrl(LOGIN_URL).failureUrl(LOGIN_URL + "?" + LoginView.ERROR_PARAMETER)
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

    /**
     * Tests if the request is an internal Vaadin request by checking if the request contains Vaadins' request type
     * parameter and its value is any of the known request type.
     *
     * @param request {@link ServletRequest}
     * @return true if is an internal Vaadin request
     */
    private static boolean isVaadinInternalRequest(@NonNull ServletRequest request) {
        final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null
            && Stream.of(ServletHelper.RequestType.values()).anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }
}

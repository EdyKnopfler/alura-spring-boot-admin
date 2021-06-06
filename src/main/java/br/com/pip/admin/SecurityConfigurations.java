package br.com.pip.admin;

import java.util.UUID;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import de.codecentric.boot.admin.server.config.AdminServerProperties;

// UMA AULA DE COMO CONFIGURAAR A SEGURANÇA!
// https://codecentric.github.io/spring-boot-admin/current/#securing-spring-boot-admins

@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class SecurityConfigurations extends WebSecurityConfigurerAdapter {

	private AdminServerProperties adminServer;

	private SecurityProperties security;

	public SecurityConfigurations(AdminServerProperties adminServer, SecurityProperties security) {
		this.adminServer = adminServer;
		this.security = security;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
		successHandler.setTargetUrlParameter("redirectTo");
		successHandler.setDefaultTargetUrl(this.adminServer.path("/"));

	    http
	    	.authorizeRequests(
	            (authorizeRequests) -> authorizeRequests
	            	// Grants public access to all static assets and the login page.
	            	.antMatchers(this.adminServer.path("/assets/**")).permitAll() 
	                .antMatchers(this.adminServer.path("/actuator/info")).permitAll()
	                .antMatchers(this.adminServer.path("/actuator/health")).permitAll()
	                .antMatchers(this.adminServer.path("/login")).permitAll()
	                
	                // Every other request must be authenticated.
	                .anyRequest().authenticated())
	                
	        // Configures login and logout.
	        .formLogin(
	            (formLogin) -> formLogin
	            	.loginPage(this.adminServer.path("/login"))
	            	.successHandler(successHandler)) 
	        .logout((logout) -> logout
	        		.logoutUrl(this.adminServer.path("/logout")))
	        
	        // Enables HTTP-Basic support. This is needed for the Spring Boot Admin Client to register.
	        .httpBasic(Customizer.withDefaults())
	        
	        // Enables CSRF-Protection using Cookies.
	        .csrf((csrf) -> csrf
        		.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers(
                		
                	// 	Disables CSRF-Protection for the endpoint the Spring Boot Admin Client uses to (de-)register.
                    new AntPathRequestMatcher(this.adminServer.path("/instances"),
                        HttpMethod.POST.toString()), 
                    new AntPathRequestMatcher(this.adminServer.path("/instances/*"),
                        HttpMethod.DELETE.toString()), 
                    
                    // Disables CSRF-Protection for the actuator endpoints.
                    new AntPathRequestMatcher(this.adminServer.path("/actuator/**")) 
                ))
	            .rememberMe((rememberMe) -> rememberMe.key(UUID.randomUUID().toString()).tokenValiditySeconds(1209600));
	}

	// Required to provide UserDetailsService for "remember functionality"
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser(security.getUser().getName())
				.password("{noop}" + security.getUser().getPassword()).roles("USER");
	}

}

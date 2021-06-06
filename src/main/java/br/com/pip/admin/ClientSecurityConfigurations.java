package br.com.pip.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import de.codecentric.boot.admin.server.config.AdminServerAutoConfiguration;
import de.codecentric.boot.admin.server.config.AdminServerProperties;
import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider;

// https://stackoverflow.com/questions/67419133/springboot-admin-custom-authentication-flow-when-accessing-client
// Autenticação no actuator protegido com JWT

@Configuration
public class ClientSecurityConfigurations extends AdminServerAutoConfiguration {

	@Autowired
	private TokenService tokenService;
	
	public ClientSecurityConfigurations(AdminServerProperties adminServerProperties) {
		super(adminServerProperties);
	}
	
	@Bean
	public HttpHeadersProvider customHttpHeadersProvider() {
		return instance -> {
			System.out.println(instance);
			HttpHeaders httpHeaders = new HttpHeaders();
	        httpHeaders.add("Authorization", "Bearer " + tokenService.gerarToken());
	        return httpHeaders;
		};
	}

}

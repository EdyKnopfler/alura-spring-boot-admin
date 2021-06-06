package br.com.pip.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import de.codecentric.boot.admin.server.config.EnableAdminServer;

// https://codecentric.github.io/spring-boot-admin/2.3.1/

@Configuration
@EnableAutoConfiguration
@EnableAdminServer

// https://cursos.alura.com.br/forum/topico-sprint-boot-admin-com-a-regra-de-seguranca-86290
@Import({SecurityConfigurations.class, ClientSecurityConfigurations.class, TokenService.class})
public class UsandoSpringBootAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsandoSpringBootAdminApplication.class, args);
	}

}

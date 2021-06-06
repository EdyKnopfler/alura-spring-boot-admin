package br.com.pip.admin;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenService {
	
	// Obtendo do application.properties!
	@Value("${forum.jwt.secret}")
	private String secret; 
	
	@Value("${forum.jwt.expiration}")
	private String expiracao;

	@Value("${forum.userid}")
	private String id;

	public String gerarToken() {
		Date agora = new Date();
		Date dataExpiracao = new Date(agora.getTime() + Long.parseLong(expiracao));
		return Jwts
				.builder()
					.setIssuer("API do Fórum do Píp")
					.setSubject(id)
					.setIssuedAt(agora)
					.setExpiration(dataExpiracao)
					.signWith(SignatureAlgorithm.HS256, secret)
					.compact();
	}

}

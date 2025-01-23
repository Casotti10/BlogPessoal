package com.generation.blogpessoal.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService { //Classe criada para criar e extrair as informaçoes do token JWT 
	//ele valida a autenticidade dos tokens e verifica sua expiraçao

	// Chave secreta utilizada para assinar e verificar a autenticidade dos tokens JWT.
	public static final String SECRET = "gTB4uOWBLXWWyReqb9Y7JbBed8Kad6p5QOCDXZk+UwgeVgFJffxox1g0SSlaI3h9";

	// Método para gerar a chave de assinatura a partir da chave secreta (decodificando em base64).
	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	 // Método que extrai todas as reivindicações (claims) do token JWT.
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSignKey()).build()
				.parseClaimsJws(token).getBody();
	}

	 // Método genérico que extrai um dado específico do token, usando uma função de extração.
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	  // Método que extrai o nome de usuário do token JWT.
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	 
	// Método que extrai a data de expiração do token JWT.
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	
	 // Método que verifica se o token JWT está expirado.
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	 // Método que valida o token JWT, verificando o nome de usuário e se o token não está expirado.
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	// Método que cria um novo token JWT, recebendo um mapa de claims e o nome de usuário.
	private String createToken(Map<String, Object> claims, String userName) {
		return Jwts.builder()
					.setClaims(claims)
					.setSubject(userName)
					.setIssuedAt(new Date(System.currentTimeMillis()))
					.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
					.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}

	// Método que gera um token JWT para um usuário, sem claims adicionais.
	public String generateToken(String userName) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userName);
	}

}
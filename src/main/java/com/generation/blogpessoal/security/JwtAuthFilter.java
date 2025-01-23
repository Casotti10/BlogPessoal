package com.generation.blogpessoal.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


//Define esta classe como um componente gerenciado pelo Spring.
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	 // Injeta o serviço responsável por manipular e validar tokens JWT.
	@Autowired
    private JwtService jwtService;

	 // Injeta o serviço que carrega os detalhes do usuário.
	@Autowired
    private UserDetailsServiceImpl userDetailsService;

	// Obtém o cabeçalho "Authorization" da requisição.
	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
    
        try{
            if (authHeader != null && authHeader.startsWith("Bearer ")) { // Verifica se o cabeçalho contém um token JWT válido (começando com "Bearer ").
                token = authHeader.substring(7);// Remove o prefixo "Bearer " do token.
                username = jwtService.extractUsername(token); // Extrai o nome do usuário do token.
            }

           // Verifica se o nome de usuário foi extraído e se o contexto de segurança está vazio.
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { //Verifica se o nome de usuário foi extraído e se o contexto de segurança está vazio.
                UserDetails userDetails = userDetailsService.loadUserByUsername(username); //carrega os detalhes do usuario
                    
             // Valida o token usando os detalhes do usuário.
                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    // Associa os detalhes da requisição à autenticação.
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);// Configura o contexto de segurança com a autenticação do usuário
                }
            
            }
            filterChain.doFilter(request, response); // Continua o processamento da requisição.

        }
     // Em caso de erro com o token JWT, retorna um status HTTP 403 (FORBIDDEN).
        catch(ExpiredJwtException | UnsupportedJwtException | MalformedJwtException 
                | SignatureException | ResponseStatusException e){
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }
    }
}
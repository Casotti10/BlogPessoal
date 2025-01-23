package com.generation.blogpessoal.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration //Configuraçoes Spring Security
@EnableWebSecurity //Ativa a segurança da aplicação usando Spring Security.
public class BasicSecurityConfig { 
	 
/* 
Basic Security: Configura a segurança do sistema usando Spring Security.
-Define como as requisições serão autenticadas e autorizadas.
-Configura o uso de autenticação baseada em JWT (JSON Web Token).
 */

	@Autowired //Spring injeta automaticamente componentes da JwtAuthFilter
	private JwtAuthFilter authFilter;

	 //Configura um serviço de detalhes do usuário personalizado, que é responsável por carregar os dados 
	 //do usuário (e-mail, senha e permissões) do banco de dados.
	@Bean
	UserDetailsService userDetailsService() {   

		return new UserDetailsServiceImpl();
	}

	//Decodificador de senhas (BCrypt) para armazenar e comparar senhas de formas seguras
	@Bean  
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean  //Configura o provedor de autenticaçao que usar o UserDetailsService e o PasswordEnconder para verificar
	// as credenciais do usuario
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean //Gerencia o processo de autenticaçao com base nas configuraçoes fornecidas
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean //
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception { // Configura a cadeia de filtros de segurança da aplicação.

		http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Define que a aplicação não manterá sessões (Stateless).
				.csrf(csrf -> csrf.disable()).cors(withDefaults());

		http.authorizeHttpRequests((auth) -> auth  // Define as regras de autorização para as requisições:
				.requestMatchers("/usuarios/logar").permitAll() //Libera acesso ao endpoint de login.
				.requestMatchers("/usuarios/cadastrar").permitAll()
				.requestMatchers("/error/**").permitAll()
				.requestMatchers(HttpMethod.OPTIONS).permitAll()  // Permite requisições OPTIONS (CORS preflight).
				.anyRequest().authenticated()) // Todas as outras requisições exigem autenticação.
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class) // Adiciona o filtro personalizado JwtAuthFilter antes do filtro padrão de autenticação.
				.httpBasic(withDefaults());

		return http.build();

	}

}
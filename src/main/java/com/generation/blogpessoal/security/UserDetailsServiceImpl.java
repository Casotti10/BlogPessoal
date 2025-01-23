package com.generation.blogpessoal.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	//Implementa a interface UserDetailsService do Spring Security, que é responsável por carregar os detalhes do usuário 
	// a partir do nome de usuário.
	
	@Autowired
	private UsuarioRepository usuarioRepository; //Injeta automaticamente a instância do repositório de usuários

	// Implementação do método loadUserByUsername que é usado pelo Spring Security 
	//para carregar os detalhes do usuário
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        // Busca o usuário no banco de dados pelo nome de usuário (userName)
		Optional<Usuario> usuario = usuarioRepository.findByUsuario(userName);

		if (usuario.isPresent())
			return new UserDetailsImpl(usuario.get()); 
		// Se o usuário for encontrado, cria e retorna um UserDetailsImpl com os dados do usuário
		else
			throw new ResponseStatusException(HttpStatus.FORBIDDEN); 
	    // Caso o usuário não seja encontrado, lança uma exceção de status FORBIDDEN (403)
			
	}
}
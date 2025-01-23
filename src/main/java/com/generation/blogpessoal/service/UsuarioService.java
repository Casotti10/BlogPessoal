package com.generation.blogpessoal.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.security.JwtService;

@Service // Define esta classe como um serviço Spring que pode ser injetado em outras
			// partes da aplicação
public class UsuarioService {

	@Autowired // O Spring vai injetar a dependência do repositório de usuários automaticamente
	private UsuarioRepository usuarioRepository;

	@Autowired // O Spring vai injetar o serviço de JWT automaticamente
	private JwtService jwtService;

	@Autowired // O Spring vai injetar o AuthenticationManager automaticamente
	private AuthenticationManager authenticationManager;

	// Método para cadastrar um novo usuário
	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {

		// Verifica se já existe um usuário com o mesmo nome de usuário
		if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent())
			return Optional.empty(); // Retorna vazio caso o nome de usuário já exista

		// Criptografa a senha antes de salvar o usuário
		usuario.setSenha(criptografarSenha(usuario.getSenha()));

		// Salva o usuário no banco de dados e retorna o usuário salvo
		return Optional.of(usuarioRepository.save(usuario));
	}

	// Método para atualizar um usuário existente
	public Optional<Usuario> atualizarUsuario(Usuario usuario) {

		// Verifica se o usuário existe no banco
		if (usuarioRepository.findById(usuario.getId()).isPresent()) {

			// Verifica se outro usuário já está usando o mesmo nome de usuário
			Optional<Usuario> buscaUsuario = usuarioRepository.findByUsuario(usuario.getUsuario());
			if ((buscaUsuario.isPresent()) && (buscaUsuario.get().getId() != usuario.getId()))
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe!", null); 																						// us
			// Lança erro se nome de usuário já estiver emu uso
			
			// Criptografa a nova senha antes de atualizar o usuário
			usuario.setSenha(criptografarSenha(usuario.getSenha()));

			// Atualiza o usuário no banco de dados e retorna o usuário atualizado
			return Optional.ofNullable(usuarioRepository.save(usuario));
		}

		return Optional.empty(); // Retorna vazio se o usuário não for encontrado
	}

	// Método para autenticar um usuário
	public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {

		// Cria um objeto de autenticação com as credenciais do usuário (nome de usuário
		// e senha)
		var credenciais = new UsernamePasswordAuthenticationToken(usuarioLogin.get().getUsuario(),
				usuarioLogin.get().getSenha());

		// Tenta autenticar o usuário
		Authentication authentication = authenticationManager.authenticate(credenciais);

		// Se a autenticação foi bem-sucedida
		if (authentication.isAuthenticated()) {

			// Busca o usuário no banco de dados com o nome de usuário informado
			Optional<Usuario> usuario = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());

			// Se o usuário for encontrado
			if (usuario.isPresent()) {

				// Preenche o objeto UsuarioLogin com os dados do usuário (nome, foto, token,
				// etc.)
				usuarioLogin.get().setId(usuario.get().getId());
				usuarioLogin.get().setNome(usuario.get().getNome());
				usuarioLogin.get().setFoto(usuario.get().getFoto());
				usuarioLogin.get().setToken(gerarToken(usuarioLogin.get().getUsuario())); // Gera o token JWT
				usuarioLogin.get().setSenha(""); // Limpa a senha antes de retornar (não deve ser enviada)

				// Retorna o objeto usuarioLogin preenchido
				return usuarioLogin;
			}
		}

		return Optional.empty(); // Retorna vazio se a autenticação falhar
	}

	// Método para criptografar a senha com o algoritmo BCrypt
	private String criptografarSenha(String senha) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(senha); // Retorna a senha criptografada
	}

	// Método para gerar o token JWT para o usuário
	private String gerarToken(String usuario) {
		return "Bearer " + jwtService.generateToken(usuario); // Adiciona "Bearer" ao token gerado
	}

}

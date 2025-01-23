package com.generation.blogpessoal.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.generation.blogpessoal.model.Usuario;

public class UserDetailsImpl implements UserDetails {   
	//Implementa a interface UserDetails, que é usada pelo Spring Security para representar as 
	//informações do usuário no sistema.

	//// Identificador único da versão da classe para serialização
	private static final long serialVersionUID = 1L;

	private String userName;
	private String password;
	private List<GrantedAuthority> authorities; //// Lista de permissões (roles) do usuário

	public UserDetailsImpl(Usuario user) {  //Construtor que inicializa o UserDetailsImpl com base nos dados de um ob
		this.userName = user.getUsuario(); // Obtém o nome de usuário do objeto Usuario
		this.password = user.getSenha(); //// Obtém a senha do objeto Usuario
	}

	public UserDetailsImpl() { //// Construtor vazio, necessário para o Spring Security
	}

	// // Retorna a lista de permissões do usuário
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		return authorities;
	}

	@Override
	public String getPassword() {

		return password;
	}

	@Override
	public String getUsername() {

		return userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
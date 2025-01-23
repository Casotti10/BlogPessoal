package com.generation.blogpessoal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.blogpessoal.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	public Optional<Usuario> findByUsuario(String usuario); 

} 

/*  
A UsuarioRepository é responsável por gerenciar operações de persistência para a entidade 
Usuario no banco de dados, como salvar, buscar, atualizar e excluir registros.
Essa interface utiliza a biblioteca Spring Data JPA, que simplifica o acesso a dados e  
como salvar, buscar, atualizar e excluir registros.
*/ 

/* 
findByUsuario:
-Busca um registro da tabela tb_usuarios com base no valor do atributo usuario.
-String usuario: Representa o e-mail ou nome de usuário a ser buscado.  

Optional<Usuario>:
Retorna um objeto Optional, que pode conter um Usuario ou estar vazio.
Isso ajuda a evitar problemas com valores null e permite tratar cenários onde 
o usuário não foi encontrado.
*/
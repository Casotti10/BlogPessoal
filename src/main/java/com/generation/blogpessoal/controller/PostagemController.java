package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;

import jakarta.validation.Valid;


@RestController        //Define ao Spring que essa classe é uma controller
@RequestMapping("/postagens") //Define qual endpoint vai ser tratado por essa classe 
@CrossOrigin(origins = "*", allowedHeaders = "*") //Libera o acesso a qualquer front 
public class PostagemController{
	
	@Autowired //O Spring da autonomia para a Interface invocar os metodos 
	private PostagemRepository postagemRepository;
	
	@GetMapping // mapea solicitações HTTP do tipo GET para um método específico em um controlado
	public ResponseEntity<List<Postagem>> getAll(){  //Indica que esse metodo e chamado em Verbos/Metodos
		return ResponseEntity.ok(postagemRepository.findAll()); //SELECT * FROM tb_postagens
	}
	
	@GetMapping("/{id}")  //Define que este método será chamado quando o usuário acessar algo como /algum-id na URL
	public ResponseEntity<Postagem> getById(@PathVariable Long id){  //Faz o Spring pegar o id da URL e colocá-lo no parâmetro id do método
		return postagemRepository.findById(id) //guardando coisas no optional(map)
				.map(reposta -> ResponseEntity.ok(reposta))   //Se encontrar a postagem no BD o map pega a resposta e retorna dentro de um ResponseEntity com um Status 200OK
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()); //Se nao encontrar retorna o 404 NOT_FOUND
	} 
	@GetMapping("titulo/{titulo}") //Define que este método será chamado quando o usuário acessar algo como /algum-id na URL
	public ResponseEntity<List<Postagem>> getByTitulo(@PathVariable String titulo){  //Retorna uma reposta HTTP com uma lista cujo o titulo contem o valor do parametro 'titulo'
		return ResponseEntity.ok (postagemRepository.findAllByTituloContainingIgnoreCase(titulo)); //Busca no BD usando tudo que tem na tabela titulo em forma de lista 
	}
	
	@PostMapping 
	public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem postagem){ //Valida as requisiçoes conforme as regras defifinidas na Model postagem(Not Null)
		return ResponseEntity.status(HttpStatus.CREATED) //Responde as resquisiçoes HTTP 
				.body(postagemRepository.save(postagem));  
	} 
	
	@PutMapping 
	public ResponseEntity<Postagem> put (@Valid @RequestBody Postagem postagem){ // Se a postagem for encontrada (método map executado):
		return postagemRepository.findById(postagem.getId()) // Se a postagem for encontrada (método map executado):
				.map(resposta -> ResponseEntity.status(HttpStatus.OK)// Atualiza a postagem no repositório e retorna uma resposta com o status 200 (OK)
						.body(postagemRepository.save(postagem))) 
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // Se a postagem não for encontrada, retorna o status 404 (NOT FOUND).
	} 
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) { 
		Optional<Postagem> postagem = postagemRepository.findById(id); 
	
	if(postagem.isEmpty())
		throw new ResponseStatusException(HttpStatus.NOT_FOUND);
	
	postagemRepository.deleteById(id);
}

}

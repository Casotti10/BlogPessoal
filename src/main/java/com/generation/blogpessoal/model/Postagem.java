package com.generation.blogpessoal.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity						//Indica que a classe é uma tabela
@Table(name = "tb_postagens") //Indica o nome da tabela no bd 
public class Postagem {

	@Id 					//Chave primaria(PRIMARY KEY)
	@GeneratedValue(strategy = GenerationType.IDENTITY) //Auto Incremento
	private Long id;
	
	@NotBlank(message = "Esse campo é orbigatório.") // Not Null
	@Size(min = 5, max = 100, message = "Esse campo é orbigatório.")
	private String titulo;
	
	@NotNull(message = "Esse campo é orbigatório.") // Not Null
	@Size(min = 10, max = 100, message = "Esse campo é orbigatório.")
	private String texto;
	
	@UpdateTimestamp
	private LocalDateTime data; 
	
	@ManyToOne //Indica que há um relacionamento de muitos-para-um (N:1) entre a classe atual (Postagem) e a classe relacionada (Tema).
	@JsonIgnoreProperties("postagem") //Evita ciclos infinitos e problemas ao transformar objetos Java em JSON em relações bidirecionais.
	private Tema tema;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	public LocalDateTime getData() {
		return data;
	}
	public void setData(LocalDateTime data) {
		this.data = data;
	}
	public Tema getTema() {
		return tema;
	}
	public void setTema(Tema tema) {
		this.tema = tema;
	}

	
}

package curso.api.rest.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Arrecadacao_Dia implements Serializable{

	// TESTANDO ALTERAÇÃO
	private static final long serialVersionUID = 6302380438833466606L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String congregacao;
	
	private String nome;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private LocalDate data;
	
	private Long dizimo;
	
	private Long oferta;
	
	private Long doacao;
	
	private Long outro;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public String getCongregacao() {
		return congregacao;
	}

	public void setCongregacao(String congregacao) {
		this.congregacao = congregacao;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	
	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public Long getDizimo() {
		return dizimo;
	}

	public void setDizimo(Long dizimo) {
		this.dizimo = dizimo;
	}

	public Long getOferta() {
		return oferta;
	}

	public void setOferta(Long oferta) {
		this.oferta = oferta;
	}

	public Long getDoacao() {
		return doacao;
	}

	public void setDoacao(Long doacao) {
		this.doacao = doacao;
	}

	public Long getOutro() {
		return outro;
	}

	public void setOutro(Long outro) {
		this.outro = outro;
	}
	
	
}

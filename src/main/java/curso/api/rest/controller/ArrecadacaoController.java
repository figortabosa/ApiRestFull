package curso.api.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.model.Arrecadacao_Dia;
import curso.api.rest.repository.ArrecadacaoRepository;

@RestController
@RequestMapping(value = "/arrecadacao")
public class ArrecadacaoController {
	
	@Autowired
	private ArrecadacaoRepository arrecadacaoRepository;
	
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Arrecadacao_Dia> salvar(@RequestBody Arrecadacao_Dia arrecadacao_Dia) {
		
 		Arrecadacao_Dia arrecadacao = arrecadacaoRepository.save(arrecadacao_Dia);
		return new ResponseEntity<Arrecadacao_Dia>(arrecadacao, HttpStatus.OK);
		
	}

}

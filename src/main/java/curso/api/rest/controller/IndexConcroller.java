package curso.api.rest.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import ch.qos.logback.classic.pattern.Util;
import curso.api.rest.model.Telefone;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.TelefoneRepository;
import curso.api.rest.repository.UsuarioRepository;
import curso.api.rest.service.ImplementacaoUserDetailsService;

@RestController
@RequestMapping(value = "/usuario")
public class IndexConcroller {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Usuario> init(@PathVariable (value = "id") Long id) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	@GetMapping(value = "/", produces = "application/json" )
	@CachePut("cacheUsuarios")
	public ResponseEntity<Page<Usuario>> usuario() {
			
		PageRequest page = PageRequest.of(0, 5,Sort.by("nome"));
		
		Page<Usuario> list = usuarioRepository.findAll(page);		
			//List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();
			
			return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
		}
	
	
	@GetMapping(value = "/page/{pagina}", produces = "application/json" )
	@CachePut("cacheUsuarios")
	public ResponseEntity<Page<Usuario>> usuarioPagina(@PathVariable(value = "pagina") int pagina) {
			
		PageRequest page = PageRequest.of(pagina, 5,Sort.by("nome"));
		
		Page<Usuario> list = usuarioRepository.findAll(page);		
			//List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();
			
			return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
		}
	
	@GetMapping(value = "/usuarioPorNome/{nome}", produces = "application/json")
	@CachePut("cacheUsuarios")
	public ResponseEntity<Page<Usuario>> usuarioPorNome(@PathVariable (value = "nome") String nome){
		
		PageRequest pageRequeste = null;
		Page<Usuario> list = null;
		
		if(nome.equalsIgnoreCase("undefined") || nome == null || (nome != null && nome.trim().isEmpty())) {
			pageRequeste = PageRequest.of(0, 5, Sort.by("nome"));
			list = usuarioRepository.findAll(pageRequeste);
		}else {
			pageRequeste = PageRequest.of(0, 5, Sort.by("nome"));
			list = usuarioRepository.findUserByNamePage(nome,pageRequeste);
		}
		
		
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
		
	}
	
	@GetMapping(value = "/{id}/nome/{nome}", produces = "application/json")
	public ResponseEntity<Usuario> consultaComMaisDeUmParametro(@PathVariable (value = "id") Long id
																, @PathVariable(value = "nome") String nome) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
		
		for (int pos = 0; pos < usuario.getTelefones().size(); pos ++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		implementacaoUserDetailsService.insereAcessoPadrao(usuarioSalvo.getId());
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}
	
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualisar(@RequestBody Usuario usuario) {
		
		for (int pos = 0; pos < usuario.getTelefones().size(); pos ++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		Usuario usuarioTemporario = usuarioRepository.findById(usuario.getId()).get();
		
		if (!usuarioTemporario.getSenha().equals(usuario.getSenha())) {
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
		}
		
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String deletar(@PathVariable("id") Long id) {
		usuarioRepository.deleteById(id);
		
		return "ok";
	}
	
	@DeleteMapping(value = "/removerTelefone/{id}", produces = "application/text")
	public String deleteTelefone(@PathVariable("id") Long id) {
		
		telefoneRepository.deleteById(id);
		
		return "ok";
	}
	
	@PostMapping(value = "/telefone",produces = "application/json" )
	public ResponseEntity<Telefone> gravarTelefone(@RequestBody Telefone telefone) {
		
		Telefone telefoneSalvo = telefoneRepository.save(telefone);
		return new ResponseEntity<Telefone>(telefoneSalvo, HttpStatus.OK);
	}
	
	@GetMapping(value = "/testeApi/{idUsuario}",produces = "application/json")
	public void testeApi(@PathVariable("idUsuario") Long idUsuario) {
		try {
			
			
			URL url = new URL("https://osb.unimedfortaleza.com.br/cd/perfil/risco?codBeneficiario=1509258&safra=202110");
			HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
			
			if (conexao.getResponseCode() != 200)
                throw new RuntimeException("HTTP error code : " + conexao.getResponseCode());
			
			
			BufferedReader resposta = new BufferedReader(new InputStreamReader((conexao.getInputStream())));
			String jsonEmString = "";
	        while ((jsonEmString = resposta.readLine()) != null) {
	            jsonEmString += resposta;
	        }
			
			//String pfRisco = "";
			//StringBuilder jsonCep = new StringBuilder();
			
			//while ((pfRisco = br.readLine()) != null) {
			//	jsonCep.append(pfRisco);
			//}
			
			System.out.println(jsonEmString);
			//Endereco gson = new Gson().fromJson(jsonCep.toString(), Endereco.class);
			//aluno.getEndereco().setCep(gson.getCep());
			//aluno.getEndereco().setLogradouro(gson.getLogradouro());
			//aluno.getEndereco().setComplemento(gson.getComplemento());
			//aluno.getEndereco().setLocalidade(gson.getLocalidade());
			//aluno.getEndereco().setUf(gson.getUf());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

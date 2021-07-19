package curso.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import curso.api.rest.ApplicationContextLoad;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {

	/* Configura o tempo do token esse tera validade de 2 dias*/
	private static final long EXPIRATION_TIME= 172800000;
	
	/*Uma senha unica para compor a autenticação e ajudar a segurança*/
	private static final String SECRET = "senhaExtremamenteSecreta";
	
	/*Prefixo padrão de token*/
	private static final String TOKEN_PREFIX = "bearer";
	
	private static final String HEADER_STRING = "Authorization";
	
	public void addAuthentication(HttpServletResponse response, String username)throws IOException {
		
		/*Montagem do tokem*/
		String JWT = Jwts.builder().setSubject(username)
					 .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
					 .signWith(SignatureAlgorithm.HS512, SECRET).compact();
		
		/*jUNTA O TOKEN COM O PREFIXO*/
		String token = TOKEN_PREFIX + " " + JWT; /*Bearer 354er6263534e4e463wwww*/
		
		/*Adiciona no cabeçalho*/
		response.addHeader(HEADER_STRING, token); /*Authorization: Bearer 344dfww234wewrsss*/
		
		/* Escreve token como resposta no corpo do http*/
		response.getWriter().write("{\"Authorization\": \""+token+"\"}");
	}
	
	/*Retorna o usuario validado com o token ou caso não seja valido retorna null*/
	
	public Authentication getAuthentication(HttpServletRequest request) {
		
		/*Pegar o token enviado no cabeçalho http*/
		String token = request.getHeader(HEADER_STRING);
		
		if (token != null) {
			/*Faz a validação do token do usuario na requisição*/
			String user = Jwts.parser().setSigningKey(SECRET) /*Neste ponto o token virá assim: Bearer 354er6263534e4e463wwww*/
					.parseClaimsJws(token.replace(TOKEN_PREFIX, "")) /*Neste ponto fica assim:354er6263534e4e463wwww*/
					.getBody().getSubject(); /*Fica assim: Ex:João Silva*/
			
			if (user != null) {
				
				Usuario usuario = ApplicationContextLoad.getApplicationContext()
						.getBean(UsuarioRepository.class).findUserLogin(user);
				
			if (usuario != null) {
				return new UsernamePasswordAuthenticationToken(usuario.getLogin(),
																usuario.getSenha(),
																usuario.getAuthorities());
			}
				
			}else {
				return null;
			}
			
		}else {
			return null; /*Não autorizado*/
		}
		return null;
	}
}

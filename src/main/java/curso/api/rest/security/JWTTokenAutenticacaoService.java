package curso.api.rest.security;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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
	
	public void addAuthentication(HttpServletResponse response, String username)throws Exception {
		
		/*Montagem do tokem*/
		String JWT = Jwts.builder().setSubject(username)
					 .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
					 .signWith(SignatureAlgorithm.HS512, SECRET).compact();
		
		/*jUNTA O TOKEN COM O PREFIXO*/
		String token = TOKEN_PREFIX + " " + JWT; /*Bearer 354er6263534e4e463wwww*/
		
		/*Adiciona no cabeçalho*/
		response.addHeader(HEADER_STRING, token); /*Authorization: Bearer 344dfww234wewrsss*/
		
	}
}

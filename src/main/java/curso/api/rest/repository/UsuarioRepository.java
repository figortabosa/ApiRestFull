package curso.api.rest.repository;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import curso.api.rest.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

	@Query("select u from Usuario u where u.login = ?1")
	Usuario findUserLogin(String login);
	
	@Query("select u from Usuario u where u.nome like %?1%")
	List<Usuario> findUserByNome(String nome);
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "update usuario set token = ?1 where login = ?2")
	void atualizaUserToken(String token, String login);
	
	@Query(value="SELECT constraint_name FROM information_schema.constraint_column_usage \r\n" + 
			"WHERE table_name = 'usuarios_role' and column_name = 'role_id'\r\n" + 
			"and constraint_name <> 'unique_role_user';", nativeQuery = true)
	String consultaConstrainteRole();
	
	@Modifying
	@Query(value= "alter table usuarios_role DROP CONSTRAINT ?1;", nativeQuery = true)
	void removerConstraintRole(String constraint);
	
	@Transactional
	@Modifying
	@Query(value= "INSERT INTO usuarios_role (usuario_id, role_id)\r\n" + 
			"values(?1, (SELECT id FROM role where nome_role = 'ROLE_USER'));", nativeQuery = true)
	void insereAcessoRolePadrao(Long idUser);

	default Page<Usuario> findUserByNamePage(String nome, PageRequest pageRequeste){
		
		Usuario usuario = new Usuario();
		usuario.setNome(nome);
		
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
				.withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		
		Example<Usuario> example = Example.of(usuario, exampleMatcher);
		Page<Usuario> retorno = findAll(example,pageRequeste);
		return retorno;
	}
	

}

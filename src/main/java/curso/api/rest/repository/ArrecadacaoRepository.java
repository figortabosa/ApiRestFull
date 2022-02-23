package curso.api.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import curso.api.rest.model.Arrecadacao_Dia;

@Repository
public interface ArrecadacaoRepository extends JpaRepository<Arrecadacao_Dia, Long>{

}

package br.com.happydo.repository;

import br.com.happydo.model.TarefaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarefaDataRepository extends JpaRepository<TarefaData, Long> {
}

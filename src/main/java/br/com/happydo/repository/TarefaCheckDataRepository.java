package br.com.happydo.repository;

import br.com.happydo.model.TarefaCheckData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarefaCheckDataRepository extends JpaRepository<TarefaCheckData, Long> {
}

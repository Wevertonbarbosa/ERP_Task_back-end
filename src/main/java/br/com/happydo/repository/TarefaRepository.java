package br.com.happydo.repository;

import br.com.happydo.model.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    // Busca tarefas delegadas para um usuário específico
    List<Tarefa> findByResponsavel_UsuarioId(Long responsavelUsuarioId);
    // Busca tarefas criadas por um mentor ou delegadas para ele

    List<Tarefa> findByCriador_UsuarioIdAndResponsavel_UsuarioIdNot(Long criadorUsuarioId, Long responsavelUsuarioId);


}

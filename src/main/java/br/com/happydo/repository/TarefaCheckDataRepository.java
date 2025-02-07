package br.com.happydo.repository;

import br.com.happydo.model.TarefaCheckData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarefaCheckDataRepository extends JpaRepository<TarefaCheckData, Long> {
    @Query("SELECT t FROM TarefaCheckData t WHERE t.usuario_id.usuarioId = :usuarioId AND t.sinalizadaUsuario = true")
    List<TarefaCheckData> findTarefasSinalizadasPorUsuario(@Param("usuarioId") Long usuarioId);

}

package br.com.happydo.repository;

import br.com.happydo.model.Mesada;
import br.com.happydo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MesadaRepository extends JpaRepository<Mesada, Long> {
    List<Mesada> findByUsuarioUsuarioId(Long usuarioId);

    Optional<Mesada> findTopByUsuarioOrderByDataRecebimentoDesc(Usuario usuario);


    @Query("SELECT m FROM Mesada m WHERE m.usuario.usuarioId = :usuarioId AND YEAR(m.dataRecebimento) = :ano AND MONTH(m.dataRecebimento) = :mes")
    Optional<Mesada> findByUsuarioAndMes(@Param("usuarioId") Long usuarioId,
                                         @Param("ano") int ano,
                                         @Param("mes") int mes);

}

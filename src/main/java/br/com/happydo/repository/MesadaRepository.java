package br.com.happydo.repository;

import br.com.happydo.model.Mesada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MesadaRepository extends JpaRepository<Mesada, Long> {
    List<Mesada> findByUsuarioUsuarioId(Long usuarioId);
}

package br.com.happydo.repository;

import br.com.happydo.model.Gasto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {
    List<Gasto> findByUsuarioUsuarioId(Long usuarioId);
}

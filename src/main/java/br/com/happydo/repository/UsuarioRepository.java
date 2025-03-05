package br.com.happydo.repository;

import br.com.happydo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);


    @Query("SELECT u FROM Usuario u WHERE u.adminResponsavel.usuarioId = :adminId")
    List<Usuario> findMentoradosByAdmin(@Param("adminId") Long adminId);

}

package br.com.happydo.service;

import br.com.happydo.dto.LoginUsuarioDTO;
import br.com.happydo.dto.LoginUsuarioMentoradoDTO;
import br.com.happydo.dto.UsuarioExibitionDTO;
import br.com.happydo.exception.UsuarioNaoEncontradoException;
import br.com.happydo.exception.UsuarioSemAutorizacaoException;
import br.com.happydo.model.Usuario;
import br.com.happydo.model.UsuarioRole;
import br.com.happydo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public UsuarioExibitionDTO autenticarUsuario(LoginUsuarioDTO loginDTO) {

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(loginDTO.email());
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();

            if (usuario.getSenha().equals(loginDTO.senha())) {

                return new UsuarioExibitionDTO(usuario);
            } else {
                throw new IllegalArgumentException("Senha incorreta!");
            }
        } else {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado.");
        }
    }

    public UsuarioExibitionDTO loginUsuarioUser(LoginUsuarioMentoradoDTO loginUsuarioMentoradoDTO) {

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(loginUsuarioMentoradoDTO.email());

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();

            if (usuario.getRole().equals(UsuarioRole.USER)) {
                return new UsuarioExibitionDTO(usuario);
            } else {
                throw new UsuarioSemAutorizacaoException("Usuário não autorizado para login sem senha.");
            }
        } else {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado.");
        }

    }


}

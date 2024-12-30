package br.com.happydo.service;

import br.com.happydo.dto.LoginUsuarioDTO;
import br.com.happydo.dto.UsuarioExibitionDTO;
import br.com.happydo.exception.UsuarioNaoEncontradoException;
import br.com.happydo.model.Usuario;
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


}

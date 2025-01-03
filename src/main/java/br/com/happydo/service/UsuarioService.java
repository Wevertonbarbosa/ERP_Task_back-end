package br.com.happydo.service;

import br.com.happydo.dto.UsuarioCadastroDTO;
import br.com.happydo.dto.UsuarioExibitionDTO;
import br.com.happydo.exception.ConflitoEmailException;
import br.com.happydo.exception.UsuarioNaoEncontradoException;
import br.com.happydo.model.Usuario;
import br.com.happydo.repository.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailUsuarioService emailUsuarioService;


    public UsuarioExibitionDTO criarUsuario(UsuarioCadastroDTO usuarioCadastroDTO) {
        // Verificar se o email já existe
        if (usuarioRepository.existsByEmail(usuarioCadastroDTO.email())) {
            throw new ConflitoEmailException("Já existe um usuário com este email.");
        }

        Usuario usuario = new Usuario();
        BeanUtils.copyProperties(usuarioCadastroDTO, usuario);
        Usuario usuarioSalvo = usuarioRepository.save(usuario);


        return new UsuarioExibitionDTO(usuarioSalvo);
    }

    // Buscar usuário por ID
    public UsuarioExibitionDTO buscarUsuarioPorId(Long id) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        if (usuarioOptional.isPresent()) {
            return new UsuarioExibitionDTO(usuarioOptional.get());
        } else {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado no banco de dados!");
        }
    }

    // Listar todos os usuários
    public List<UsuarioExibitionDTO> listarTodosUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioExibitionDTO::new)
                .toList();
    }

    // Deletar usuário com validação
    public void deleteUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new UsuarioNaoEncontradoException("Usuário com ID " + id + " não encontrado.");
        }
        usuarioRepository.deleteById(id);
    }

    // Atualizar usuário
    public UsuarioExibitionDTO atualizarUsuario(Long id, UsuarioCadastroDTO usuarioCadastroDTO) {
        Usuario usuario = new Usuario();
        BeanUtils.copyProperties(usuarioCadastroDTO, usuario);
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        if (usuarioOptional.isPresent()) {
            usuario.setUsuarioId(id);
            return new UsuarioExibitionDTO(usuarioRepository.save(usuario));
        } else {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado.");
        }
    }







}

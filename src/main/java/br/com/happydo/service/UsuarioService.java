package br.com.happydo.service;

import br.com.happydo.dto.AtualizarSaldoDTO;
import br.com.happydo.dto.UsuarioCadastroDTO;
import br.com.happydo.dto.UsuarioExibitionDTO;
import br.com.happydo.dto.UsuarioTarefasExibitionDTO;
import br.com.happydo.exception.ConflitoEmailException;
import br.com.happydo.exception.UsuarioNaoEncontradoException;
import br.com.happydo.exception.UsuarioNaoPodeSerExcluidoException;
import br.com.happydo.model.Tarefa;
import br.com.happydo.model.Usuario;
import br.com.happydo.model.UsuarioRole;
import br.com.happydo.repository.TarefaRepository;
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
    private TarefaRepository tarefaRepository;

    @Autowired
    private MesadaService mesadaService;

    @Autowired
    private EmailUsuarioService emailUsuarioService;


    // Listar mentorados com saldo total
    public List<UsuarioExibitionDTO> listarMentorados(Long adminId) {
        List<Usuario> mentorados = usuarioRepository.findMentoradosByAdmin(adminId);
        return mentorados.stream()
                .map(UsuarioExibitionDTO::new)
                .toList();
    }


    public UsuarioExibitionDTO criarUsuario(UsuarioCadastroDTO usuarioCadastroDTO, Long adminId) {
        // Verificar se o email já existe
        if (usuarioRepository.existsByEmail(usuarioCadastroDTO.email())) {
            throw new ConflitoEmailException("Já existe um usuário com este email.");
        }

        // Criar novo usuário
        Usuario usuario = new Usuario();
        BeanUtils.copyProperties(usuarioCadastroDTO, usuario);

        // Se for um usuário comum (USER), vincular ao admin que o criou
        if (usuarioCadastroDTO.role() == UsuarioRole.USER) {
            Usuario admin = usuarioRepository.findById(adminId)
                    .orElseThrow(() -> new UsuarioNaoEncontradoException("Admin não encontrado."));
            usuario.setAdminResponsavel(admin);
        }

        usuario.setSaldoTotal(0.0);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return new UsuarioExibitionDTO(usuarioSalvo);
    }


    // Buscar usuário por ID
    public UsuarioExibitionDTO buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));
        return new UsuarioExibitionDTO(usuario);
    }


    // Listar todos os usuários
    public List<UsuarioExibitionDTO> listarTodosUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioExibitionDTO::new)
                .toList();
    }

    public UsuarioTarefasExibitionDTO tarefasStatus(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        // Garantindo que os valores não sejam nulos
        Integer concluidas = (usuario.getTarefasConcluidas() != null) ? usuario.getTarefasConcluidas() : 0;
        Integer pendentes = (usuario.getTarefasPendentes() != null) ? usuario.getTarefasPendentes() : 0;

        return new UsuarioTarefasExibitionDTO(concluidas, pendentes);

    }


    // Deletar usuário com validação
    public void deleteUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new UsuarioNaoEncontradoException("Usuário com ID " + id + " não encontrado.");
        }

        List<Tarefa> tarefas = tarefaRepository.findByResponsavel_UsuarioId(id);

        if (!tarefas.isEmpty()) {
            throw new UsuarioNaoPodeSerExcluidoException("Usuário não pode ser excluído porque possui tarefas ativas.");
        }


        usuarioRepository.deleteById(id);
    }

    // Atualizar usuário (preservando saldoTotal)
    public UsuarioExibitionDTO atualizarUsuario(Long id, UsuarioCadastroDTO usuarioCadastroDTO) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        if (usuarioOptional.isPresent()) {
            Usuario usuarioExistente = usuarioOptional.get();

            // Mantém o admin responsável e o saldoTotal existente
            Usuario adminResponsavel = usuarioExistente.getAdminResponsavel();
            Double saldoTotalExistente = usuarioExistente.getSaldoTotal();

            BeanUtils.copyProperties(usuarioCadastroDTO, usuarioExistente, "usuarioId", "adminResponsavel", "saldoTotal");

            usuarioExistente.setUsuarioId(id);
            usuarioExistente.setAdminResponsavel(adminResponsavel);
            usuarioExistente.setSaldoTotal(saldoTotalExistente); // Garante que o saldo total não seja sobrescrito

            return new UsuarioExibitionDTO(usuarioRepository.save(usuarioExistente));
        } else {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado.");
        }
    }

    public UsuarioExibitionDTO atualizarSaldoUsuario(Long id, AtualizarSaldoDTO saldoDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        // Atualiza o saldo
        usuario.setSaldoTotal(saldoDTO.valor());

        // Salva a alteração no banco
        usuarioRepository.save(usuario);

        // Retorna o DTO atualizado
        return new UsuarioExibitionDTO(usuario);
    }


}

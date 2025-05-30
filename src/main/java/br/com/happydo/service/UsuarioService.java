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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
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


    public List<UsuarioExibitionDTO> listarMentorados(Long adminId) {
        List<Usuario> mentorados = usuarioRepository.findMentoradosByAdmin(adminId);
        return mentorados.stream()
                .map(UsuarioExibitionDTO::new)
                .toList();
    }

    private String gerarSenhaAleatoria(int tamanho) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder senha = new StringBuilder(tamanho);

        for (int i = 0; i < tamanho; i++) {
            int index = random.nextInt(caracteres.length());
            senha.append(caracteres.charAt(index));
        }

        return senha.toString();
    }


    public UsuarioExibitionDTO criarUsuario(UsuarioCadastroDTO usuarioCadastroDTO, Long adminId) {
        if (usuarioRepository.existsByEmail(usuarioCadastroDTO.email())) {
            throw new ConflitoEmailException("Já existe um usuário com este email.");
        }

        Usuario usuario = new Usuario();
        BeanUtils.copyProperties(usuarioCadastroDTO, usuario);


        if (usuarioCadastroDTO.role() == UsuarioRole.USER) {

            Usuario admin = usuarioRepository.findById(adminId)
                    .orElseThrow(() -> new UsuarioNaoEncontradoException("Admin não encontrado."));


            String senhaAleatoria = gerarSenhaAleatoria(8);
            String senhaCriptografadaUser = new BCryptPasswordEncoder().encode(senhaAleatoria);
            usuario.setSenha(senhaCriptografadaUser);


            usuario.setAdminResponsavel(admin);
            usuario.setSaldoTotal(0.0);
            usuario.setValorMesadaMensal(0.0);
            usuario.setMesadaAtiva(false);
            usuario.setPrimeiroAcesso(true);

            String assunto = "Cadastro confirmado - HappyDo";
            String mensagem = "Seu cadastro foi criado! \nEssa é sua senha gerada pelo nosso sistema:\n\n" + senhaAleatoria;
            emailUsuarioService.enviarEmail(usuarioCadastroDTO.email(), assunto, mensagem);
        } else {

            String senhaCriptografada = new BCryptPasswordEncoder().encode(usuarioCadastroDTO.senha());
            usuario.setSenha(senhaCriptografada);
            usuario.setSaldoTotal(1000000.0);
            usuario.setValorMesadaMensal(null);
        }


        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return new UsuarioExibitionDTO(usuarioSalvo);
    }


    public UsuarioExibitionDTO buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));
        return new UsuarioExibitionDTO(usuario);
    }


    public List<UsuarioExibitionDTO> listarTodosUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioExibitionDTO::new)
                .toList();
    }

    public UsuarioTarefasExibitionDTO tarefasStatus(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        Integer concluidas = (usuario.getTarefasConcluidas() != null) ? usuario.getTarefasConcluidas() : 0;
        Integer pendentes = (usuario.getTarefasPendentes() != null) ? usuario.getTarefasPendentes() : 0;

        return new UsuarioTarefasExibitionDTO(concluidas, pendentes);

    }


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

    public UsuarioExibitionDTO atualizarUsuario(Long id, UsuarioCadastroDTO usuarioCadastroDTO) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        if (usuarioOptional.isPresent()) {
            Usuario usuarioExistente = usuarioOptional.get();

            Usuario adminResponsavel = usuarioExistente.getAdminResponsavel();
            Double saldoTotalExistente = usuarioExistente.getSaldoTotal();

            BeanUtils.copyProperties(usuarioCadastroDTO, usuarioExistente, "usuarioId", "adminResponsavel", "saldoTotal");

            String senhaCriptografada = new BCryptPasswordEncoder().encode(usuarioCadastroDTO.senha());

            usuarioExistente.setUsuarioId(id);
            usuarioExistente.setSenha(senhaCriptografada);
            usuarioExistente.setAdminResponsavel(adminResponsavel);
            usuarioExistente.setSaldoTotal(saldoTotalExistente);

            usuarioExistente.setMesadaAtiva(usuarioExistente.getMesadaAtiva());
            usuarioExistente.setPrimeiroAcesso(usuarioExistente.getPrimeiroAcesso());

            return new UsuarioExibitionDTO(usuarioRepository.save(usuarioExistente));
        } else {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado.");
        }
    }

    public UsuarioExibitionDTO atualizarSaldoUsuario(Long id, AtualizarSaldoDTO saldoDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        usuario.setSaldoTotal(saldoDTO.valor());

        usuarioRepository.save(usuario);

        return new UsuarioExibitionDTO(usuario);
    }


}

package br.com.happydo.service;

import br.com.happydo.dto.TarefaCheckDataDTO;
import br.com.happydo.exception.AcessoNegadoException;
import br.com.happydo.exception.TarefaNaoEncontradaException;
import br.com.happydo.exception.UsuarioNaoEncontradoException;
import br.com.happydo.model.Tarefa;
import br.com.happydo.model.TarefaCheckData;
import br.com.happydo.model.Usuario;
import br.com.happydo.model.UsuarioRole;
import br.com.happydo.repository.TarefaCheckDataRepository;
import br.com.happydo.repository.TarefaRepository;
import br.com.happydo.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TarefaCheckDataService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TarefaCheckDataRepository tarefaCheckDataRepository;

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private EntityManager entityManager;


    //TODO POSSIVEL VALIDACAO DE NAO PERMITIR A CONFIRMACAO DA MESMA TAREFA ID DUAS VEZES
    // TALVEZ NO FRONT EU CONSIGA NAO PERMITIR ISSO!
    public TarefaCheckDataDTO UsuarioSinalizarConclusaoTarefa(Long tarefaId, Long usuarioId, TarefaCheckDataDTO tarefaCheckDataDTO) {
        // Recuperar o usuário logado
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));
        // Validar se o usuário é um USER
        if (!usuario.getRole().equals(UsuarioRole.USER)) {
            throw new AcessoNegadoException("Apenas usuários USER podem sinalizar a conclusão de tarefas.");
        }

        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada."));

        if (!tarefa.getResponsavel().getUsuarioId().equals(usuarioId)) {
            throw new AcessoNegadoException("Você não tem permissão para sinalizar esta tarefa.");
        }


        TarefaCheckData tarefaCheckData = new TarefaCheckData();
        BeanUtils.copyProperties(tarefaCheckDataDTO, tarefaCheckData);
        tarefaCheckData.setTarefa(tarefa);
        tarefaCheckData.setUsuario_id(usuario);
        tarefaCheckData.setAdmin(tarefa.getCriador());
        tarefaCheckData.setSinalizadaUsuario(true);

        TarefaCheckData tarefaCheckDataSalva = tarefaCheckDataRepository.save(tarefaCheckData);

        return new TarefaCheckDataDTO(tarefaCheckDataSalva);
    }

    //TODO
    // Ajustar conflito de id no update (unificar todos com o mesmo id checkTask e UsuarioSinalizarConclusaoTarefa)

    public void checkTask(Long checkId, Long adminId, boolean aceitaConclusao) {
        Usuario usuario = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        if (!usuario.getRole().equals(UsuarioRole.ADMIN)) {
            throw new AcessoNegadoException("Apenas usuários ADMIN e criador da tarefa que podem confirmar a conclusão de tarefas.");
        }

        TarefaCheckData tarefaCheckData = tarefaCheckDataRepository.findById(checkId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada."));


        if (!tarefaCheckData.getTarefa().getCriador().getUsuarioId().equals(adminId)) {
            throw new AcessoNegadoException("Você não tem permissão para confirmar esta tarefa, " +
                    "pois não é o criador da tarefa.");
        }


        tarefaCheckData.setId(checkId);
        tarefaCheckData.setAdmin(tarefaCheckData.getTarefa().getCriador());
        tarefaCheckData.setUsuario_id(usuario);

        if (aceitaConclusao) {
            // Admin aceitou a conclusão, mantém o campo como true
            tarefaCheckData.setSinalizadaUsuario(true);
            tarefaCheckData.setConcluida(true);
        } else {
            // Admin rejeitou a conclusão, define como false
            tarefaCheckData.setSinalizadaUsuario(false);
            tarefaCheckData.setConcluida(false);
        }

        tarefaCheckDataRepository.save(tarefaCheckData);

    }

    public void deletarConfirmacaoTarefa(Long adminId, Long tarefaId) {

        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        if (!admin.getRole().equals(UsuarioRole.ADMIN)) {
            throw new AcessoNegadoException("Apenas usuários ADMIN podem deletar tarefas.");
        }

        TarefaCheckData tarefaCheckData = tarefaCheckDataRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada."));

        if (!tarefaCheckData.getTarefa().getCriador().getUsuarioId().equals(adminId)) {
            throw new AcessoNegadoException("Você não é o criador dessa tarefa" +
                    " por isso não pode excluir essa conclusão!");
        }

        tarefaCheckDataRepository.delete(tarefaCheckData);
    }


}

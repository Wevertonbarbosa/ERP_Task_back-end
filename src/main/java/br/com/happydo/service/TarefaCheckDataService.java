package br.com.happydo.service;

import br.com.happydo.dto.TarefaCheckDataDTO;
import br.com.happydo.exception.AcessoNegadoException;
import br.com.happydo.exception.TarefaJaSinalizada;
import br.com.happydo.exception.TarefaNaoEncontradaException;
import br.com.happydo.exception.UsuarioNaoEncontradoException;
import br.com.happydo.model.*;
import br.com.happydo.repository.TarefaCheckDataRepository;
import br.com.happydo.repository.TarefaRepository;
import br.com.happydo.repository.UsuarioRepository;
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


    //TODO POSSIVEL VALIDACAO DE NAO PERMITIR A CONFIRMACAO DA MESMA TAREFA ID DUAS VEZES
    // TALVEZ NO FRONT EU CONSIGA NAO PERMITIR ISSO!
    public TarefaCheckDataDTO UsuarioSinalizarConclusaoTarefa(Long tarefaId, Long usuarioId, TarefaCheckDataDTO tarefaCheckDataDTO) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

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

    public void atualizarSinalizarConclusaoTarefa(Long checkId, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        if (!usuario.getRole().equals(UsuarioRole.USER)) {
            throw new AcessoNegadoException("Apenas usuários USER podem sinalizar a conclusão de tarefas.");
        }

        TarefaCheckData tarefaCheckData = tarefaCheckDataRepository.findById(checkId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada."));

        if (!tarefaCheckData.getTarefa().getResponsavel().getUsuarioId().equals(usuarioId)) {
            throw new AcessoNegadoException("Somente usuário responsável pela tarefa pode atualizar a " +
                    "sinalização da conclusão da tarefa.");
        }

        if (tarefaCheckData.isSinalizadaUsuario()) {
            throw new TarefaJaSinalizada("Tarefa já está sinalizada como concluída!");
        }

        tarefaCheckData.setId(checkId);
        tarefaCheckData.setSinalizadaUsuario(true);
        tarefaCheckDataRepository.save(tarefaCheckData);

    }


    public void checkTask(Long checkId, Long adminId, boolean aceitaConclusao) {
        Usuario usuario = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        if (!usuario.getRole().equals(UsuarioRole.ADMIN)) {
            throw new AcessoNegadoException("Apenas usuários que é ADMIN e criador da tarefa que podem confirmar a conclusão de tarefas.");
        }

        TarefaCheckData tarefaCheckData = tarefaCheckDataRepository.findById(checkId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada."));

        if (!tarefaCheckData.getTarefa().getCriador().getUsuarioId().equals(adminId)) {
            throw new AcessoNegadoException("Você não tem permissão para confirmar esta tarefa, " +
                    "pois não é o criador da tarefa.");
        }

        tarefaCheckData.setId(checkId);
        tarefaCheckData.setAdmin(tarefaCheckData.getTarefa().getCriador());

        Usuario usuarioResponsavel = tarefaCheckData.getUsuario_id();

        if (aceitaConclusao) {
            tarefaCheckData.setSinalizadaUsuario(true);
            tarefaCheckData.setConcluida(true);
            tarefaCheckData.getTarefa().setStatus(StatusTarefa.CONCLUIDO);

            // Atualiza contadores
            if (usuarioResponsavel.getTarefasConcluidas() == null) {
                usuarioResponsavel.setTarefasConcluidas(0);
            }
            usuarioResponsavel.setTarefasConcluidas(usuarioResponsavel.getTarefasConcluidas() + 1);

            if (usuarioResponsavel.getTarefasPendentes() == null) {
                usuarioResponsavel.setTarefasPendentes(0);
            }
            usuarioResponsavel.setTarefasPendentes(usuarioResponsavel.getTarefasPendentes() - 1);
            usuarioRepository.save(usuarioResponsavel);

        } else {
            tarefaCheckData.setSinalizadaUsuario(false);
            tarefaCheckData.setConcluida(false);
            tarefaCheckData.getTarefa().setStatus(StatusTarefa.ANDAMENTO);

            // Atualiza contadores ao negar a conclusão
            if (usuarioResponsavel.getTarefasConcluidas() == null || usuarioResponsavel.getTarefasConcluidas() <= 0) {
                usuarioResponsavel.setTarefasConcluidas(0);  // Garantir que não tenha valor negativo
            }
            usuarioResponsavel.setTarefasConcluidas(usuarioResponsavel.getTarefasConcluidas() - 1); // Decrementa tarefas concluídas

            if (usuarioResponsavel.getTarefasPendentes() == null) {
                usuarioResponsavel.setTarefasPendentes(0);
            }
            usuarioResponsavel.setTarefasPendentes(usuarioResponsavel.getTarefasPendentes() + 1); // Incrementa tarefas pendentes

            usuarioRepository.save(usuarioResponsavel);
        }

        tarefaRepository.save(tarefaCheckData.getTarefa());
        tarefaCheckDataRepository.save(tarefaCheckData);
    }

    public void deletarConfirmacaoTarefa(Long adminId, Long checkId) {

        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        if (!admin.getRole().equals(UsuarioRole.ADMIN)) {
            throw new AcessoNegadoException("Apenas usuários ADMIN podem deletar tarefas.");
        }

        TarefaCheckData tarefaCheckData = tarefaCheckDataRepository.findById(checkId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada."));

        if (!tarefaCheckData.getTarefa().getCriador().getUsuarioId().equals(adminId)) {
            throw new AcessoNegadoException("Você não é o criador dessa tarefa" +
                    " por isso não pode excluir essa conclusão!");
        }

        // Ajustar o contador de tarefas pendentes
        Usuario usuarioResponsavel = tarefaCheckData.getUsuario_id();

        // Se a tarefa foi sinalizada como concluída, precisamos atualizar os contadores
        if (tarefaCheckData.isConcluida()) {
            if (usuarioResponsavel.getTarefasConcluidas() != null && usuarioResponsavel.getTarefasConcluidas() > 0) {
                usuarioResponsavel.setTarefasConcluidas(usuarioResponsavel.getTarefasConcluidas() - 1);
            }

            if (usuarioResponsavel.getTarefasPendentes() == null) {
                usuarioResponsavel.setTarefasPendentes(0);
            }
            usuarioResponsavel.setTarefasPendentes(usuarioResponsavel.getTarefasPendentes() + 1);
            usuarioRepository.save(usuarioResponsavel);
        }


        tarefaCheckDataRepository.delete(tarefaCheckData);
    }


}

package br.com.happydo.service;

import br.com.happydo.dto.TarefaCheckDataDTO;
import br.com.happydo.dto.TarefaSinalizadaConcluidaDTO;
import br.com.happydo.dto.UsuarioExibitionDTO;
import br.com.happydo.exception.AcessoNegadoException;
import br.com.happydo.exception.TarefaJaSinalizada;
import br.com.happydo.exception.TarefaNaoEncontradaException;
import br.com.happydo.exception.UsuarioNaoEncontradoException;
import br.com.happydo.model.*;
import br.com.happydo.repository.MesadaRepository;
import br.com.happydo.repository.TarefaCheckDataRepository;
import br.com.happydo.repository.TarefaRepository;
import br.com.happydo.repository.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class TarefaCheckDataService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TarefaCheckDataRepository tarefaCheckDataRepository;

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private MesadaRepository mesadaRepository;

    @Autowired
    private MesadaService mesadaService;


    public List<TarefaSinalizadaConcluidaDTO> tarefaSinalizada(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        List<TarefaCheckData> tarefasSinalizadas = tarefaCheckDataRepository.findTarefasSinalizadasPorUsuario(usuarioId);

        return tarefasSinalizadas.stream().map(TarefaSinalizadaConcluidaDTO::new).toList();
    }


    public TarefaCheckDataDTO UsuarioSinalizarConclusaoTarefa(Long tarefaId, Long usuarioId, TarefaCheckDataDTO tarefaCheckDataDTO) {

        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        if (!usuario.getRole().equals(UsuarioRole.USER)) {
            throw new AcessoNegadoException("Apenas usuários USER podem sinalizar a conclusão de tarefas.");
        }

        Tarefa tarefa = tarefaRepository.findById(tarefaId).orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada."));

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
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        if (!usuario.getRole().equals(UsuarioRole.USER)) {
            throw new AcessoNegadoException("Apenas usuários USER podem sinalizar a conclusão de tarefas.");
        }

        TarefaCheckData tarefaCheckData = tarefaCheckDataRepository.findById(checkId).orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada."));

        if (!tarefaCheckData.getTarefa().getResponsavel().getUsuarioId().equals(usuarioId)) {
            throw new AcessoNegadoException("Somente usuário responsável pela tarefa pode atualizar a " + "sinalização da conclusão da tarefa.");
        }

        if (tarefaCheckData.isSinalizadaUsuario()) {
            throw new TarefaJaSinalizada("Tarefa já está sinalizada como concluída!");
        }

        tarefaCheckData.setId(checkId);
        tarefaCheckData.setSinalizadaUsuario(true);
        tarefaCheckDataRepository.save(tarefaCheckData);

    }


    public void checkAdminTask(Long idTask, Long adminId, boolean conlcuirTarefa) {
        Usuario usuario = usuarioRepository.findById(adminId).orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        if (!usuario.getRole().equals(UsuarioRole.ADMIN)) {
            throw new AcessoNegadoException("Apenas usuários ADMIN pode concluir a tarefa diretamente.");
        }

        Tarefa tarefa = tarefaRepository.findById(idTask).orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada."));


        TarefaCheckData tarefaCheckData = new TarefaCheckData();
        tarefaCheckData.setAdmin(tarefa.getCriador());
        tarefaCheckData.setTarefa(tarefa);
        tarefaCheckData.setUsuario_id(usuario);

        Usuario usuarioResponsavel = tarefaCheckData.getUsuario_id();

        if (conlcuirTarefa) {
            tarefaCheckData.setSinalizadaUsuario(true);
            tarefaCheckData.setConcluida(true);
            tarefaCheckData.getTarefa().setStatus(StatusTarefa.CONCLUIDO);

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

            if (usuarioResponsavel.getTarefasConcluidas() == null || usuarioResponsavel.getTarefasConcluidas() <= 0) {
                usuarioResponsavel.setTarefasConcluidas(0);
            }
            usuarioResponsavel.setTarefasConcluidas(usuarioResponsavel.getTarefasConcluidas() - 1);

            if (usuarioResponsavel.getTarefasPendentes() == null) {
                usuarioResponsavel.setTarefasPendentes(0);
            }
            usuarioResponsavel.setTarefasPendentes(usuarioResponsavel.getTarefasPendentes() + 1);

            usuarioRepository.save(usuarioResponsavel);
        }


        tarefaRepository.save(tarefaCheckData.getTarefa());
        tarefaCheckDataRepository.save(tarefaCheckData);
    }


    public void checkTask(Long checkId, Long adminId, boolean aceitaConclusao) {
        Usuario usuario = usuarioRepository.findById(adminId).orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        if (!usuario.getRole().equals(UsuarioRole.ADMIN)) {
            throw new AcessoNegadoException("Apenas usuários ADMIN podem confirmar a conclusão de tarefas.");
        }

        TarefaCheckData tarefaCheckData = tarefaCheckDataRepository.findById(checkId).orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada."));

        if (!tarefaCheckData.getTarefa().getCriador().getUsuarioId().equals(adminId)) {
            throw new AcessoNegadoException("Você não tem permissão para confirmar esta tarefa.");
        }

        tarefaCheckData.setId(checkId);
        tarefaCheckData.setAdmin(tarefaCheckData.getTarefa().getCriador());

        Usuario usuarioResponsavel = tarefaCheckData.getUsuario_id();

        if (aceitaConclusao) {
            tarefaCheckData.setSinalizadaUsuario(true);
            tarefaCheckData.setConcluida(true);
            tarefaCheckData.getTarefa().setStatus(StatusTarefa.CONCLUIDO);

            Integer pontuacaoTarefa = tarefaCheckData.getTarefa().getPontuacao();
            if (pontuacaoTarefa == null) pontuacaoTarefa = 0;

            // Atualiza pontuação acumulada do usuário
            if (usuarioResponsavel.getPontuacaoAcumulada() == null) {
                usuarioResponsavel.setPontuacaoAcumulada(0);
            }

            usuarioResponsavel.setPontuacaoAcumulada(usuarioResponsavel.getPontuacaoAcumulada() + pontuacaoTarefa);


            // Atualiza contadores de tarefas
            if (usuarioResponsavel.getTarefasConcluidas() == null) usuarioResponsavel.setTarefasConcluidas(0);
            usuarioResponsavel.setTarefasConcluidas(usuarioResponsavel.getTarefasConcluidas() + 1);

            if (usuarioResponsavel.getTarefasPendentes() == null) usuarioResponsavel.setTarefasPendentes(0);
            usuarioResponsavel.setTarefasPendentes(usuarioResponsavel.getTarefasPendentes() - 1);

            mesadaService.adicionarPontosConcluidosNaMesada(usuarioResponsavel.getUsuarioId(), pontuacaoTarefa);


            LocalDate hoje = LocalDate.now();
            int ano = hoje.getYear();
            int mes = hoje.getMonthValue();

            Mesada mesada = mesadaRepository.findByUsuarioAndMes(usuarioResponsavel.getUsuarioId(), ano, mes)
                    .orElseThrow(() -> new IllegalStateException("Mesada não encontrada para o usuário neste período."));

            // Atualiza percentual e valor proporcional
            mesadaService.atualizarDesempenhoMesada(mesada, usuarioResponsavel.getValorMesadaMensal());


            if (mesada.getPontosConcluidos() >= mesada.getTotalPontosPeriodo()) {
                mesada.setMesadaRecebida(true);
                usuarioResponsavel.setSaldoTotal(mesada.getValor());
                usuarioResponsavel.setPontuacaoAcumulada(0);
                usuarioResponsavel.setValorMesadaMensal(0.0);
            }


            usuarioRepository.save(usuarioResponsavel);
            mesadaRepository.save(mesada);

        } else {
            // Rejeição da tarefa
            tarefaCheckData.setSinalizadaUsuario(false);
            tarefaCheckData.setConcluida(false);
            tarefaCheckData.getTarefa().setStatus(StatusTarefa.ANDAMENTO);

            if (usuarioResponsavel.getTarefasConcluidas() == null || usuarioResponsavel.getTarefasConcluidas() <= 0) {
                usuarioResponsavel.setTarefasConcluidas(0);
            } else {
                usuarioResponsavel.setTarefasConcluidas(usuarioResponsavel.getTarefasConcluidas() - 1);
            }

            if (usuarioResponsavel.getTarefasPendentes() == null) usuarioResponsavel.setTarefasPendentes(0);
            usuarioResponsavel.setTarefasPendentes(usuarioResponsavel.getTarefasPendentes() + 1);

            usuarioRepository.save(usuarioResponsavel);
        }

        tarefaRepository.save(tarefaCheckData.getTarefa());
        tarefaCheckDataRepository.save(tarefaCheckData);
    }

    public void deletarConfirmacaoTarefa(Long adminId, Long checkId) {

        Usuario admin = usuarioRepository.findById(adminId).orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        if (!admin.getRole().equals(UsuarioRole.ADMIN)) {
            throw new AcessoNegadoException("Apenas usuários ADMIN podem deletar tarefas.");
        }

        TarefaCheckData tarefaCheckData = tarefaCheckDataRepository.findById(checkId).orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada."));

        if (!tarefaCheckData.getTarefa().getCriador().getUsuarioId().equals(adminId)) {
            throw new AcessoNegadoException("Você não é o criador dessa tarefa" + " por isso não pode excluir essa conclusão!");
        }

        Usuario usuarioResponsavel = tarefaCheckData.getUsuario_id();

        if (tarefaCheckData.isConcluida()) {
            if (usuarioResponsavel.getTarefasConcluidas() != null && usuarioResponsavel.getTarefasConcluidas() > 0) {
                usuarioResponsavel.setTarefasConcluidas(usuarioResponsavel.getTarefasConcluidas() - 1);
            }

            if (usuarioResponsavel.getTarefasPendentes() == null) {
                usuarioResponsavel.setTarefasPendentes(0);
            }
            usuarioResponsavel.setTarefasPendentes(usuarioResponsavel.getTarefasPendentes() + 1);

            tarefaCheckData.getTarefa().setStatus(StatusTarefa.ANDAMENTO);

            usuarioRepository.save(usuarioResponsavel);
        }

        tarefaRepository.save(tarefaCheckData.getTarefa());
        tarefaCheckDataRepository.delete(tarefaCheckData);
    }


}

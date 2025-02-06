package br.com.happydo.service;

import br.com.happydo.dto.TarefaDTO;
import br.com.happydo.exception.*;
import br.com.happydo.model.*;
import br.com.happydo.repository.TarefaRepository;
import br.com.happydo.repository.UsuarioRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;


    public TarefaDTO criarTarefa(Long criadorId, Long responsavelId, TarefaDTO tarefaDTO) {
        Usuario criador = usuarioRepository.findById(criadorId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        if (!criador.getRole().equals(UsuarioRole.ADMIN)) {
            throw new AcessoNegadoException("Apenas usuários ADMIN podem criar tarefas.");
        }

        Usuario responsavel = usuarioRepository.findById(responsavelId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário responsável não encontrado."));

        if (tarefaDTO.dataInicio().isAfter(tarefaDTO.dataFim())) {
            throw new ConflitoDatasException("A data de início não pode ser maior que a data de fim.");
        }


        Tarefa tarefa = new Tarefa();
        BeanUtils.copyProperties(tarefaDTO, tarefa);
        tarefa.setCriador(criador); // Associar o criador
        tarefa.setResponsavel(responsavel); // Associar o responsavel
        tarefa.setStatus(StatusTarefa.ANDAMENTO);

        responsavel.setTarefasPendentes(responsavel.getTarefasPendentes() + 1);
        usuarioRepository.save(responsavel);

        if (tarefaDTO.frequencia() != null && tarefaDTO.frequencia().equals(FrequenciaTarefa.SEMANAL)) {
            // Validação dos dias da semana
            List<String> diasValidos = Arrays.asList("segunda", "terça", "quarta", "quinta", "sexta", "sabado", "domingo");
            if (tarefaDTO.diasSemana() == null || tarefaDTO.diasSemana().isEmpty()) {
                throw new DiasObrigatorioException("Os dias da semana são obrigatórios para tarefas com frequência semanal.");
            }

            for (String dia : tarefaDTO.diasSemana()) {
                // Ignora se o dia for inserido em maiúsculas ou minúsculas
                if (!diasValidos.contains(dia.trim().toLowerCase())) {
                    throw new DiaInvalidoException("Dia da semana inválido: " + dia);
                }
            }

            tarefa.setDiasSemanaList(tarefaDTO.diasSemana());
        } else {
            tarefa.setDiasSemanaList(null);
        }


        Tarefa tarefaSalva = tarefaRepository.save(tarefa);

        return new TarefaDTO(tarefaSalva);
    }


    public List<TarefaDTO> listarTarefas(Long usuarioId) {
        // Recupera o usuário logado
        Usuario usuarioLogado = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));


        // Usuário mentorado: retorna apenas as tarefas delegadas a ele
        return tarefaRepository.findByResponsavel_UsuarioId(usuarioId).stream()
                .map(TarefaDTO::new)
                .toList();

    }

    public List<TarefaDTO> listarTarefasMentorados(Long usuarioId) {
        Usuario usuarioLogado = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        if (usuarioLogado.getRole().equals(UsuarioRole.ADMIN)) {
            // Buscar tarefas criadas pelo admin, mas com outro responsável
            return tarefaRepository.findByCriador_UsuarioIdAndResponsavel_UsuarioIdNot(usuarioId, usuarioId)
                    .stream()
                    .map(TarefaDTO::new)
                    .toList();
        } else {
            throw new AcessoNegadoException("Perfil de usuário não autorizado.");
        }

    }


    public TarefaDTO atualizarTarefa(Long adminId, Long tarefaId, TarefaDTO tarefaDTO) {
        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        if (!admin.getRole().equals(UsuarioRole.ADMIN)) {
            throw new AcessoNegadoException("Apenas usuários ADMIN podem atualizar tarefas.");
        }

        Optional<Tarefa> tarefaOptional = tarefaRepository.findById(tarefaId);
        if (tarefaOptional.isPresent()) {
            Tarefa tarefa = tarefaOptional.get();
            if (tarefaDTO.dataInicio().isAfter(tarefaDTO.dataFim())) {
                throw new ConflitoDatasException("A data de início não pode ser maior que a data de fim.");
            }
            BeanUtils.copyProperties(tarefaDTO, tarefa);
            // Manter o ID da tarefa para evitar conflitos
            tarefa.setId(tarefaId);


            if (tarefaDTO.frequencia() == FrequenciaTarefa.SEMANAL) {

                if (tarefaDTO.diasSemana() == null || tarefaDTO.diasSemana().isEmpty()) {
                    throw new DiasObrigatorioException("Os dias da semana são obrigatórios para tarefas com frequência semanal.");
                }
                // Validação dos dias da semana
                List<String> diasValidos = Arrays.asList("segunda", "terça", "quarta", "quinta", "sexta", "sabado", "domingo");
                for (String dia : tarefaDTO.diasSemana()) {
                    // Ignora se o dia for inserido em maiúsculas ou minúsculas
                    if (!diasValidos.contains(dia.trim().toLowerCase())) {
                        throw new DiaInvalidoException("Dia da semana inválido: " + dia);
                    }
                }
                tarefa.setDiasSemanaList(tarefaDTO.diasSemana());
            } else {
                tarefa.setDiasSemanaList(null);
            }

            return new TarefaDTO(tarefaRepository.save(tarefa));
        } else {
            throw new TarefaNaoEncontradaException("Tarefa não encontrada.");
        }
    }


    public void deletarTarefa(Long adminId, Long tarefaId) {

        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        if (!admin.getRole().equals(UsuarioRole.ADMIN)) {
            throw new AcessoNegadoException("Apenas usuários ADMIN podem deletar tarefas.");
        }

        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada."));

        Usuario usuarioResponsavel = tarefa.getResponsavel();

        if (tarefa.getStatus().toString().equals("ANDAMENTO")) {
            usuarioResponsavel.setTarefasPendentes(usuarioResponsavel.getTarefasPendentes() - 1);
        } else {
            usuarioResponsavel.setTarefasConcluidas(usuarioResponsavel.getTarefasConcluidas() - 1);
        }
        usuarioRepository.save(usuarioResponsavel);


        tarefaRepository.delete(tarefa);
    }


}

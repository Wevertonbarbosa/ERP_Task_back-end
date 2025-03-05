package br.com.happydo.controller;

import br.com.happydo.dto.TarefaCheckDataDTO;
import br.com.happydo.dto.TarefaSinalizadaConcluidaDTO;
import br.com.happydo.exception.AcessoNegadoException;
import br.com.happydo.exception.TarefaNaoEncontradaException;
import br.com.happydo.exception.UsuarioNaoEncontradoException;
import br.com.happydo.service.TarefaCheckDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tarefas-data")
public class TarefaCheckDataController {

    @Autowired
    private TarefaCheckDataService tarefaCheckDataService;


    @GetMapping("/tarefa-sinalizada/{usuarioId}")
    public ResponseEntity<List<TarefaSinalizadaConcluidaDTO>> listarTarefasSinalizadas(@PathVariable Long usuarioId) {
        List<TarefaSinalizadaConcluidaDTO> tarefas = tarefaCheckDataService.tarefaSinalizada(usuarioId);
        return ResponseEntity.ok(tarefas);
    }

    @PostMapping("/{tarefaId}/sinalizar/{usuarioId}")
    public ResponseEntity<TarefaCheckDataDTO> sinalizarConclusaoTarefa(
            @PathVariable Long tarefaId,
            @PathVariable Long usuarioId,
            @RequestBody TarefaCheckDataDTO tarefaCheckDataDTO) {
        TarefaCheckDataDTO tarefaDataConfirmando =
                tarefaCheckDataService.UsuarioSinalizarConclusaoTarefa(tarefaId, usuarioId, tarefaCheckDataDTO);
        return ResponseEntity.ok(tarefaDataConfirmando);
    }

    @PutMapping("/{checkId}/atualiza-sinalizar/{usuarioId}")
    public ResponseEntity<String> sinalizarConclusaoNovamenteTarefa(
            @PathVariable Long checkId,
            @PathVariable Long usuarioId) {
        tarefaCheckDataService.atualizarSinalizarConclusaoTarefa(checkId, usuarioId);
        return ResponseEntity.ok("Tarefa sinalizada como concluida novamente!");
    }

    @PutMapping("/{checkId}/confirmar/{adminId}/{aceitaConclusao}")
    public ResponseEntity<String> concluirTarefas(
            @PathVariable Long checkId,
            @PathVariable Long adminId,
            @PathVariable boolean aceitaConclusao
    ) {
        tarefaCheckDataService.checkTask(checkId, adminId, aceitaConclusao);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{checkId}/{adminId}")
    public ResponseEntity<Void> deleteConfirmacao(
            @PathVariable Long checkId,
            @PathVariable Long adminId) {
        tarefaCheckDataService.deletarConfirmacaoTarefa(adminId, checkId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{idTask}/concluir/{adminId}/{conlcuirTarefa}")
    public ResponseEntity<Void> adminConcluirTarefa(
            @PathVariable Long idTask,
            @PathVariable Long adminId,
            @PathVariable boolean conlcuirTarefa) {

        try {
            tarefaCheckDataService.checkAdminTask(idTask, adminId, conlcuirTarefa);
            return ResponseEntity.ok().build();
        } catch (UsuarioNaoEncontradoException e) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado.");
        } catch (TarefaNaoEncontradaException e) {
            throw new TarefaNaoEncontradaException("Tarefa não encontrado.");
        } catch (AcessoNegadoException e) {
            throw new AcessoNegadoException("Apenas usuários ADMIN pode concluir a tarefa diretamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Retorna 500 para outros erros inesperados
        }


    }


}

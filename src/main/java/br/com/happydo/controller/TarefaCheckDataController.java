package br.com.happydo.controller;

import br.com.happydo.dto.TarefaCheckDataDTO;
import br.com.happydo.service.TarefaCheckDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tarefas-data")
public class TarefaCheckDataController {

    @Autowired
    private TarefaCheckDataService tarefaCheckDataService;


    //USUARIO SINALIZANDO A CONCLUSAO DA TAREFA
    @PostMapping("/{tarefaId}/sinalizar/{usuarioId}")
    public ResponseEntity<TarefaCheckDataDTO> sinalizarConclusaoTarefa(
            @PathVariable Long tarefaId,
            @PathVariable Long usuarioId,
            @RequestBody TarefaCheckDataDTO tarefaCheckDataDTO) {
        TarefaCheckDataDTO tarefaDataConfirmando =
                tarefaCheckDataService.UsuarioSinalizarConclusaoTarefa(tarefaId, usuarioId, tarefaCheckDataDTO);
        return ResponseEntity.ok(tarefaDataConfirmando);
    }

    //USUARIO SINALIZA NOVAMENTE TAREFA CONCLUIDA
    @PutMapping("/{checkId}/atualiza-sinalizar/{usuarioId}")
    public ResponseEntity<String> sinalizarConclusaoNovamenteTarefa(
            @PathVariable Long checkId,
            @PathVariable Long usuarioId) {
        tarefaCheckDataService.atualizarSinalizarConclusaoTarefa(checkId, usuarioId);
        return ResponseEntity.ok("Tarefa sinalizada como concluida novamente!");
    }

    //ADMIN CONFIRMANDO A CONCLUSAO DA TAREFA
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
}

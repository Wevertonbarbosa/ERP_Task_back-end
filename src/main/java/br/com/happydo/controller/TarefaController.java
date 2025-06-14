package br.com.happydo.controller;

import br.com.happydo.dto.TarefaDTO;
import br.com.happydo.model.StatusTarefa;
import br.com.happydo.service.TarefaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    @PostMapping("/{criadorId}/{responsavelId}")
    public ResponseEntity<TarefaDTO> criarTarefa(
            @PathVariable Long criadorId,
            @PathVariable Long responsavelId,
            @RequestBody TarefaDTO tarefaDTO) {
        TarefaDTO novaTarefa = tarefaService.criarTarefa(criadorId, responsavelId, tarefaDTO);
        return ResponseEntity.ok(novaTarefa);
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<TarefaDTO>> listarTarefas(@PathVariable Long usuarioId) {
        List<TarefaDTO> tarefas = tarefaService.listarTarefas(usuarioId);
        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("mentorado/{usuarioId}")
    public ResponseEntity<List<TarefaDTO>> listarTarefasMentorados(@PathVariable Long usuarioId) {
        List<TarefaDTO> tarefas = tarefaService.listarTarefasMentorados(usuarioId);
        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/{usuarioId}/andamento")
    public ResponseEntity<List<TarefaDTO>> listarTarefasAndamento(@PathVariable Long usuarioId) {
        List<TarefaDTO> tarefas = tarefaService.listarTarefasPorStatus(usuarioId, StatusTarefa.ANDAMENTO);
        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/{usuarioId}/concluido")
    public ResponseEntity<List<TarefaDTO>> listarTarefasConcluidas(@PathVariable Long usuarioId) {
        List<TarefaDTO> tarefas = tarefaService.listarTarefasPorStatus(usuarioId, StatusTarefa.CONCLUIDO);
        return ResponseEntity.ok(tarefas);
    }


    @PutMapping("/{tarefaId}/{adminId}")
    public ResponseEntity<TarefaDTO> atualizarTarefa(
            @PathVariable Long tarefaId,
            @PathVariable Long adminId,
            @RequestBody @Valid TarefaDTO tarefaDTO) {
        TarefaDTO tarefaAtualizada = tarefaService.atualizarTarefa(adminId, tarefaId, tarefaDTO);
        return ResponseEntity.ok(tarefaAtualizada);
    }

    @DeleteMapping("/{tarefaId}/{adminId}")
    public ResponseEntity<Void> deletarTarefa(
            @PathVariable Long tarefaId,
            @PathVariable Long adminId) {
        tarefaService.deletarTarefa(adminId, tarefaId);
        return ResponseEntity.noContent().build();
    }




}

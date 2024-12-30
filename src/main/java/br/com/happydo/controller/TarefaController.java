package br.com.happydo.controller;

import br.com.happydo.dto.TarefaDTO;
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

    /**
     * Endpoint para criar uma nova tarefa.
     *
     * @param criadorId    ID do usuário criador (ADMIN).
     * @param responsavelId ID do usuário responsável (USER).
     * @param tarefaDTO    Objeto com os dados da tarefa.
     * @return Tarefa criada.
     */

    @PostMapping("/{criadorId}/{responsavelId}")
    public ResponseEntity<TarefaDTO> criarTarefa(
            @PathVariable Long criadorId,
            @PathVariable Long responsavelId,
            @RequestBody TarefaDTO tarefaDTO) {
        TarefaDTO novaTarefa = tarefaService.criarTarefa(criadorId, responsavelId, tarefaDTO);
        return ResponseEntity.ok(novaTarefa);
    }

    /**
     * Endpoint para listar as tarefas do usuário logado.
     *
     * @param usuarioId ID do usuário logado.
     * @return Lista de tarefas.
     */
    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<TarefaDTO>> listarTarefas(@PathVariable Long usuarioId) {
        List<TarefaDTO> tarefas = tarefaService.listarTarefas(usuarioId);
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

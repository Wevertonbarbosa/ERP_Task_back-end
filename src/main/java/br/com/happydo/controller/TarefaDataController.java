package br.com.happydo.controller;

import br.com.happydo.dto.TarefaDataDTO;
import br.com.happydo.service.TarefaDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tarefas-data")
public class TarefaDataController {

    @Autowired
    private TarefaDataService tarefaDataService;


    //USUARIO SINALIZANDO A CONCLUSAO DA TAREFA
    @PostMapping("/{tarefaId}/sinalizar/{usuarioId}")
    public ResponseEntity<TarefaDataDTO> sinalizarConclusaoTarefa(
            @PathVariable Long tarefaId,
            @PathVariable Long usuarioId,
            @RequestBody TarefaDataDTO tarefaDataDTO) {
        TarefaDataDTO tarefaDataConfirmando =
                tarefaDataService.UsuarioSinalizarConclusaoTarefa(tarefaId, usuarioId, tarefaDataDTO);
        return ResponseEntity.ok(tarefaDataConfirmando);
    }

    //ADMIN CONFIRMANDO A CONCLUSAO DA TAREFA
    @PutMapping("/{tarefaId}/confirmar/{adminId}")
    public ResponseEntity<Void> confirmarConclusaoTarefa(
            @PathVariable Long tarefaId,
            @PathVariable Long adminId) {
        tarefaDataService.confirmarConclusaoTarefa(tarefaId, adminId);
        return ResponseEntity.noContent().build();
    }

}

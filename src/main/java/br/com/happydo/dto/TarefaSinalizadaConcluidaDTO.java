package br.com.happydo.dto;

import br.com.happydo.model.TarefaCheckData;
import br.com.happydo.model.Usuario;

public record TarefaSinalizadaConcluidaDTO(
        Long usuario_id,
        Long tarefa,
        boolean sinalizadaUsuario,
        boolean concluida
) {
    public TarefaSinalizadaConcluidaDTO(TarefaCheckData tarefaCheckData) {
        this(
                tarefaCheckData.getUsuario_id().getUsuarioId(),
                tarefaCheckData.getTarefa().getId(),
                tarefaCheckData.isSinalizadaUsuario(),
                tarefaCheckData.isConcluida()
        );
    }


}

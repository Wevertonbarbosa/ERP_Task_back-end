package br.com.happydo.dto;

import br.com.happydo.model.TarefaCheckData;


public record TarefaCheckDataDTO(
        Long id,
        Long tarefa,
        Long admin,
        Long usuario_id,
        boolean sinalizadaUsuario,
        boolean concluida
) {

    public TarefaCheckDataDTO(TarefaCheckData tarefaCheckData) {
        this(
                tarefaCheckData.getId(),
                tarefaCheckData.getTarefa().getId(),
                tarefaCheckData.getAdmin().getUsuarioId(),
                tarefaCheckData.getUsuario_id().getUsuarioId(),
                tarefaCheckData.isSinalizadaUsuario(),
                tarefaCheckData.isConcluida()
        );
    }
}

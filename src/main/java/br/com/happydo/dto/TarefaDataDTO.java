package br.com.happydo.dto;

import br.com.happydo.model.TarefaData;


public record TarefaDataDTO(
        Long id,
        Long tarefa,
        Long admin,
        Long usuario_id,
        boolean sinalizadaUsuario,
        boolean concluida
) {

    public TarefaDataDTO(TarefaData tarefaData) {
        this(
                tarefaData.getId(),
                tarefaData.getTarefa().getId(),
                tarefaData.getAdmin().getUsuarioId(),
                tarefaData.getUsuario_id().getUsuarioId(),
                tarefaData.isSinalizadaUsuario(),
                tarefaData.isConcluida()
        );
    }
}

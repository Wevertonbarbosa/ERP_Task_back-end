package br.com.happydo.dto;

import br.com.happydo.model.FrequenciaTarefa;
import br.com.happydo.model.StatusTarefa;
import br.com.happydo.model.Tarefa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public record TarefaDTO(
        Long id,
        @NotBlank(message = "O Titulo da tarefa é obrigatório!")
        String titulo,

        @NotBlank(message = "A descrição da tarefa é obrigatório!")
        String descricao,

        @NotBlank(message = "A categoria da tarefa é obrigatório!")
        String categoria,

        @NotNull(message = "A Frequência da tarefa é obrigatória!")
        FrequenciaTarefa frequencia,

        @NotNull(message = "O Status da tarefa é obrigatória para atualizar a tarefa!")
        StatusTarefa status,

        LocalDate dataInicio,
        LocalDate dataFim,

        List<String> diasSemana,


        Integer pontuacao,


        Long criadorId,
        Long responsavelId


) {

    public TarefaDTO(Tarefa tarefa) {
        this(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getCategoria(),
                tarefa.getFrequencia(),
                tarefa.getStatus(),
                tarefa.getDataInicio(),
                tarefa.getDataFim(),
                tarefa.getDiasSemanaList(),
                tarefa.getPontuacao(),
                tarefa.getCriador().getUsuarioId(),
                tarefa.getResponsavel().getUsuarioId()

        );
    }
}

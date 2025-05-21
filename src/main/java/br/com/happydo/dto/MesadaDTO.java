package br.com.happydo.dto;

import br.com.happydo.model.Mesada;

import java.time.LocalDate;

public record MesadaDTO(
        Long id,
        Long usuario,
        Double valor,
        LocalDate dataRecebimento,
        Integer totalPontosPeriodo,
        Integer pontosConcluidos,
        Integer mesReferencia,
        Integer anoReferencia
) {

    public MesadaDTO(Mesada mesada) {
        this(
                mesada.getId(),
                mesada.getUsuario().getUsuarioId(),
                mesada.getValor(),
                mesada.getDataRecebimento(),
                mesada.getTotalPontosPeriodo(),
                mesada.getPontosConcluidos(),
                mesada.getMesReferencia(),
                mesada.getAnoReferencia()
        );
    }
}

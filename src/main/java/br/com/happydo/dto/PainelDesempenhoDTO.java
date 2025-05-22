package br.com.happydo.dto;

import br.com.happydo.model.Mesada;

public record PainelDesempenhoDTO(
        Integer totalPontosPeriodo,
        Integer pontosConcluidos,
        Double percentualConclusao,
        Double valorProporcional
) {

    public PainelDesempenhoDTO(Mesada mesada) {
        this(
                mesada.getTotalPontosPeriodo(),
                mesada.getPontosConcluidos(),
                mesada.getPercentualConclusao(),
                mesada.getValorProporcional()
        );
    }
}

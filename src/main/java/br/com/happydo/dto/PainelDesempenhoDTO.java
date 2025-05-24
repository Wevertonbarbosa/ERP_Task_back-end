package br.com.happydo.dto;

import br.com.happydo.model.Mesada;

public record PainelDesempenhoDTO(
        Double valor,
        Integer totalPontosPeriodo,
        Integer pontosConcluidos,
        Double percentualConclusao,
        Double valorProporcional
) {

    public PainelDesempenhoDTO(Mesada mesada) {
        this(
                mesada.getValor(),
                mesada.getTotalPontosPeriodo(),
                mesada.getPontosConcluidos(),
                mesada.getPercentualConclusao(),
                mesada.getValorProporcional()
        );
    }
}

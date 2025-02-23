package br.com.happydo.dto;

import br.com.happydo.model.CategoriaGasto;
import br.com.happydo.model.Gasto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GastoDTO(
        Long id,
        Long usuario,

        Double valor,
        @NotNull(message = "A Categoria do gasto é obrigatória!")
        CategoriaGasto categoria,
        @NotBlank(message = "O Titulo do gasto é obrigatório!")
        String titulo,
        @NotBlank(message = "O produto do gasto é obrigatório!")
        String produto,

        String descricao,

        LocalDate dataGasto
) {

    public GastoDTO(Gasto gasto) {
        this(
                gasto.getId(),
                gasto.getUsuario().getUsuarioId(),
                gasto.getValor(),
                gasto.getCategoria(),
                gasto.getTitulo(),
                gasto.getProduto(),
                gasto.getDescricao(),
                gasto.getDataGasto()
        );
    }
}

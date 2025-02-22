package br.com.happydo.dto;

import jakarta.validation.constraints.NotNull;

public record AtualizarSaldoDTO(
        @NotNull(message = "O Valor é obrigatório!")
        Double valor
) {}

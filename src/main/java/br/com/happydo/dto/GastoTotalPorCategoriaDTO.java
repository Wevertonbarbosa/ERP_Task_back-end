package br.com.happydo.dto;

import java.time.YearMonth;

public record GastoTotalPorCategoriaDTO(YearMonth mesAno, Double totalEssencial, Double totalNaoEssencial) {
}

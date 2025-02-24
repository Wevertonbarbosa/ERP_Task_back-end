package br.com.happydo.dto;

import java.time.YearMonth;

public record GastoTotalPorCategoriaMensalDTO(YearMonth mesAno, Double totalEssencial, Double totalNaoEssencial) {
}

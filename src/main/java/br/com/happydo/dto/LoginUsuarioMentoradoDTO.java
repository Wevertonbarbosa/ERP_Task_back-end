package br.com.happydo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginUsuarioMentoradoDTO(
        @NotBlank(message = "Email do Usuário é obrigatório!")
        @Email(message = "Email com formato inválido.")
        String email
) {
}

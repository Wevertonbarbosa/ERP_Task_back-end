package br.com.happydo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioNovaSenhaDTO(

        Long usuarioId,

        @NotBlank(message = "O email do usuário é obrigatório!")
        @Email(message = "O email do usuário não válido!")
        String email,

        @NotBlank(message = "A senha do usuário é obrigatória!")
        @Size(min = 6, max = 20, message = "A senha deve conter entre 6 e 20 caracteres!")
        String novaSenha

) {

}

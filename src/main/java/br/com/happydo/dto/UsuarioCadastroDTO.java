package br.com.happydo.dto;

import br.com.happydo.model.UsuarioRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioCadastroDTO(

        Long usuarioId,

        @NotBlank(message = "O nome do usuário é obrigatório!")
        String nome,

        @NotBlank(message = "O email do usuário é obrigatório!")
        @Email(message = "O email do usuário não válido!")
        String email,


        @Size(min = 6, max = 20, message = "A senha deve conter entre 6 e 20 caracteres!")
        String senha,

        @NotNull(message = "A role do usuário é obrigatória!")
        UsuarioRole role
) {
}

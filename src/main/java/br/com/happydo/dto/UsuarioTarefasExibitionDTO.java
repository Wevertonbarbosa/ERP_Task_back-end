package br.com.happydo.dto;

import br.com.happydo.model.Usuario;

public record UsuarioTarefasExibitionDTO(
        Integer tarefasConcluidas,
        Integer tarefasPendentes
) {

    public UsuarioTarefasExibitionDTO(Usuario usuario) {
        this(
                usuario.getTarefasConcluidas(),
                usuario.getTarefasPendentes()
        );
    }
}

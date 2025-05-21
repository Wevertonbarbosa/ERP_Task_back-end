package br.com.happydo.dto;

import br.com.happydo.model.Usuario;
import br.com.happydo.model.UsuarioRole;

public record UsuarioExibitionDTO(
        Long usuarioId,
        String nome,
        String email,
        UsuarioRole role,
        Integer tarefasConcluidas,
        Integer tarefasPendentes,
        Double saldoTotal,
        Integer pontuacaoAcumulada,
        Double valorMesadaMensal

) {
    public UsuarioExibitionDTO(Usuario usuario) {
        this(
                usuario.getUsuarioId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getTarefasConcluidas(),
                usuario.getTarefasPendentes(),
                usuario.getSaldoTotal(),
                usuario.getPontuacaoAcumulada(),
                usuario.getValorMesadaMensal()
        );
    }


}
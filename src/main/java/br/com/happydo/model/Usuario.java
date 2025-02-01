package br.com.happydo.model;


import jakarta.persistence.*;

import lombok.*;


@Entity
@Table(name = "tbl_usuarios")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    @SequenceGenerator(name = "usuario_seq", sequenceName = "USUARIO_SEQ", allocationSize = 1)
    @Column(name = "usuario_id")
    private Long usuarioId;


    private String nome;
    private String email;
    private String senha;

    @Enumerated(EnumType.STRING)
    private UsuarioRole role;

    @Column(name = "tarefas_concluidas")
    private Integer tarefasConcluidas = 0;

    @Column(name = "tarefas_pendentes")
    private Integer tarefasPendentes = 0;


}

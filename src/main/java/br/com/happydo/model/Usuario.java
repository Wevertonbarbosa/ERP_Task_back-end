package br.com.happydo.model;


import jakarta.persistence.*;

import lombok.*;

import java.util.ArrayList;
import java.util.List;


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

    @Column(name = "saldo_total", nullable = false)
    private Double saldoTotal = 0.0;

    @Column(name = "pontuacao_acumulada")
    private Integer pontuacaoAcumulada = 0;

    @Column(name = "valor_mesada_mensal")
    private Double valorMesadaMensal = 0.0;

    @OneToMany(mappedBy = "criador")
    private List<Tarefa> tarefasCriadas;

    @ManyToOne
    @JoinColumn(name = "admin_responsavel_id")
    private Usuario adminResponsavel;

    @OneToMany(mappedBy = "adminResponsavel")
    private List<Usuario> mentorados = new ArrayList<>();


}

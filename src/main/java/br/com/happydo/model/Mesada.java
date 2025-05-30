package br.com.happydo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_mesada")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Data
public class Mesada {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_mesada")
    @SequenceGenerator(name = "seq_mesada", sequenceName = "SEQ_MESADA", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private Double valor;

    @Column(nullable = false)
    private LocalDate dataRecebimento;

    @Column(name = "total_pontos_periodo")
    private Integer totalPontosPeriodo = 0;

    @Column(name = "pontos_concluidos")
    private Integer pontosConcluidos;

    @Column(name = "mes_referencia")
    private Integer mesReferencia;

    @Column(name = "ano_referencia")
    private Integer anoReferencia;

    @Column(name = "percentual_conclusao")
    private Double percentualConclusao;

    @Column(name = "valor_proporcional")
    private Double valorProporcional;

    @Column(name = "mesada_recebida")
    private Boolean mesadaRecebida;


}

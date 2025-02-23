package br.com.happydo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_gastos")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Data
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gastos")
    @SequenceGenerator(name = "seq_gastos", sequenceName = "SEQ_GASTOS", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private Double valor;

    @Enumerated(EnumType.STRING)
    private CategoriaGasto categoria;

    private String titulo;

    private String produto;

    private String descricao;

    private LocalDate dataGasto;



}

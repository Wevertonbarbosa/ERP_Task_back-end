package br.com.happydo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "tbl_tarefas")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Data
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tarefas")
    @SequenceGenerator(name = "seq_tarefas", sequenceName = "SEQ_TAREFAS", allocationSize = 1)
    private Long id;

    private String titulo;
    private String descricao;
    private String categoria;

    @Enumerated(EnumType.STRING)
    private FrequenciaTarefa frequencia;
    @Column(name = "data_inicio")
    private LocalDate dataInicio;
    @Column(name = "data_fim")
    private LocalDate dataFim;

    @ManyToOne
    @JoinColumn(name = "criador_id", nullable = false)
    private Usuario criador;

    @ManyToOne
    @JoinColumn(name = "responsavel_id", nullable = false)
    private Usuario responsavel;

    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TarefaCheckData> datas;

    @Column(name = "dias_semana") // Limite de caracteres para evitar problemas
    private String diasSemana;

    public List<String> getDiasSemanaList() {
        if (diasSemana != null) {
            return Arrays.asList(diasSemana.split(","));
        }
        return new ArrayList<>();
    }

    public void setDiasSemanaList(List<String> dias) {
        if (dias != null && !dias.isEmpty()) {
            this.diasSemana = String.join(",", dias);
        }
    }

}

package br.com.happydo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_tarefas_data")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Data
public class TarefaData {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tarefas_data")
    @SequenceGenerator(name = "seq_tarefas_data", sequenceName = "SEQ_TAREFAS_DATA", allocationSize = 1)
    private Long id;

    private boolean concluida = false;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Usuario admin;

    @ManyToOne
    @JoinColumn(name = "tarefa_id", nullable = false)
    private Tarefa tarefa;

    @Column(name = "sinalizada_usuario", nullable = false)
    private boolean sinalizadaUsuario = false;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario_id;


}

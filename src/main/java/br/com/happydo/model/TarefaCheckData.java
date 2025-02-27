package br.com.happydo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_tarefas_check_data")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Data
public class TarefaCheckData {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tarefas_check_data")
    @SequenceGenerator(name = "seq_tarefas_check_data", sequenceName = "SEQ_TAREFAS_CHECK_DATA", allocationSize = 1)
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

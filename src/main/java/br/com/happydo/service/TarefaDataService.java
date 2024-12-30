package br.com.happydo.service;

import br.com.happydo.dto.TarefaDataDTO;
import br.com.happydo.exception.AcessoNegadoException;
import br.com.happydo.exception.TarefaNaoEncontradaException;
import br.com.happydo.exception.TarefaNaoSinalizadaException;
import br.com.happydo.exception.UsuarioNaoEncontradoException;
import br.com.happydo.model.Tarefa;
import br.com.happydo.model.TarefaData;
import br.com.happydo.model.Usuario;
import br.com.happydo.model.UsuarioRole;
import br.com.happydo.repository.TarefaDataRepository;
import br.com.happydo.repository.TarefaRepository;
import br.com.happydo.repository.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TarefaDataService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TarefaDataRepository tarefaDataRepository;

    @Autowired
    private TarefaRepository tarefaRepository;


    //TODO POSSIVEL VALIDACAO DE NAO PERMITIR A CONFIRMACAO DA MESMA TAREFA ID DUAS VEZES
    // TALVEZ NO FRONT EU CONSIGA NAO PERMITIR ISSO!
    public TarefaDataDTO UsuarioSinalizarConclusaoTarefa(Long tarefaId, Long usuarioId, TarefaDataDTO tarefaDataDTO) {
        // Recuperar o usuário logado
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));
        // Validar se o usuário é um USER
        if (!usuario.getRole().equals(UsuarioRole.USER)) {
            throw new AcessoNegadoException("Apenas usuários USER podem sinalizar a conclusão de tarefas.");
        }

        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada."));

        if (!tarefa.getResponsavel().getUsuarioId().equals(usuarioId)) {
            throw new AcessoNegadoException("Você não tem permissão para sinalizar esta tarefa.");
        }


        TarefaData tarefaData = new TarefaData();
        BeanUtils.copyProperties(tarefaDataDTO, tarefaData);
        tarefaData.setTarefa(tarefa);
        tarefaData.setUsuario_id(usuario);
        tarefaData.setAdmin(tarefa.getCriador());
        tarefaData.setSinalizadaUsuario(true);

        TarefaData tarefaDataSalva = tarefaDataRepository.save(tarefaData);

        return new TarefaDataDTO(tarefaDataSalva);
    }

    //TODO AQUI O ADMIN VAI ATUALIZAR CONFIRMANDO SE ACEITA OU NAO A CONCLUSAO DA TAREFA DO USUARIO
    // CASO NAO ACEITE ATUALIZA O CAMPO sinalizadaUsuario PARA FALSE
    // E QUANDO ACEITAR ATUALIZA O CAMPO sinalizadaUsuario PARA TRUE
    public void confirmarConclusaoTarefa(Long tarefaId, Long adminId) {
        // Recuperar o usuário logado
        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));
        // Validar se o usuário é um ADMIN
        if (!admin.getRole().equals(UsuarioRole.ADMIN)) {
            throw new AcessoNegadoException("Apenas usuários ADMIN podem confirmar a conclusão de tarefas.");
        }
        // Recuperar a tarefa
        TarefaData tarefaData = tarefaDataRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada."));
        // Validar se o ADMIN é o criador da tarefa
        if (!tarefaData.getTarefa().getCriador().getUsuarioId().equals(adminId)) {
            throw new AcessoNegadoException("Você não tem permissão para confirmar esta tarefa.");
        }
        // Validar se a tarefa foi sinalizada como concluída pelo USER
        if (!tarefaData.isConcluida()) {
            throw new TarefaNaoSinalizadaException("A tarefa ainda não foi sinalizada como concluída pelo usuário.");
        }
        // Confirmar a conclusão (opcionalmente, podemos ter outro campo de confirmação)
        tarefaData.setConcluida(true); // Já está true, mas aqui validamos o fluxo
        tarefaDataRepository.save(tarefaData);
    }


}

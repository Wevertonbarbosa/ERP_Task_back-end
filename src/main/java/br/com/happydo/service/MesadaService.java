package br.com.happydo.service;

import br.com.happydo.dto.MesadaDTO;
import br.com.happydo.exception.MesadaNaoEncontradaException;
import br.com.happydo.exception.MesadaSemPermissaoException;
import br.com.happydo.exception.UsuarioNaoEncontradoException;
import br.com.happydo.model.Mesada;
import br.com.happydo.model.Usuario;
import br.com.happydo.model.UsuarioRole;
import br.com.happydo.repository.MesadaRepository;
import br.com.happydo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MesadaService {

    @Autowired
    private MesadaRepository mesadaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public MesadaDTO salvarMesada(Long usuarioId, MesadaDTO mesadaDTO) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado");
        }

        if (!usuarioOpt.get().getRole().equals(UsuarioRole.USER)) {
            throw new MesadaSemPermissaoException("Apenas usuários do tipo USER podem receber mesada");
        }

        Mesada mesada = new Mesada();
        mesada.setUsuario(usuarioOpt.get());
        mesada.setValor(mesadaDTO.valor());
        mesada.setDataRecebimento(java.time.LocalDate.now());

        Mesada mesadaSalva = mesadaRepository.save(mesada);
        return new MesadaDTO(mesadaSalva);
    }

    public MesadaDTO buscarPorId(Long id) {
        Mesada mesada = mesadaRepository.findById(id)
                .orElseThrow(() -> new MesadaNaoEncontradaException("Mesada não encontrada"));
        return new MesadaDTO(mesada);
    }

    public List<MesadaDTO> listarMesadasPorUsuario(Long usuarioId) {
        List<Mesada> mesadas = mesadaRepository.findByUsuarioUsuarioId(usuarioId);
        return mesadas.stream().map(MesadaDTO::new).collect(Collectors.toList());
    }


    public MesadaDTO atualizarMesada(Long mesadaId, Long idUser, MesadaDTO mesadaDTO) {
        Usuario usuario = usuarioRepository.findById(idUser)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        Mesada mesada = mesadaRepository.findById(mesadaId)
                .orElseThrow(() -> new MesadaNaoEncontradaException("Mesada não encontrada"));

        mesada.setValor(mesadaDTO.valor());
        mesada.setDataRecebimento(mesadaDTO.dataRecebimento());

        mesadaRepository.save(mesada);
        return new MesadaDTO(mesada);
    }

    public void excluirMesada(Long mesadaId) {
        if (!mesadaRepository.existsById(mesadaId)) {
            throw new MesadaNaoEncontradaException("Mesada não encontrada");
        }
        mesadaRepository.deleteById(mesadaId);
    }


    public Double calcularSaldoTotal(Long usuarioId) {
        return mesadaRepository.findByUsuarioUsuarioId(usuarioId)
                .stream()
                .mapToDouble(Mesada::getValor)
                .sum();
    }


}

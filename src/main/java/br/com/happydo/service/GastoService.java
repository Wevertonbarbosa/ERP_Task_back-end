package br.com.happydo.service;

import br.com.happydo.dto.GastoDTO;
import br.com.happydo.dto.GastoTotalPorCategoriaDTO;
import br.com.happydo.exception.GastoNaoEncontradoException;
import br.com.happydo.exception.SaldoInsuficienteException;
import br.com.happydo.exception.UsuarioNaoEncontradoException;
import br.com.happydo.model.CategoriaGasto;
import br.com.happydo.model.Gasto;
import br.com.happydo.model.Usuario;
import br.com.happydo.repository.GastoRepository;
import br.com.happydo.repository.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GastoService {

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MesadaService mesadaService;


    public List<GastoDTO> listarGastosPorUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado.");
        }

        List<Gasto> gastos = gastoRepository.findByUsuarioUsuarioId(usuarioId);
        return gastos.stream().map(GastoDTO::new).toList();
    }

    public GastoDTO criarGasto(Long usuarioId, GastoDTO gastoDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        Double saldoAtual = calcularSaldoTotal(usuarioId);
        if (saldoAtual < gastoDTO.valor()) {
            throw new SaldoInsuficienteException("Saldo insuficiente para registrar este gasto.");
        }

        Gasto gasto = new Gasto();
        BeanUtils.copyProperties(gastoDTO, gasto);
        gasto.setUsuario(usuario);

        Gasto gastoSalvo = gastoRepository.save(gasto);


        atualizarSaldo(usuarioId);


        return new GastoDTO(gastoSalvo);
    }


    public GastoDTO buscarGastoPorId(Long gastoId) {
        Gasto gasto = gastoRepository.findById(gastoId)
                .orElseThrow(() -> new GastoNaoEncontradoException("Gasto não encontrado."));
        return new GastoDTO(gasto);
    }


    public GastoDTO atualizarGasto(Long gastoId, Long usuarioId, GastoDTO gastoDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        Gasto gastoExistente = gastoRepository.findById(gastoId)
                .orElseThrow(() -> new GastoNaoEncontradoException("Gasto não encontrado."));

        // Calcula o saldo sem o gasto antigo
        Double saldoAntes = calcularSaldoTotal(usuarioId) + gastoExistente.getValor();

        if (saldoAntes < gastoDTO.valor()) {
            throw new SaldoInsuficienteException("Saldo insuficiente para atualizar este gasto.");
        }

        // Atualiza os dados do gasto
        BeanUtils.copyProperties(gastoDTO, gastoExistente, "id", "usuario");

        Gasto gastoAtualizado = gastoRepository.save(gastoExistente);

        // Atualiza o saldo após a edição
        atualizarSaldo(usuarioId);

        return new GastoDTO(gastoAtualizado);
    }


    public void deletarGasto(Long gastoId) {
        Gasto gasto = gastoRepository.findById(gastoId)
                .orElseThrow(() -> new GastoNaoEncontradoException("Gasto não encontrado."));

        Long usuarioId = gasto.getUsuario().getUsuarioId();

        gastoRepository.deleteById(gastoId);

        // Atualiza o saldo após a exclusão do gasto
        atualizarSaldo(usuarioId);
    }


    public Double calcularGastoTotal(Long usuarioId) {
        return gastoRepository.findByUsuarioUsuarioId(usuarioId)
                .stream()
                .mapToDouble(Gasto::getValor)
                .sum();
    }


    public Double calcularSaldoTotal(Long usuarioId) {
        Double mesadaTotal = mesadaService.calcularSaldoTotal(usuarioId);
        Double gastosTotais = calcularGastoTotal(usuarioId);
        return mesadaTotal - gastosTotais;
    }


    private void atualizarSaldo(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        usuario.setSaldoTotal(calcularSaldoTotal(usuarioId));

        usuarioRepository.save(usuario);
    }


    public GastoTotalPorCategoriaDTO calcularGastoTotalPorCategoria(Long usuarioId) {
        List<Gasto> gastos = gastoRepository.findByUsuarioUsuarioId(usuarioId);

        double totalEssencial = gastos.stream()
                .filter(gasto -> gasto.getCategoria() == CategoriaGasto.ESSENCIAL)
                .mapToDouble(Gasto::getValor)
                .sum();

        double totalNaoEssencial = gastos.stream()
                .filter(gasto -> gasto.getCategoria() == CategoriaGasto.NAO_ESSENCIAL)
                .mapToDouble(Gasto::getValor)
                .sum();

        return new GastoTotalPorCategoriaDTO(totalEssencial, totalNaoEssencial);
    }


}

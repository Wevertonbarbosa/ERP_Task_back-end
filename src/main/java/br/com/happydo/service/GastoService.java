package br.com.happydo.service;

import br.com.happydo.dto.GastoDTO;
import br.com.happydo.dto.GastoTotalCategoriaDTO;
import br.com.happydo.dto.GastoTotalPorCategoriaAnualDTO;
import br.com.happydo.dto.GastoTotalPorCategoriaMensalDTO;
import br.com.happydo.exception.GastoNaoEncontradoException;
import br.com.happydo.exception.SaldoInsuficienteException;
import br.com.happydo.exception.UsuarioNaoEncontradoException;
import br.com.happydo.model.CategoriaGasto;
import br.com.happydo.model.Gasto;
import br.com.happydo.model.Usuario;
import br.com.happydo.model.UsuarioRole;
import br.com.happydo.repository.GastoRepository;
import br.com.happydo.repository.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        gasto.setDataGasto(java.time.LocalDate.now());

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

        Double saldoAntes = calcularSaldoTotal(usuarioId) + gastoExistente.getValor();

        if (saldoAntes < gastoDTO.valor()) {
            throw new SaldoInsuficienteException("Saldo insuficiente para atualizar este gasto.");
        }

        BeanUtils.copyProperties(gastoDTO, gastoExistente, "id", "usuario");
        gastoExistente.setDataGasto(gastoDTO.dataGasto());

        Gasto gastoAtualizado = gastoRepository.save(gastoExistente);

        atualizarSaldo(usuarioId);

        return new GastoDTO(gastoAtualizado);
    }


    public void deletarGasto(Long gastoId) {
        Gasto gasto = gastoRepository.findById(gastoId)
                .orElseThrow(() -> new GastoNaoEncontradoException("Gasto não encontrado."));

        Long usuarioId = gasto.getUsuario().getUsuarioId();

        gastoRepository.deleteById(gastoId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        if (usuario.getRole() == UsuarioRole.ADMIN) {

            usuario.setSaldoTotal(usuario.getSaldoTotal() + gasto.getValor());
        } else {

            usuario.setSaldoTotal(calcularSaldoTotal(usuarioId));
        }

        usuarioRepository.save(usuario);
    }


    public Double calcularGastoTotal(Long usuarioId) {
        return gastoRepository.findByUsuarioUsuarioId(usuarioId)
                .stream()
                .mapToDouble(Gasto::getValor)
                .sum();
    }


    public Double calcularSaldoTotal(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        Double gastosTotais = calcularGastoTotal(usuarioId);

        if (usuario.getRole() == UsuarioRole.ADMIN) {
            return usuario.getSaldoTotal() - gastosTotais;
        }

        Double mesadaTotal = mesadaService.calcularSaldoTotal(usuarioId);

        return mesadaTotal - gastosTotais;


    }


    private void atualizarSaldo(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        if (usuario.getRole() == UsuarioRole.ADMIN) {

            return;
        }


        usuario.setSaldoTotal(calcularSaldoTotal(usuarioId));

        usuarioRepository.save(usuario);
    }


    public List<GastoTotalPorCategoriaMensalDTO> calcularGastoTotalPorCategoriaMensal(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        List<Gasto> gastos = gastoRepository.findByUsuarioUsuarioId(usuarioId);

        if (gastos.isEmpty()) {
            throw new GastoNaoEncontradoException("Nenhum gasto encontrado para este usuário.");
        }

        Map<YearMonth, GastoTotalPorCategoriaMensalDTO> gastosPorMes = gastos.stream()
                .collect(Collectors.groupingBy(
                        gasto -> YearMonth.from(gasto.getDataGasto()),
                        Collectors.collectingAndThen(Collectors.toList(), listaGastos -> {

                            double totalEssencial = listaGastos.stream()
                                    .filter(g -> g.getCategoria() == CategoriaGasto.ESSENCIAL)
                                    .mapToDouble(Gasto::getValor)
                                    .sum();

                            double totalNaoEssencial = listaGastos.stream()
                                    .filter(g -> g.getCategoria() == CategoriaGasto.NAO_ESSENCIAL)
                                    .mapToDouble(Gasto::getValor)
                                    .sum();

                            return new GastoTotalPorCategoriaMensalDTO(
                                    YearMonth.from(listaGastos.get(0).getDataGasto()),
                                    totalEssencial,
                                    totalNaoEssencial
                            );
                        })
                ));

        return new ArrayList<>(gastosPorMes.values());
    }

    public GastoTotalCategoriaDTO GastoTotalPorCategoria(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        List<Gasto> gastos = gastoRepository.findByUsuarioUsuarioId(usuarioId);

        if (gastos.isEmpty()) {
            throw new GastoNaoEncontradoException("Nenhum gasto encontrado para este usuário.");
        }

        double totalEssencial = gastos.stream()
                .filter(g -> g.getCategoria() == CategoriaGasto.ESSENCIAL)
                .mapToDouble(Gasto::getValor)
                .sum();

        double totalNaoEssencial = gastos.stream()
                .filter(g -> g.getCategoria() == CategoriaGasto.NAO_ESSENCIAL)
                .mapToDouble(Gasto::getValor)
                .sum();

        return new GastoTotalCategoriaDTO(totalEssencial, totalNaoEssencial);
    }


    public GastoTotalPorCategoriaAnualDTO calcularGastoTotalPorCategoriaAnual(Long usuarioId, int ano) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        List<Gasto> gastos = gastoRepository.findByUsuarioUsuarioId(usuarioId);

        if (gastos.isEmpty()) {
            throw new GastoNaoEncontradoException("Nenhum gasto encontrado para este usuário.");
        }

        double totalEssencial = gastos.stream()
                .filter(g -> g.getCategoria() == CategoriaGasto.ESSENCIAL && g.getDataGasto().getYear() == ano)
                .mapToDouble(Gasto::getValor)
                .sum();

        double totalNaoEssencial = gastos.stream()
                .filter(g -> g.getCategoria() == CategoriaGasto.NAO_ESSENCIAL && g.getDataGasto().getYear() == ano)
                .mapToDouble(Gasto::getValor)
                .sum();

        double totalAnual = totalEssencial + totalNaoEssencial;

        return new GastoTotalPorCategoriaAnualDTO(ano, totalEssencial, totalNaoEssencial, totalAnual);
    }


}

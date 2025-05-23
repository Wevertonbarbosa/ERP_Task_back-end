package br.com.happydo.service;

import br.com.happydo.dto.MesadaDTO;
import br.com.happydo.dto.PainelDesempenhoDTO;
import br.com.happydo.exception.MesadaJaAddNoMes;
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

import java.time.LocalDate;
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
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        LocalDate hoje = LocalDate.now();
        int ano = hoje.getYear();
        int mes = hoje.getMonthValue();

        // Verifica se já existe uma mesada para o mesmo mês e ano
        Optional<Mesada> mesadaExistente = mesadaRepository.findByUsuarioAndMes(usuarioId, ano, mes);

        if (mesadaExistente.isEmpty()) {
            Mesada mesada = new Mesada();
            mesada.setUsuario(usuario);
            mesada.setAnoReferencia(ano);
            mesada.setMesReferencia(mes);
            mesada.setTotalPontosPeriodo(mesadaDTO.totalPontosPeriodo());
            mesada.setPontosConcluidos(usuario.getPontuacaoAcumulada());
            mesada.setDataRecebimento(LocalDate.now());
            mesada.setPercentualConclusao(0.0);
            mesada.setValorProporcional(0.0);
            mesada.setMesadaRecebida(false);

            // Usa o valor da mesada vindo do DTO (definido pelo mentor)
            Double valorDefinidoPeloMentor = mesadaDTO.valor() != null ? mesadaDTO.valor() : 0.0;

            Double valorCalculado = calcularValorProporcional(
                    mesada.getTotalPontosPeriodo(),
                    mesada.getPontosConcluidos(),
                    valorDefinidoPeloMentor
            );

            mesada.setValor(mesadaDTO.valor());


            usuario.setSaldoTotal(valorCalculado);

            usuario.setValorMesadaMensal(mesada.getValor());

            Mesada mesadaSalva = mesadaRepository.save(mesada);
            usuarioRepository.save(usuario);

            return new MesadaDTO(mesadaSalva);
        } else {
            throw new MesadaJaAddNoMes("A mesada desse mês para esse usuários já foi add.");
        }

    }


    private Double calcularValorProporcional(Integer totalPontos, Integer pontosConcluidos, Double valorMesada) {
        if (totalPontos == null || totalPontos == 0 || pontosConcluidos == null) return 0.0;
        double proporcao = (double) pontosConcluidos / totalPontos;
        return Math.round(proporcao * valorMesada * 100.0) / 100.0;
    }

    public void adicionarPontosConcluidosNaMesada(Long usuarioId, int pontosTarefa) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        LocalDate hoje = LocalDate.now();
        int ano = hoje.getYear();
        int mes = hoje.getMonthValue();

        Optional<Mesada> mesadaOptional = mesadaRepository.findByUsuarioAndMes(usuarioId, ano, mes);

        if (mesadaOptional.isPresent()) {
            Mesada mesadaExistente = mesadaOptional.get();

            int pontosAtuais = mesadaExistente.getPontosConcluidos() != null ? mesadaExistente.getPontosConcluidos() : 0;

            mesadaExistente.setPontosConcluidos(pontosAtuais + pontosTarefa);


            mesadaRepository.save(mesadaExistente);


        } else {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado.");
        }

    }

    public void atualizarDesempenhoMesada(Mesada mesada, Double valorMesadaMensal) {
        int totalPontos = mesada.getTotalPontosPeriodo() != null ? mesada.getTotalPontosPeriodo() : 0;
        int pontosConcluidos = mesada.getPontosConcluidos() != null ? mesada.getPontosConcluidos() : 0;

        double percentualConclusao = 0.0;
        double valorProporcional = 0.0;

        if (totalPontos > 0) {
            percentualConclusao = (pontosConcluidos * 100.0) / totalPontos;
            valorProporcional = (valorMesadaMensal * pontosConcluidos) / totalPontos;
        }

        mesada.setPercentualConclusao(percentualConclusao);
        mesada.setValorProporcional(valorProporcional);
    }

    public PainelDesempenhoDTO obterPainelDesempenho(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        LocalDate hoje = LocalDate.now();
        int ano = hoje.getYear();
        int mes = hoje.getMonthValue();

        Mesada mesada = mesadaRepository.findByUsuarioAndMes(usuarioId, ano, mes)
                .orElseThrow(() -> new MesadaNaoEncontradaException("Mesada não encontrada para o usuário neste período."));

        return new PainelDesempenhoDTO(mesada);
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

        usuario.setSaldoTotal(usuario.getSaldoTotal() - mesada.getValor());

        usuario.setValorMesadaMensal(mesadaDTO.valor());

        mesada.setTotalPontosPeriodo(mesadaDTO.totalPontosPeriodo());

        mesada.setPontosConcluidos(mesada.getPontosConcluidos());
        mesada.setMesadaRecebida(mesada.getMesadaRecebida());
        mesada.setValorProporcional(mesada.getValorProporcional());
        mesada.setPercentualConclusao(mesada.getPercentualConclusao());

        //MES PRECISA SER PREENCHIDO
        mesada.setMesReferencia(mesadaDTO.mesReferencia());
        //ANO PRECISA SER PREENCHIDO
        mesada.setAnoReferencia(mesadaDTO.anoReferencia());

        Double novoValor = calcularValorProporcional(
                mesadaDTO.totalPontosPeriodo(),
                mesadaDTO.pontosConcluidos(),
                mesadaDTO.valor()
        );

        mesada.setValor(mesadaDTO.valor());
        mesada.setDataRecebimento(mesadaDTO.dataRecebimento());


        usuario.setSaldoTotal(usuario.getSaldoTotal() + novoValor);

        usuarioRepository.save(usuario);
        mesadaRepository.save(mesada);
        return new MesadaDTO(mesada);
    }

    public void excluirMesada(Long mesadaId) {
        Mesada mesada = mesadaRepository.findById(mesadaId)
                .orElseThrow(() -> new MesadaNaoEncontradaException("Mesada não encontrada"));

        Usuario usuario = mesada.getUsuario();

        if (mesada.getMesadaRecebida().equals(false)) {
            double novoSaldo = usuario.getSaldoTotal() - mesada.getValor();
            usuario.setSaldoTotal(Math.max(0.0, novoSaldo));

            usuario.setValorMesadaMensal(Math.max(0.0, novoSaldo));

            usuarioRepository.save(usuario);
            mesadaRepository.deleteById(mesadaId);

        } else {
            throw new MesadaJaAddNoMes("A mesada já foi recebida e não pode mais ser excluída.");
        }


    }

    public Double calcularSaldoTotal(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        if (usuario.getRole() == UsuarioRole.ADMIN) {

            return usuario.getSaldoTotal();
        }

        return mesadaRepository.findByUsuarioUsuarioId(usuarioId)
                .stream()
                .mapToDouble(Mesada::getValor)
                .sum();
    }


}

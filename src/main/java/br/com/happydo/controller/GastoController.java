package br.com.happydo.controller;

import br.com.happydo.dto.GastoDTO;
import br.com.happydo.dto.GastoTotalCategoriaDTO;
import br.com.happydo.dto.GastoTotalPorCategoriaAnualDTO;
import br.com.happydo.dto.GastoTotalPorCategoriaMensalDTO;
import br.com.happydo.exception.GastoNaoEncontradoException;
import br.com.happydo.exception.UsuarioNaoEncontradoException;
import br.com.happydo.service.GastoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gasto")
public class GastoController {

    @Autowired
    private GastoService gastoService;

    @GetMapping("/{usuarioId}/gastos")
    public ResponseEntity<List<GastoDTO>> listarGastosPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<GastoDTO> gastos = gastoService.listarGastosPorUsuario(usuarioId);
            return ResponseEntity.ok(gastos);
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @GetMapping("/{usuarioId}/gastos/total")
    public ResponseEntity<Double> calcularGastoTotal(@PathVariable Long usuarioId) {
        try {
            Double gastoTotal = gastoService.calcularGastoTotal(usuarioId);
            return ResponseEntity.ok(gastoTotal);
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @GetMapping("/{usuarioId}/gastos/categoria/mensal")
    public ResponseEntity<List<GastoTotalPorCategoriaMensalDTO>> calcularGastoTotalPorCategoria(@PathVariable Long usuarioId) {
        try {
            List<GastoTotalPorCategoriaMensalDTO> gastosTotais = gastoService.calcularGastoTotalPorCategoriaMensal(usuarioId);
            return ResponseEntity.ok(gastosTotais);
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (GastoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @GetMapping("/{usuarioId}/gastos/categoria/anual/{ano}")
    public ResponseEntity<GastoTotalPorCategoriaAnualDTO> calcularGastoTotalPorCategoriaAnual(
            @PathVariable Long usuarioId,
            @PathVariable int ano) {
        try {
            GastoTotalPorCategoriaAnualDTO gastoTotal = gastoService.calcularGastoTotalPorCategoriaAnual(usuarioId, ano);
            return ResponseEntity.ok(gastoTotal);
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (GastoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }


    @GetMapping("/{usuarioId}/gastos/total/categoria")
    public ResponseEntity<GastoTotalCategoriaDTO> calcularGastoTotalCategoria(@PathVariable Long usuarioId) {
        try {
            GastoTotalCategoriaDTO totalCategoria = gastoService.GastoTotalPorCategoria(usuarioId);
            return ResponseEntity.ok(totalCategoria);
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (GastoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }


    @PostMapping("/{usuarioId}/registro")
    public ResponseEntity<GastoDTO> criarGasto(
            @PathVariable Long usuarioId,
            @RequestBody @Valid GastoDTO gastoDTO) {
        try {
            GastoDTO gastoCriado = gastoService.criarGasto(usuarioId, gastoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(gastoCriado);
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @GetMapping("/{gastoId}")
    public ResponseEntity<GastoDTO> buscarGastoPorId(@PathVariable Long gastoId) {
        try {
            GastoDTO gastoDTO = gastoService.buscarGastoPorId(gastoId);
            return ResponseEntity.ok(gastoDTO);
        } catch (GastoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @PutMapping("/{usuarioId}/atualizar/{gastoId}")
    public ResponseEntity<GastoDTO> atualizarGasto(
            @PathVariable Long usuarioId,
            @PathVariable Long gastoId,
            @RequestBody @Valid GastoDTO gastoDTO) {
        try {
            GastoDTO gastoAtualizado = gastoService.atualizarGasto(gastoId, usuarioId, gastoDTO);
            return ResponseEntity.ok(gastoAtualizado);
        } catch (UsuarioNaoEncontradoException | GastoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @DeleteMapping("/{gastoId}")
    public ResponseEntity<Void> deletarGasto(@PathVariable Long gastoId) {
        try {
            gastoService.deletarGasto(gastoId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (GastoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


}

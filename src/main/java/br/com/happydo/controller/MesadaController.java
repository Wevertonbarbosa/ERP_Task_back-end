package br.com.happydo.controller;

import br.com.happydo.dto.MesadaDTO;
import br.com.happydo.service.MesadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mesada")
public class MesadaController {

    @Autowired
    private MesadaService mesadaService;

    // Buscar todas as mesadas de um usuário
    @GetMapping("/{usuarioId}/mesadas")
    public ResponseEntity<List<MesadaDTO>> listarMesadasPorUsuario(@PathVariable Long usuarioId) {
        List<MesadaDTO> mesadas = mesadaService.listarMesadasPorUsuario(usuarioId);
        return ResponseEntity.ok(mesadas);
    }

    // BUSCAR MESADA PELO ID
    @GetMapping("/{mesadaId}")
    public ResponseEntity<MesadaDTO> buscarMesadaPorId(@PathVariable Long mesadaId) {
        MesadaDTO mesadaDTO = mesadaService.buscarPorId(mesadaId);
        return ResponseEntity.ok(mesadaDTO);
    }

    //BUSCA O SALDO TOTAL DO USUARIO MEDIANTE A QUANTIDADE DE MESADA RECEBIDA
    @GetMapping("/saldo/{usuarioId}")
    public ResponseEntity<Double> calcularSaldoTotal(@PathVariable Long usuarioId) {
        Double saldoTotal = mesadaService.calcularSaldoTotal(usuarioId);
        return ResponseEntity.ok(saldoTotal);
    }



    // Criar nova mesada para um usuário
    @PostMapping("/{usuarioId}")
    public ResponseEntity<MesadaDTO> criarMesada(@PathVariable Long usuarioId, @RequestBody MesadaDTO mesadaDTO) {
        MesadaDTO novaMesada = mesadaService.salvarMesada(usuarioId, mesadaDTO);
        return ResponseEntity.ok(novaMesada);
    }

    // Atualizar mesada
    @PutMapping("/{mesadaId}/{idUser}")
    public ResponseEntity<MesadaDTO> atualizarMesada(@PathVariable Long mesadaId, @PathVariable Long idUser, @RequestBody MesadaDTO mesadaDTO) {
        MesadaDTO mesadaAtualizada = mesadaService.atualizarMesada(mesadaId, idUser, mesadaDTO);
        return ResponseEntity.ok(mesadaAtualizada);
    }

    // Excluir mesada
    @DeleteMapping("/{mesadaId}")
    public ResponseEntity<Void> excluirMesada(@PathVariable Long mesadaId) {
        mesadaService.excluirMesada(mesadaId);
        return ResponseEntity.noContent().build();
    }
}

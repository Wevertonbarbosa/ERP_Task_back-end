package br.com.happydo.controller;

import br.com.happydo.dto.MesadaDTO;
import br.com.happydo.dto.PainelDesempenhoDTO;
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


    @GetMapping("/{usuarioId}/mesadas")
    public ResponseEntity<List<MesadaDTO>> listarMesadasPorUsuario(@PathVariable Long usuarioId) {
        List<MesadaDTO> mesadas = mesadaService.listarMesadasPorUsuario(usuarioId);
        return ResponseEntity.ok(mesadas);
    }

    @GetMapping("/{mesadaId}")
    public ResponseEntity<MesadaDTO> buscarMesadaPorId(@PathVariable Long mesadaId) {
        MesadaDTO mesadaDTO = mesadaService.buscarPorId(mesadaId);
        return ResponseEntity.ok(mesadaDTO);
    }

    @GetMapping("/saldo/{usuarioId}")
    public ResponseEntity<Double> calcularSaldoTotal(@PathVariable Long usuarioId) {
        Double saldoTotal = mesadaService.calcularSaldoTotal(usuarioId);
        return ResponseEntity.ok(saldoTotal);
    }

    @GetMapping("/painel-desempenho/{usuarioId}")
    public ResponseEntity<PainelDesempenhoDTO> getPainelDesempenho(@PathVariable Long usuarioId) {
        PainelDesempenhoDTO painel = mesadaService.obterPainelDesempenho(usuarioId);
        return ResponseEntity.ok(painel);
    }


    @PostMapping("/{usuarioId}")
    public ResponseEntity<MesadaDTO> criarMesada(@PathVariable Long usuarioId, @RequestBody MesadaDTO mesadaDTO) {
        MesadaDTO novaMesada = mesadaService.salvarMesada(usuarioId, mesadaDTO);
        return ResponseEntity.ok(novaMesada);
    }

    @PutMapping("/{mesadaId}/{idUser}")
    public ResponseEntity<MesadaDTO> atualizarMesada(@PathVariable Long mesadaId, @PathVariable Long idUser, @RequestBody MesadaDTO mesadaDTO) {
        MesadaDTO mesadaAtualizada = mesadaService.atualizarMesada(mesadaId, idUser, mesadaDTO);
        return ResponseEntity.ok(mesadaAtualizada);
    }

    @DeleteMapping("/{mesadaId}")
    public ResponseEntity<Void> excluirMesada(@PathVariable Long mesadaId) {
        mesadaService.excluirMesada(mesadaId);
        return ResponseEntity.noContent().build();
    }
}

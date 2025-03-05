package br.com.happydo.controller;

import br.com.happydo.dto.AtualizarSaldoDTO;
import br.com.happydo.dto.UsuarioCadastroDTO;
import br.com.happydo.dto.UsuarioExibitionDTO;
import br.com.happydo.dto.UsuarioTarefasExibitionDTO;
import br.com.happydo.exception.UsuarioNaoEncontradoException;
import br.com.happydo.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class UsuarioController {


    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/{adminId}/mentorados")
    public ResponseEntity<List<UsuarioExibitionDTO>> listarMentorados(@PathVariable Long adminId) {
        List<UsuarioExibitionDTO> mentorados = usuarioService.listarMentorados(adminId);
        return ResponseEntity.ok(mentorados);
    }

    @PostMapping("/{adminId}/mentorados")
    public ResponseEntity<UsuarioExibitionDTO> criarMentorado(
            @PathVariable Long adminId,
            @RequestBody @Valid UsuarioCadastroDTO usuarioCadastroDTO) {

        try {
            UsuarioExibitionDTO usuarioExibitionDTO = usuarioService.criarUsuario(usuarioCadastroDTO, adminId);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioExibitionDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioExibitionDTO> criarAdmin(
            @RequestBody @Valid UsuarioCadastroDTO usuarioCadastroDTO) {

        try {
            UsuarioExibitionDTO usuarioExibitionDTO = usuarioService.criarUsuario(usuarioCadastroDTO, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioExibitionDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioExibitionDTO> buscarUsuarioPorId(@PathVariable Long id) {
        try {
            UsuarioExibitionDTO usuarioExibitionDTO = usuarioService.buscarUsuarioPorId(id);
            return new ResponseEntity<>(usuarioExibitionDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<UsuarioExibitionDTO>> listarTodosUsuarios() {
        List<UsuarioExibitionDTO> usuarios = usuarioService.listarTodosUsuarios();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/{usuarioId}/tarefas")
    public ResponseEntity<UsuarioTarefasExibitionDTO> getTarefasUsuarioStatus(@PathVariable Long usuarioId) {
        UsuarioTarefasExibitionDTO tarefas = usuarioService.tarefasStatus(usuarioId);
        return ResponseEntity.ok(tarefas);
    }


    @PutMapping("/{id}")
    public ResponseEntity<UsuarioExibitionDTO> atualizarUsuario(
            @PathVariable Long id,
            @RequestBody @Valid UsuarioCadastroDTO usuarioCadastroDTO) {

        UsuarioExibitionDTO usuarioAtualizado = usuarioService.atualizarUsuario(id, usuarioCadastroDTO);
        return ResponseEntity.ok(usuarioAtualizado);

    }

    @PutMapping("/{id}/saldo")
    public ResponseEntity<UsuarioExibitionDTO> atualizarSaldo(
            @PathVariable Long id,
            @RequestBody AtualizarSaldoDTO saldoDTO) {

        UsuarioExibitionDTO usuarioAtualizado = usuarioService.atualizarSaldoUsuario(id, saldoDTO);
        return ResponseEntity.ok(usuarioAtualizado);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        try {
            usuarioService.deleteUsuario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado.");
        }
    }


}

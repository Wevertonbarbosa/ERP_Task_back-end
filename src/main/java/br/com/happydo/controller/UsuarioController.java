package br.com.happydo.controller;

import br.com.happydo.dto.UsuarioCadastroDTO;
import br.com.happydo.dto.UsuarioExibitionDTO;
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



    // Criar usuario
    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioExibitionDTO> criarUsuario(@RequestBody @Valid UsuarioCadastroDTO usuarioCadastroDTO) {
        try {
            UsuarioExibitionDTO usuarioExibitionDTO = usuarioService.criarUsuario(usuarioCadastroDTO);
            return new ResponseEntity<>(usuarioExibitionDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Buscar usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioExibitionDTO> buscarUsuarioPorId(@PathVariable Long id) {
        try {
            UsuarioExibitionDTO usuarioExibitionDTO = usuarioService.buscarUsuarioPorId(id);
            return new ResponseEntity<>(usuarioExibitionDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Listar todos os usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioExibitionDTO>> listarTodosUsuarios() {
        List<UsuarioExibitionDTO> usuarios = usuarioService.listarTodosUsuarios();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    //Atualiza os Usuarios
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioExibitionDTO> atualizarUsuario(
            @PathVariable Long id,
            @RequestBody @Valid UsuarioCadastroDTO usuarioCadastroDTO) {

        UsuarioExibitionDTO usuarioAtualizado = usuarioService.atualizarUsuario(id, usuarioCadastroDTO);
        return ResponseEntity.ok(usuarioAtualizado);

    }


    // Deletar usuario
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

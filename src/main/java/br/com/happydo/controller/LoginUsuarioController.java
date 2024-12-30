package br.com.happydo.controller;

import br.com.happydo.dto.LoginUsuarioDTO;
import br.com.happydo.dto.UsuarioExibitionDTO;
import br.com.happydo.exception.UsuarioNaoEncontradoException;
import br.com.happydo.service.LoginUsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LoginUsuarioController {

    @Autowired
    private LoginUsuarioService loginUsuarioService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginUsuarioDTO loginDTO) {
        try {
            UsuarioExibitionDTO usuario = loginUsuarioService.autenticarUsuario(loginDTO);
            return ResponseEntity.ok(usuario); // Retorna o DTO em caso de sucesso
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro no servidor!");
        }
    }
}

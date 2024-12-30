package br.com.happydo.controller;

import br.com.happydo.dto.UsuarioNovaSenhaDTO;
import br.com.happydo.exception.UsuarioNaoEncontradoException;
import br.com.happydo.service.RedefinirSenhaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class RedefinirSenhaController {

    @Autowired
    private RedefinirSenhaService redefinirSenhaService;


    @PostMapping("/esqueci-senha")
    public ResponseEntity<String> solicitarRedefinicaoSenha(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email"); // Extrai o e-mail do corpo JSON
        try {
            redefinirSenhaService.redefinicaoSenha(email);
            return ResponseEntity.ok("Email enviado com sucesso!");
        } catch (RuntimeException e) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado.");
        }
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<String> atualizarNovaSenha(@RequestBody @Valid UsuarioNovaSenhaDTO usuarioNovaSenhaDTO) {
        try {
            redefinirSenhaService.novaSenha(usuarioNovaSenhaDTO.email(), usuarioNovaSenhaDTO.novaSenha());
            return ResponseEntity.ok("Senha redefinida com sucesso!");
        } catch (UsuarioNaoEncontradoException e) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado.");
        }
    }


}

package br.com.happydo.service;

import br.com.happydo.exception.UsuarioNaoEncontradoException;
import br.com.happydo.model.Usuario;
import br.com.happydo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedefinirSenhaService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailUsuarioService emailUsuarioService;





    //Redefinicao de senha
    public void redefinicaoSenha(String emailUsuario) {
        if (usuarioRepository.existsByEmail(emailUsuario)) {
            // Gerar o link para redefinir a senha (aqui estamos usando uma URL simples)
            String linkRedefinicao = "http://localhost:8080/redefinir-senha?email=" + emailUsuario;
            String assunto = "Solicitação de Redefinição de Senha";
            String mensagem = "Você solicitou a redefinição de sua senha. Clique no link abaixo para redefinir a sua senha:\n" + linkRedefinicao;

            emailUsuarioService.enviarEmail(emailUsuario, assunto, mensagem);
        } else {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado.");
        }
    }


    //Validacao e atualizacao de senha
    public void novaSenha(String email, String novaSenha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        usuario.setSenha(novaSenha);
        usuarioRepository.save(usuario);
    }





}

package br.com.happydo.service;

import br.com.happydo.exception.UsuarioNaoEncontradoException;
import br.com.happydo.model.Usuario;
import br.com.happydo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RedefinirSenhaService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailUsuarioService emailUsuarioService;

    public void redefinicaoSenha(String emailUsuario) {
        if (usuarioRepository.existsByEmail(emailUsuario)) {
            String linkRedefinicao = "http://localhost:4200/redefinir";
            String assunto = "Solicitação de Redefinição de Senha";
            String mensagem = "Para redefinir sua senha,\nClique no link e seja redirecionado: " + linkRedefinicao;

            emailUsuarioService.enviarEmail(emailUsuario, assunto, mensagem);
        } else {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado.");
        }
    }

    public void novaSenha(String email, String novaSenha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        String senhaCriptografada = new BCryptPasswordEncoder().encode(novaSenha);

        usuario.setSenha(senhaCriptografada);
        usuarioRepository.save(usuario);
    }


}

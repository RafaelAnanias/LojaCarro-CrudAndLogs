package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.CarroException;
import br.org.edu.ifrn.LojaCarro.model.Usuario;
import br.org.edu.ifrn.LojaCarro.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    // Logger SLF4J / Log4j
    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario salvar(Usuario usuario) {
        log.info("Tentativa de salvar usuário: {}", usuario.getNome());

        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            log.warn("Falha de validação: nome do usuário está vazio.");
            throw new CarroException("O nome do usuário não pode ser vazio.");
        }

        if (usuario.getCargo() == null) {
            log.warn("Falha de validação: cargo não informado para o usuário {}", usuario.getNome());
            throw new CarroException("O cargo do usuário é obrigatório.");
        }

        try {
            Usuario usuarioSalvo = usuarioRepository.save(usuario);
            log.info("Usuário salvo com sucesso! ID: {}, Cargo: {}", usuarioSalvo.getId(), usuarioSalvo.getCargo());
            return usuarioSalvo;
        } catch (Exception e) {
            log.error("Erro inesperado ao salvar usuário no banco de dados: {}", e.getMessage(), e);
            throw new CarroException("Erro ao persistir usuário: " + e.getMessage());
        }
    }

    public List<Usuario> listarTodos() {
        log.info("Buscando lista de todos os usuários cadastrados.");
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        log.info("Buscando usuário pelo ID: {}", id);
        return usuarioRepository.findById(id).orElseThrow(() -> {
            log.warn("Usuário com ID {} não foi localizado.", id);
            return new CarroException("Usuário não encontrado para o ID: " + id);
        });
    }

    public Usuario atualizar(Long id, Usuario dadosAtualizados) {
        log.info("Atualizando dados do usuário ID: {}", id);
        Usuario usuarioExistente = buscarPorId(id);

        if (dadosAtualizados.getNome() == null || dadosAtualizados.getNome().trim().isEmpty()) {
            log.warn("Falha na atualização: nome vazio para o ID {}", id);
            throw new CarroException("O nome não pode estar em branco.");
        }

        usuarioExistente.setNome(dadosAtualizados.getNome());
        usuarioExistente.setCargo(dadosAtualizados.getCargo());

        Usuario atualizado = usuarioRepository.save(usuarioExistente);
        log.info("Usuário ID {} atualizado com sucesso.", atualizado.getId());
        return atualizado;
    }

    public void deletar(Long id) {
        log.info("Tentando excluir usuário ID: {}", id);
        if (!usuarioRepository.existsById(id)) {
            log.warn("Não foi possível excluir: ID {} inexistente.", id);
            throw new CarroException("Usuário com ID " + id + " não existe.");
        }
        usuarioRepository.deleteById(id);
        log.info("Usuário ID {} removido com sucesso.", id);
    }
}
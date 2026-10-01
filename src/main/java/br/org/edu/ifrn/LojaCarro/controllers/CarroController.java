package br.org.edu.ifrn.LojaCarro.controllers;

import br.org.edu.ifrn.LojaCarro.CarroException;
import br.org.edu.ifrn.LojaCarro.model.Cargo;
import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.model.Usuario;
import br.org.edu.ifrn.LojaCarro.services.CarroService;
import br.org.edu.ifrn.LojaCarro.services.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carro")
@CrossOrigin(origins = "*")
public class CarroController {

    private static final Logger log = LoggerFactory.getLogger(CarroController.class);

    @Autowired
    private CarroService carroService;

    @Autowired
    private UsuarioService usuarioService;

    // Salvar carro (apenas GERENTE ou ADMIN podem cadastrar)
    @PostMapping("/salvar")
    public ResponseEntity<Carro> salvarCarro(@RequestParam Long usuarioId, @RequestBody Carro c) {
        log.info("Tentativa de cadastro de veículo solicitada pelo usuário ID: {}", usuarioId);

        Usuario usuario = usuarioService.buscarPorId(usuarioId);

        if (usuario.getCargo() != Cargo.GERENTE && usuario.getCargo() != Cargo.ADMIN) {
            log.warn("Permissão negada: Usuário '{}' com cargo '{}' tentou cadastrar um carro.",
                    usuario.getNome(), usuario.getCargo());
            throw new CarroException("Acesso negado: Apenas Gerente ou Admin pode cadastrar veículos.");
        }

        Carro savedCarro = carroService.save(c);
        log.info("Carro modelo '{}' (ID: {}) cadastrado com sucesso pelo usuário '{}'.",
                savedCarro.getModelo(), savedCarro.getId(), usuario.getNome());

        return ResponseEntity.ok(savedCarro);
    }

    // Atualizar carro (por ID)
    @PutMapping("/{id}")
    public ResponseEntity<Carro> atualizarCarro(@PathVariable Long id, @RequestBody Carro c) {
        log.info("Atualizando carro com ID: {}", id);
        c.setId(id);
        Carro updatedCarro = carroService.update(c);
        log.info("Carro ID: {} atualizado com sucesso.", updatedCarro.getId());
        return ResponseEntity.ok(updatedCarro);
    }

    // Deletar carro (por ID)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCarro(@PathVariable Long id) {
        log.info("Excluindo carro com ID: {}", id);
        carroService.deleteById(id);
        log.info("Carro com ID: {} excluído com sucesso.", id);
        return ResponseEntity.noContent().build();
    }

    // Pesquisar carro por ID
    @GetMapping("/{id}")
    public ResponseEntity<Carro> pesquisarCarroPorId(@PathVariable Long id) {
        log.info("Buscando carro com ID: {}", id);
        Optional<Carro> carro = carroService.findById(id);
        if (carro.isEmpty()) {
            log.warn("Carro com ID: {} não foi encontrado.", id);
        }
        return carro.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Pesquisar todos os carros
    @GetMapping("/listarCarros")
    public ResponseEntity<List<Carro>> pesquisarTodosCarros() {
        log.info("Listando todos os carros cadastrados.");
        List<Carro> carros = carroService.findAll();
        return ResponseEntity.ok(carros);
    }

    @PostMapping(value = "/getCarro", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Carro> pesquisarCarroPorModelo(@RequestBody String modelo) {
        log.info("Pesquisando carro pelo modelo: '{}'", modelo.trim());
        Optional<Carro> carro = carroService.findByModelo(modelo.trim());
        if (carro.isEmpty()) {
            log.warn("Nenhum carro encontrado para o modelo: '{}'", modelo.trim());
        }
        return carro.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/deleteCarro", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> deletarCarroPorModelo(@RequestBody String modelo) {
        log.info("Tentativa de exclusão do carro pelo modelo: '{}'", modelo.trim());
        Carro carro = carroService.deleteByModelo(modelo.trim());
        log.info("Carro modelo '{}' excluído com sucesso.", carro.getModelo());
        return ResponseEntity.ok("Carro deletado: " + carro.getModelo());
    }

    @PostMapping(value = "/updateCarro", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Carro> atualizarCarroPorModelo(@RequestBody String payload) {
        log.info("Recebida requisição legado para atualizar carro por modelo: {}", payload);
        String[] partes = payload.split(",", 2);
        if (partes.length < 2) {
            log.warn("Payload inválido recebido em /updateCarro: {}", payload);
            throw new CarroException("Payload inválido. Use modelo,preco.");
        }
        String modelo = partes[0].trim();
        double preco = parsePreco(partes[1].trim());
        Carro updatedCarro = carroService.updateByModelo(modelo, preco);
        log.info("Carro modelo '{}' atualizado com novo preço: {}", modelo, preco);
        return ResponseEntity.ok(updatedCarro);
    }

    @PostMapping(value = "/teste", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> bomDia(@RequestBody String nome) {
        log.info("Endpoint /teste acionado com o nome: {}", nome.trim());
        return ResponseEntity.ok("Bom dia, " + nome.trim());
    }

    private double parsePreco(String preco) {
        try {
            return Double.parseDouble(preco);
        } catch (NumberFormatException ex) {
            log.error("Erro ao converter valor do preço '{}': {}", preco, ex.getMessage());
            throw new CarroException("Preço inválido: " + preco);
        }
    }
}
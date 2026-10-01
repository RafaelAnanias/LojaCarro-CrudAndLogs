package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.CarroException;
import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.repository.CarroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CarroService {

    private static final Logger log = LoggerFactory.getLogger(CarroService.class);

    @Autowired
    public CarroRepository carroRepository;

    public Carro save(Carro c) {
        log.info("Iniciando processo de cadastro para o modelo: {}", c.getModelo());
        validarModelo(c.getModelo());
        validarPreco(c.getPreco());
        Carro salvo = carroRepository.save(c);
        log.info("Carro salvo com sucesso no banco. ID: {}", salvo.getId());
        return salvo;
    }

    // Deletar por ID
    public void deleteById(Long id) {
        log.info("Solicitação para deletar carro com ID: {}", id);
        if (id <= 0) {
            log.warn("Tentativa de exclusão com ID inválido: {}", id);
            throw new CarroException("O ID do carro não pode ser negativo. ID fornecido: " + id);
        }
        if (!carroRepository.existsById(id)) {
            log.warn("Carro com ID {} não encontrado para exclusão.", id);
            throw new CarroException("Carro não encontrado para o ID: " + id);
        }
        carroRepository.deleteById(id);
        log.info("Carro com ID {} deletado com sucesso.", id);
    }

    // Pesquisar por ID
    public Optional<Carro> findById(Long id) {
        log.info("Consultando carro por ID: {}", id);
        if (id <= 0) {
            log.warn("Tentativa de consulta com ID inválido: {}", id);
            throw new CarroException("O ID do carro não pode ser negativo. ID fornecido: " + id);
        }
        return carroRepository.findById(id);
    }

    // Listar todos os carros
    public List<Carro> findAll() {
        log.info("Buscando todos os carros no banco de dados.");
        return carroRepository.findAll();
    }

    public Optional<Carro> findByModelo(String modelo) {
        log.info("Buscando carro pelo modelo: {}", modelo);
        validarModelo(modelo);
        return carroRepository.findFirstByModelo(modelo);
    }

    public Carro saveFromLegacy(String modelo, double preco) {
        log.info("Salvando carro via método legado. Modelo: {}, Preço: {}", modelo, preco);
        Carro carro = new Carro(modelo, LocalDate.now().getYear(), preco);
        return save(carro);
    }

    public Carro updateByModelo(String modelo, double preco) {
        log.info("Atualizando preço do modelo '{}' para {}", modelo, preco);
        Carro carro = localizarCarroPorModelo(modelo);
        validarPreco(preco);
        carro.setPreco(preco);
        return carroRepository.save(carro);
    }

    public Carro deleteByModelo(String modelo) {
        log.info("Deletando carro pelo modelo: {}", modelo);
        Carro carro = localizarCarroPorModelo(modelo);
        carroRepository.delete(carro);
        log.info("Carro modelo '{}' deletado do banco.", modelo);
        return carro;
    }

    // Método para atualizar
    public Carro update(Carro c) {
        log.info("Atualizando carro com ID: {}", c.getId());
        if (c.getId() == null) {
            log.warn("Tentativa de atualização com ID nulo.");
            throw new CarroException("O ID do carro para atualização não pode ser nulo.");
        }
        if (!carroRepository.existsById(c.getId())) {
            log.warn("Carro com ID {} não localizado para atualização.", c.getId());
            throw new CarroException("Carro com ID " + c.getId() + " não encontrado para atualização.");
        }
        validarModelo(c.getModelo());
        validarPreco(c.getPreco());
        return carroRepository.save(c);
    }

    // Validação do modelo (removida a restrição de tamanho < 5)
    private void validarModelo(String modelo) {
        if (modelo == null || modelo.trim().isEmpty()) {
            log.warn("Validação falhou: modelo está vazio ou nulo.");
            throw new CarroException("O modelo do carro não pode estar vazio.");
        }
    }

    private void validarPreco(double preco) {
        if (preco < 0) {
            log.warn("Validação falhou: preço negativo informado ({})", preco);
            throw new CarroException("O preço do carro não pode ser negativo. Valor fornecido: " + preco);
        }
    }

    private Carro localizarCarroPorModelo(String modelo) {
        validarModelo(modelo);
        return carroRepository.findFirstByModelo(modelo)
                .orElseThrow(() -> {
                    log.warn("Carro modelo '{}' não encontrado.", modelo);
                    return new CarroException("Carro com modelo " + modelo + " não encontrado.");
                });
    }
}
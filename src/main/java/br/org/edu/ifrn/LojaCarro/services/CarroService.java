package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.CarroException;
import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.repository.CarroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CarroService {

    @Autowired
    public CarroRepository carroRepository;

    public Carro save(Carro c) {
        validarModelo(c.getModelo());  // Valida o modelo antes de salvar
        validarPreco(c.getPreco());
        return carroRepository.save(c);
    }

    // Novo método para deletar por ID
    public void deleteById(Long id) {
        if(id <= 0){
            throw new CarroException("O ID do carro não pode ser negativo. ID fornecido: " + id);
        }
        carroRepository.deleteById(id);
    }

    // Novo método para pesquisar por ID
    public Optional<Carro> findById(Long id) {
        if(id <= 0){
            throw new CarroException("O ID do carro não pode ser negativo. ID fornecido: " + id);
        }
        return carroRepository.findById(id);
    }

    // Novo método para listar todos os carros
    public List<Carro> findAll() {
        return carroRepository.findAll();
    }

    public Optional<Carro> findByModelo(String modelo) {
        validarModelo(modelo);
        return carroRepository.findFirstByModelo(modelo);
    }

    public Carro saveFromLegacy(String modelo, double preco) {
        Carro carro = new Carro(modelo, LocalDate.now().getYear(), preco);
        return save(carro);
    }

    public Carro updateByModelo(String modelo, double preco) {
        Carro carro = localizarCarroPorModelo(modelo);
        validarPreco(preco);
        carro.setPreco(preco);
        return carroRepository.save(carro);
    }

    public Carro deleteByModelo(String modelo) {
        Carro carro = localizarCarroPorModelo(modelo);
        carroRepository.delete(carro);
        return carro;
    }

    // Método para atualizar (usa o save existente, mas pode ser renomeado se preferir)
    public Carro update(Carro c) {
        if (c.getId() == null) {
            throw new CarroException("O ID do carro para atualização não pode ser nulo.");
        }
        if (!carroRepository.existsById(c.getId())) {
            throw new CarroException("Carro com ID " + c.getId() + " não encontrado para atualização.");
        }
        validarModelo(c.getModelo());  // Valida o modelo antes de atualizar
        validarPreco(c.getPreco());
        return carroRepository.save(c);  // Retorna o carro salvo para feedback
    }

    // Validação do modelo
    private void validarModelo(String modelo) {
        if (modelo == null || modelo.trim().isEmpty()) {
            throw new CarroException("O modelo do carro não pode estar vazio.");
        }
        if (modelo.length() >= 5) {
            throw new CarroException("O modelo do carro deve ter menos de 5 caracteres. Tamanho atual: " + modelo.length());
        }
    }

    private void validarPreco(double preco) {
        if (preco < 0) {
            throw new CarroException("O preço do carro não pode ser negativo. Valor fornecido: " + preco);
        }
    }

    private Carro localizarCarroPorModelo(String modelo) {
        validarModelo(modelo);
        return carroRepository.findFirstByModelo(modelo)
                .orElseThrow(() -> new CarroException("Carro com modelo " + modelo + " não encontrado."));
    }
}
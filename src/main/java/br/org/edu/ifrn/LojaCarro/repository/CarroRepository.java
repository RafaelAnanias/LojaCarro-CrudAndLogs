package br.org.edu.ifrn.LojaCarro.repository;

import br.org.edu.ifrn.LojaCarro.model.Carro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarroRepository extends JpaRepository<Carro, Long> {

    Optional<Carro> findFirstByModelo(String modelo);
}

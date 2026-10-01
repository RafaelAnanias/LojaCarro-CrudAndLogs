package br.org.edu.ifrn.LojaCarro.model;

import jakarta.persistence.*;

@Entity
public class Carro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    String modelo;
    int ano;
    double preco;

    // No-arg constructor necessário para desserialização (Jackson)
    public Carro() {
    }

    public Carro(String modelo, int ano) {
        this.modelo = modelo;
        this.ano = ano;
        this.preco = 0.0;
    }

    public Carro(String modelo, int ano, double preco) {
        this.modelo = modelo;
        this.ano = ano;
        this.preco = preco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }
}

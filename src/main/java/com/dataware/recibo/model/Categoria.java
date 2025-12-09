package com.dataware.recibo.model;

import java.time.LocalDateTime;

public class Categoria {
    private Integer id;
    private Integer empresaSistemaId;
    private String nome;
    private String descricao;
    private Boolean ativo;
    private LocalDateTime dataCadastro;

    // Construtor padrão
    public Categoria() {
        this.ativo = true;
    }

    // Construtor com parâmetros
    public Categoria(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
        this.ativo = true;
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEmpresaSistemaId() {
        return empresaSistemaId;
    }

    public void setEmpresaSistemaId(Integer empresaSistemaId) {
        this.empresaSistemaId = empresaSistemaId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    @Override
    public String toString() {
        return nome;
    }
}




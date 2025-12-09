package com.dataware.recibo.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Recibo {
    private Integer id;
    private Integer empresaSistemaId;
    private String numeroRecibo;
    private Integer clienteId;
    private Integer categoriaId;
    private BigDecimal valor;
    private String valorExtenso;
    private String referente;
    private LocalDate dataEmissao;
    private String formaPagamento;
    private String observacoes;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;

    // Campos relacionados (não persistidos)
    private Cliente cliente;
    private Categoria categoria;
    private Empresa empresa;

    // Construtor padrão
    public Recibo() {
        this.dataEmissao = LocalDate.now();
        this.valor = BigDecimal.ZERO;
    }

    // Construtor com parâmetros
    public Recibo(Integer id, String numeroRecibo, BigDecimal valor) {
        this.id = id;
        this.numeroRecibo = numeroRecibo;
        this.valor = valor;
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

    public String getNumeroRecibo() {
        return numeroRecibo;
    }

    public void setNumeroRecibo(String numeroRecibo) {
        this.numeroRecibo = numeroRecibo;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public Integer getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Integer categoriaId) {
        this.categoriaId = categoriaId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getValorExtenso() {
        return valorExtenso;
    }

    public void setValorExtenso(String valorExtenso) {
        this.valorExtenso = valorExtenso;
    }

    public String getReferente() {
        return referente;
    }

    public void setReferente(String referente) {
        this.referente = referente;
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDate dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    @Override
    public String toString() {
        return "Recibo #" + numeroRecibo + " - " + (cliente != null ? cliente.getNomeRazaoSocial() : "");
    }
}




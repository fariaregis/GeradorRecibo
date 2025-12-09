package com.dataware.recibo.model;

import java.time.LocalDateTime;

/**
 * Classe que representa um cliente no sistema.
 * Pode ser tanto pessoa física (F) quanto jurídica (J).
 */
public class Cliente {
    private Integer id;
    private Integer empresaSistemaId;
    private String tipoPessoa;  // F = Pessoa Física, J = Pessoa Jurídica
    private String nomeRazaoSocial;
    private String nomeFantasia;
    private String cpfCnpj;
    private String rgInscricaoEstadual;
    private String endereco;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private String telefone;
    private String celular;
    private String email;
    private String observacoes;
    private Boolean ativo;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;

    public Cliente() {
        this.ativo = true;  // Por padrão, cliente é criado como ativo
    }

    /**
     * Cria um novo cliente com ID e nome/razão social.
     * 
     * @param id Identificador único do cliente
     * @param nomeRazaoSocial Nome ou razão social do cliente
     */
    public Cliente(Integer id, String nomeRazaoSocial) {
        this();
        this.id = id;
        this.nomeRazaoSocial = nomeRazaoSocial;
    }

    // ===== Getters e Setters ===== //
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

    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public void setNomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getRgInscricaoEstadual() {
        return rgInscricaoEstadual;
    }

    public void setRgInscricaoEstadual(String rgInscricaoEstadual) {
        this.rgInscricaoEstadual = rgInscricaoEstadual;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
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

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    // Métodos auxiliares
    public String getEnderecoCompleto() {
        StringBuilder sb = new StringBuilder();
        if (endereco != null && !endereco.isEmpty()) {
            sb.append(endereco);
            if (numero != null && !numero.isEmpty()) {
                sb.append(", ").append(numero);
            }
            if (complemento != null && !complemento.isEmpty()) {
                sb.append(" - ").append(complemento);
            }
        }
        return sb.toString();
    }

    public String getTipoPessoaDescricao() {
        return "F".equals(tipoPessoa) ? "Pessoa Física" : "Pessoa Jurídica";
    }

    @Override
    public String toString() {
        return nomeRazaoSocial;
    }
}



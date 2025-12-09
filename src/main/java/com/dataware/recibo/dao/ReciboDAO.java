package com.dataware.recibo.dao;

import com.dataware.recibo.model.Categoria;
import com.dataware.recibo.model.Cliente;
import com.dataware.recibo.model.Empresa;
import com.dataware.recibo.model.Recibo;
import com.dataware.recibo.util.DatabaseConnection;
import com.dataware.recibo.util.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReciboDAO {
    private static final Logger logger = LoggerFactory.getLogger(ReciboDAO.class);

    /**
     * Lista todos os recibos da empresa logada
     */
    public List<Recibo> listar() throws SQLException {
        Integer empresaId = SessionManager.getInstance().getEmpresaSistemaId();
        if (empresaId == null) {
            throw new IllegalStateException("Nenhuma empresa logada");
        }

        List<Recibo> recibos = new ArrayList<>();
        String sql = "SELECT r.*, " +
                     "c.nome_razao_social as cliente_nome, c.cpf_cnpj as cliente_cpf_cnpj, " +
                     "cat.nome as categoria_nome " +
                     "FROM tb_recibos r " +
                     "LEFT JOIN tb_clientes c ON r.cliente_id = c.id " +
                     "LEFT JOIN tb_categorias cat ON r.categoria_id = cat.id " +
                     "WHERE r.empresa_sistema_id = ? " +
                     "ORDER BY r.data_emissao DESC, r.id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empresaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    recibos.add(mapResultSetToRecibo(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar recibos", e);
            throw e;
        }

        return recibos;
    }

    /**
     * Busca recibo por ID com dados completos
     */
    public Recibo buscarPorIdCompleto(Integer id) throws SQLException {
        Integer empresaId = SessionManager.getInstance().getEmpresaSistemaId();
        String sql = "SELECT r.*, " +
                     "e.tipo_pessoa as empresa_tipo_pessoa, e.nome_razao_social as empresa_nome, " +
                     "e.nome_fantasia as empresa_fantasia, e.cpf_cnpj as empresa_cpf_cnpj, " +
                     "e.rg_inscricao_estadual as empresa_ie, e.endereco_completo as empresa_endereco, " +
                     "e.numero as empresa_numero, e.bairro as empresa_bairro, e.cidade as empresa_cidade, " +
                     "e.estado as empresa_estado, e.cep as empresa_cep, e.telefone as empresa_telefone, " +
                     "e.celular as empresa_celular, e.email as empresa_email, " +
                     "c.tipo_pessoa as cliente_tipo_pessoa, c.nome_razao_social as cliente_nome, " +
                     "c.cpf_cnpj as cliente_cpf_cnpj, c.rg_inscricao_estadual as cliente_rg_ie, " +
                     "c.endereco_completo as cliente_endereco, c.numero as cliente_numero, " +
                     "c.bairro as cliente_bairro, c.cidade as cliente_cidade, c.estado as cliente_estado, " +
                     "c.cep as cliente_cep, c.telefone as cliente_telefone, c.celular as cliente_celular, " +
                     "c.email as cliente_email, " +
                     "cat.nome as categoria_nome, cat.descricao as categoria_descricao " +
                     "FROM tb_recibos r " +
                     "LEFT JOIN tb_empresas e ON r.empresa_sistema_id = e.id " +
                     "LEFT JOIN tb_clientes c ON r.cliente_id = c.id " +
                     "LEFT JOIN tb_categorias cat ON r.categoria_id = cat.id " +
                     "WHERE r.id = ? AND r.empresa_sistema_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setInt(2, empresaId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReciboCompleto(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar recibo por ID: " + id, e);
            throw e;
        }

        return null;
    }

    /**
     * Busca recibos por número
     */
    public List<Recibo> buscarPorNumero(String numero) throws SQLException {
        Integer empresaId = SessionManager.getInstance().getEmpresaSistemaId();
        List<Recibo> recibos = new ArrayList<>();
        String sql = "SELECT r.*, " +
                     "c.nome_razao_social as cliente_nome, c.cpf_cnpj as cliente_cpf_cnpj, " +
                     "cat.nome as categoria_nome " +
                     "FROM tb_recibos r " +
                     "LEFT JOIN tb_clientes c ON r.cliente_id = c.id " +
                     "LEFT JOIN tb_categorias cat ON r.categoria_id = cat.id " +
                     "WHERE r.empresa_sistema_id = ? AND r.numero_recibo LIKE ? " +
                     "ORDER BY r.data_emissao DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empresaId);
            stmt.setString(2, "%" + numero + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    recibos.add(mapResultSetToRecibo(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar recibos por número", e);
            throw e;
        }

        return recibos;
    }

    /**
     * Gera próximo número de recibo
     */
    public String gerarProximoNumero() throws SQLException {
        Integer empresaId = SessionManager.getInstance().getEmpresaSistemaId();
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTR(numero_recibo, 1, INSTR(numero_recibo || '-', '-') - 1) AS INTEGER)), 0) + 1 AS proximo " +
                     "FROM tb_recibos WHERE empresa_sistema_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empresaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int proximo = rs.getInt("proximo");
                    return String.format("%03d-%d", proximo, java.time.Year.now().getValue());
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao gerar próximo número de recibo", e);
            throw e;
        }

        return "001-" + java.time.Year.now().getValue();
    }

    /**
     * Insere novo recibo
     */
    public Integer inserir(Recibo recibo) throws SQLException {
        Integer empresaId = SessionManager.getInstance().getEmpresaSistemaId();
        if (empresaId == null) {
            throw new IllegalStateException("Nenhuma empresa logada");
        }

        String sql = "INSERT INTO tb_recibos (empresa_sistema_id, numero_recibo, cliente_id, categoria_id, " +
                     "valor, valor_extenso, referente, data_emissao, forma_pagamento, observacoes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, empresaId);
            stmt.setString(2, recibo.getNumeroRecibo());
            stmt.setInt(3, recibo.getClienteId());
            if (recibo.getCategoriaId() != null) {
                stmt.setInt(4, recibo.getCategoriaId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setBigDecimal(5, recibo.getValor());
            stmt.setString(6, recibo.getValorExtenso());
            stmt.setString(7, recibo.getReferente());
            stmt.setString(8, recibo.getDataEmissao().toString());
            stmt.setString(9, recibo.getFormaPagamento());
            stmt.setString(10, recibo.getObservacoes());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    Integer id = rs.getInt(1);
                    logger.info("Recibo inserido com ID: " + id);
                    return id;
                }
            }
            
            throw new SQLException("Falha ao obter ID do recibo inserido");

        } catch (SQLException e) {
            logger.error("Erro ao inserir recibo", e);
            throw e;
        }
    }

    /**
     * Atualiza recibo
     */
    public void atualizar(Recibo recibo) throws SQLException {
        Integer empresaId = SessionManager.getInstance().getEmpresaSistemaId();
        if (empresaId == null) {
            throw new IllegalStateException("Nenhuma empresa logada");
        }

        String sql = "UPDATE tb_recibos SET numero_recibo = ?, cliente_id = ?, categoria_id = ?, " +
                     "valor = ?, valor_extenso = ?, referente = ?, data_emissao = ?, " +
                     "forma_pagamento = ?, observacoes = ? WHERE id = ? AND empresa_sistema_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, recibo.getNumeroRecibo());
            stmt.setInt(2, recibo.getClienteId());
            if (recibo.getCategoriaId() != null) {
                stmt.setInt(3, recibo.getCategoriaId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setBigDecimal(4, recibo.getValor());
            stmt.setString(5, recibo.getValorExtenso());
            stmt.setString(6, recibo.getReferente());
            stmt.setString(7, recibo.getDataEmissao().toString());
            stmt.setString(8, recibo.getFormaPagamento());
            stmt.setString(9, recibo.getObservacoes());
            stmt.setInt(10, recibo.getId());
            stmt.setInt(11, empresaId);

            stmt.executeUpdate();
            logger.info("Recibo atualizado: " + recibo.getId());

        } catch (SQLException e) {
            logger.error("Erro ao atualizar recibo", e);
            throw e;
        }
    }

    /**
     * Deleta recibo
     */
    public void deletar(Integer id) throws SQLException {
        Integer empresaId = SessionManager.getInstance().getEmpresaSistemaId();
        if (empresaId == null) {
            throw new IllegalStateException("Nenhuma empresa logada");
        }

        String sql = "DELETE FROM tb_recibos WHERE id = ? AND empresa_sistema_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setInt(2, empresaId);
            stmt.executeUpdate();
            logger.info("Recibo deletado: " + id);

        } catch (SQLException e) {
            logger.error("Erro ao deletar recibo", e);
            throw e;
        }
    }

    /**
     * Mapeia ResultSet para objeto Recibo (listagem simples)
     */
    private Recibo mapResultSetToRecibo(ResultSet rs) throws SQLException {
        Recibo recibo = new Recibo();
        recibo.setId(rs.getInt("id"));
        recibo.setEmpresaSistemaId(rs.getInt("empresa_sistema_id"));
        recibo.setNumeroRecibo(rs.getString("numero_recibo"));
        recibo.setClienteId(rs.getInt("cliente_id"));
        recibo.setCategoriaId(rs.getInt("categoria_id"));
        recibo.setValor(rs.getBigDecimal("valor"));
        recibo.setValorExtenso(rs.getString("valor_extenso"));
        recibo.setReferente(rs.getString("referente"));
        
        String dataEmissao = rs.getString("data_emissao");
        if (dataEmissao != null && !dataEmissao.isEmpty()) {
            try {
                recibo.setDataEmissao(java.time.LocalDate.parse(dataEmissao));
            } catch (Exception e) {
                // Ignora erro de parsing
            }
        }
        
        recibo.setFormaPagamento(rs.getString("forma_pagamento"));
        recibo.setObservacoes(rs.getString("observacoes"));
        
        // Dados relacionados
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("cliente_id"));
        cliente.setNomeRazaoSocial(rs.getString("cliente_nome"));
        cliente.setCpfCnpj(rs.getString("cliente_cpf_cnpj"));
        recibo.setCliente(cliente);
        
        Categoria categoria = new Categoria();
        categoria.setId(rs.getInt("categoria_id"));
        categoria.setNome(rs.getString("categoria_nome"));
        recibo.setCategoria(categoria);
        
        String dataCadastro = rs.getString("data_cadastro");
        if (dataCadastro != null && !dataCadastro.isEmpty()) {
            try {
                recibo.setDataCadastro(java.time.LocalDateTime.parse(dataCadastro.replace(" ", "T")));
            } catch (Exception e) {
                // Ignora erro de parsing
            }
        }
        
        return recibo;
    }

    /**
     * Mapeia ResultSet para objeto Recibo completo (para impressão)
     */
    private Recibo mapResultSetToReciboCompleto(ResultSet rs) throws SQLException {
        Recibo recibo = new Recibo();
        recibo.setId(rs.getInt("id"));
        recibo.setEmpresaSistemaId(rs.getInt("empresa_sistema_id"));
        recibo.setNumeroRecibo(rs.getString("numero_recibo"));
        recibo.setValor(rs.getBigDecimal("valor"));
        recibo.setValorExtenso(rs.getString("valor_extenso"));
        recibo.setReferente(rs.getString("referente"));
        
        String dataEmissao = rs.getString("data_emissao");
        if (dataEmissao != null && !dataEmissao.isEmpty()) {
            try {
                recibo.setDataEmissao(java.time.LocalDate.parse(dataEmissao));
            } catch (Exception e) {
                // Ignora erro de parsing
            }
        }
        
        recibo.setFormaPagamento(rs.getString("forma_pagamento"));
        recibo.setObservacoes(rs.getString("observacoes"));
        
        // Empresa
        Empresa empresa = new Empresa();
        empresa.setId(rs.getInt("empresa_sistema_id"));
        empresa.setTipoPessoa(rs.getString("empresa_tipo_pessoa"));
        empresa.setNomeRazaoSocial(rs.getString("empresa_nome"));
        empresa.setNomeFantasia(rs.getString("empresa_fantasia"));
        empresa.setCpfCnpj(rs.getString("empresa_cpf_cnpj"));
        empresa.setRgInscricaoEstadual(rs.getString("empresa_ie"));
        empresa.setEndereco(rs.getString("empresa_endereco"));
        empresa.setNumero(rs.getString("empresa_numero"));
        empresa.setBairro(rs.getString("empresa_bairro"));
        empresa.setCidade(rs.getString("empresa_cidade"));
        empresa.setEstado(rs.getString("empresa_estado"));
        empresa.setCep(rs.getString("empresa_cep"));
        empresa.setTelefone(rs.getString("empresa_telefone"));
        empresa.setCelular(rs.getString("empresa_celular"));
        empresa.setEmail(rs.getString("empresa_email"));
        recibo.setEmpresa(empresa);
        
        // Cliente
        Cliente cliente = new Cliente();
        cliente.setTipoPessoa(rs.getString("cliente_tipo_pessoa"));
        cliente.setNomeRazaoSocial(rs.getString("cliente_nome"));
        cliente.setCpfCnpj(rs.getString("cliente_cpf_cnpj"));
        cliente.setRgInscricaoEstadual(rs.getString("cliente_rg_ie"));
        cliente.setEndereco(rs.getString("cliente_endereco"));
        cliente.setNumero(rs.getString("cliente_numero"));
        cliente.setBairro(rs.getString("cliente_bairro"));
        cliente.setCidade(rs.getString("cliente_cidade"));
        cliente.setEstado(rs.getString("cliente_estado"));
        cliente.setCep(rs.getString("cliente_cep"));
        cliente.setTelefone(rs.getString("cliente_telefone"));
        cliente.setCelular(rs.getString("cliente_celular"));
        cliente.setEmail(rs.getString("cliente_email"));
        recibo.setCliente(cliente);
        
        // Categoria
        Categoria categoria = new Categoria();
        categoria.setNome(rs.getString("categoria_nome"));
        categoria.setDescricao(rs.getString("categoria_descricao"));
        recibo.setCategoria(categoria);
        
        return recibo;
    }
}

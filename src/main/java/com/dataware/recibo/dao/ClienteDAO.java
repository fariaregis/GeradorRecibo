package com.dataware.recibo.dao;

import com.dataware.recibo.model.Cliente;
import com.dataware.recibo.util.DatabaseConnection;
import com.dataware.recibo.util.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    private static final Logger log = LoggerFactory.getLogger(ClienteDAO.class);

    // Lista clientes ativos da empresa atual
    public List<Cliente> listar() throws SQLException {
        Integer empresaId = SessionManager.getInstance().getEmpresaSistemaId();
        if (empresaId == null) {
            log.warn("Tentativa de listar clientes sem empresa definida");
            throw new IllegalStateException("Empresa não definida");
        }

        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM tb_clientes WHERE empresa_sistema_id = ? AND ativo = 1 ORDER BY nome_razao_social";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empresaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapResultSetToCliente(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Erro ao listar clientes", e);
            throw e;
        }

        return clientes;
    }

    /**
     * Busca cliente por ID
     */
    public Cliente buscarPorId(Integer id) throws SQLException {
        Integer empresaId = SessionManager.getInstance().getEmpresaSistemaId();
        String sql = "SELECT * FROM tb_clientes WHERE id = ? AND empresa_sistema_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setInt(2, empresaId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCliente(rs);
                }
            }
        } catch (SQLException e) {
            log.error("Erro ao buscar cliente por ID: " + id, e);
            throw e;
        }

        return null;
    }

    /**
     * Busca clientes por nome
     */
    public List<Cliente> buscarPorNome(String nome) throws SQLException {
        Integer empresaId = SessionManager.getInstance().getEmpresaSistemaId();
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM tb_clientes WHERE empresa_sistema_id = ? AND nome_razao_social LIKE ? AND ativo = 1 ORDER BY nome_razao_social";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empresaId);
            stmt.setString(2, "%" + nome + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapResultSetToCliente(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Erro ao buscar clientes por nome", e);
            throw e;
        }

        return clientes;
    }

    /**
     * Insere novo cliente
     */
    public Integer inserir(Cliente cliente) throws SQLException {
        Integer empresaId = SessionManager.getInstance().getEmpresaSistemaId();
        if (empresaId == null) {
            throw new IllegalStateException("Nenhuma empresa logada");
        }

        String sql = "INSERT INTO tb_clientes (empresa_sistema_id, tipo_pessoa, nome_razao_social, nome_fantasia, " +
                     "cpf_cnpj, rg_inscricao_estadual, endereco_completo, numero, complemento, bairro, cidade, " +
                     "estado, cep, telefone, celular, email, observacoes, ativo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, empresaId);
            stmt.setString(2, cliente.getTipoPessoa());
            stmt.setString(3, cliente.getNomeRazaoSocial());
            stmt.setString(4, cliente.getNomeFantasia());
            stmt.setString(5, cliente.getCpfCnpj());
            stmt.setString(6, cliente.getRgInscricaoEstadual());
            stmt.setString(7, cliente.getEndereco());
            stmt.setString(8, cliente.getNumero());
            stmt.setString(9, cliente.getComplemento());
            stmt.setString(10, cliente.getBairro());
            stmt.setString(11, cliente.getCidade());
            stmt.setString(12, cliente.getEstado());
            stmt.setString(13, cliente.getCep());
            stmt.setString(14, cliente.getTelefone());
            stmt.setString(15, cliente.getCelular());
            stmt.setString(16, cliente.getEmail());
            stmt.setString(17, cliente.getObservacoes());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    Integer id = rs.getInt(1);
                    log.info("Cliente inserido com ID: " + id);
                    return id;
                }
            }
            
            throw new SQLException("Falha ao obter ID do cliente inserido");

        } catch (SQLException e) {
            log.error("Erro ao inserir cliente", e);
            throw e;
        }
    }

    /**
     * Atualiza cliente
     */
    public void atualizar(Cliente cliente) throws SQLException {
        Integer empresaId = SessionManager.getInstance().getEmpresaSistemaId();
        if (empresaId == null) {
            throw new IllegalStateException("Nenhuma empresa logada");
        }

        String sql = "UPDATE tb_clientes SET tipo_pessoa = ?, nome_razao_social = ?, nome_fantasia = ?, " +
                     "cpf_cnpj = ?, rg_inscricao_estadual = ?, endereco_completo = ?, numero = ?, " +
                     "complemento = ?, bairro = ?, cidade = ?, estado = ?, cep = ?, telefone = ?, " +
                     "celular = ?, email = ?, observacoes = ?, ativo = ? " +
                     "WHERE id = ? AND empresa_sistema_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getTipoPessoa());
            stmt.setString(2, cliente.getNomeRazaoSocial());
            stmt.setString(3, cliente.getNomeFantasia());
            stmt.setString(4, cliente.getCpfCnpj());
            stmt.setString(5, cliente.getRgInscricaoEstadual());
            stmt.setString(6, cliente.getEndereco());
            stmt.setString(7, cliente.getNumero());
            stmt.setString(8, cliente.getComplemento());
            stmt.setString(9, cliente.getBairro());
            stmt.setString(10, cliente.getCidade());
            stmt.setString(11, cliente.getEstado());
            stmt.setString(12, cliente.getCep());
            stmt.setString(13, cliente.getTelefone());
            stmt.setString(14, cliente.getCelular());
            stmt.setString(15, cliente.getEmail());
            stmt.setString(16, cliente.getObservacoes());
            stmt.setInt(17, cliente.getAtivo() ? 1 : 0);
            stmt.setInt(18, cliente.getId());
            stmt.setInt(19, empresaId);

            stmt.executeUpdate();
            log.info("Cliente atualizado: " + cliente.getId());

        } catch (SQLException e) {
            log.error("Erro ao atualizar cliente", e);
            throw e;
        }
    }

    /**
     * Deleta cliente (lógico - apenas desativa)
     */
    public void deletar(Integer id) throws SQLException {
        Integer empresaId = SessionManager.getInstance().getEmpresaSistemaId();
        if (empresaId == null) {
            throw new IllegalStateException("Nenhuma empresa logada");
        }

        String sql = "UPDATE tb_clientes SET ativo = 0 WHERE id = ? AND empresa_sistema_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setInt(2, empresaId);
            stmt.executeUpdate();
            log.info("Cliente removido: " + id);

        } catch (SQLException e) {
            log.error("Erro ao remover cliente", e);
            throw e;
        }
    }

    /**
     * Mapeia ResultSet para objeto Cliente
     */
    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("id"));
        cliente.setEmpresaSistemaId(rs.getInt("empresa_sistema_id"));
        cliente.setTipoPessoa(rs.getString("tipo_pessoa"));
        cliente.setNomeRazaoSocial(rs.getString("nome_razao_social"));
        cliente.setNomeFantasia(rs.getString("nome_fantasia"));
        cliente.setCpfCnpj(rs.getString("cpf_cnpj"));
        cliente.setRgInscricaoEstadual(rs.getString("rg_inscricao_estadual"));
        cliente.setEndereco(rs.getString("endereco_completo"));
        cliente.setNumero(rs.getString("numero"));
        cliente.setComplemento(rs.getString("complemento"));
        cliente.setBairro(rs.getString("bairro"));
        cliente.setCidade(rs.getString("cidade"));
        cliente.setEstado(rs.getString("estado"));
        cliente.setCep(rs.getString("cep"));
        cliente.setTelefone(rs.getString("telefone"));
        cliente.setCelular(rs.getString("celular"));
        cliente.setEmail(rs.getString("email"));
        cliente.setObservacoes(rs.getString("observacoes"));
        cliente.setAtivo(rs.getInt("ativo") == 1);
        
        String dataCadastro = rs.getString("data_cadastro");
        if (dataCadastro != null && !dataCadastro.isEmpty()) {
            try {
                cliente.setDataCadastro(java.time.LocalDateTime.parse(dataCadastro.replace(" ", "T")));
            } catch (Exception e) {
                // Ignora erro de parsing
            }
        }
        
        return cliente;
    }
}

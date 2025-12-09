package com.dataware.recibo.dao;

import com.dataware.recibo.model.Empresa;
import com.dataware.recibo.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpresaDAO {
    private static final Logger logger = LoggerFactory.getLogger(EmpresaDAO.class);

    /**
     * Lista todas as empresas ativas
     */
    public List<Empresa> listar() throws SQLException {
        List<Empresa> empresas = new ArrayList<>();
        String sql = "SELECT * FROM tb_empresas WHERE ativo = 1 ORDER BY nome_razao_social";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                empresas.add(mapResultSetToEmpresa(rs));
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar empresas", e);
            throw e;
        }

        return empresas;
    }

    /**
     * Busca empresa por ID
     */
    public Empresa buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT * FROM tb_empresas WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmpresa(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar empresa por ID: " + id, e);
            throw e;
        }

        return null;
    }

    /**
     * Busca empresa por CPF/CNPJ
     */
    public Empresa buscarPorCpfCnpj(String cpfCnpj) throws SQLException {
        String sql = "SELECT * FROM tb_empresas WHERE cpf_cnpj = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpfCnpj);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmpresa(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar empresa por CPF/CNPJ", e);
            throw e;
        }

        return null;
    }

    /**
     * Insere nova empresa
     */
    public Integer inserir(Empresa empresa) throws SQLException {
        String sql = "INSERT INTO tb_empresas (tipo_pessoa, nome_razao_social, nome_fantasia, cpf_cnpj, " +
                     "rg_inscricao_estadual, endereco_completo, numero, complemento, bairro, cidade, estado, " +
                     "cep, telefone, celular, email, site, ativo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, empresa.getTipoPessoa());
            stmt.setString(2, empresa.getNomeRazaoSocial());
            stmt.setString(3, empresa.getNomeFantasia());
            stmt.setString(4, empresa.getCpfCnpj());
            stmt.setString(5, empresa.getRgInscricaoEstadual());
            stmt.setString(6, empresa.getEndereco());
            stmt.setString(7, empresa.getNumero());
            stmt.setString(8, empresa.getComplemento());
            stmt.setString(9, empresa.getBairro());
            stmt.setString(10, empresa.getCidade());
            stmt.setString(11, empresa.getEstado());
            stmt.setString(12, empresa.getCep());
            stmt.setString(13, empresa.getTelefone());
            stmt.setString(14, empresa.getCelular());
            stmt.setString(15, empresa.getEmail());
            stmt.setString(16, empresa.getSite());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    Integer id = rs.getInt(1);
                    logger.info("Empresa inserida com ID: " + id);
                    return id;
                }
            }
            
            throw new SQLException("Falha ao obter ID da empresa inserida");

        } catch (SQLException e) {
            logger.error("Erro ao inserir empresa", e);
            throw e;
        }
    }

    /**
     * Atualiza empresa
     */
    public void atualizar(Empresa empresa) throws SQLException {
        String sql = "UPDATE tb_empresas SET tipo_pessoa = ?, nome_razao_social = ?, nome_fantasia = ?, " +
                     "cpf_cnpj = ?, rg_inscricao_estadual = ?, endereco_completo = ?, numero = ?, " +
                     "complemento = ?, bairro = ?, cidade = ?, estado = ?, cep = ?, telefone = ?, " +
                     "celular = ?, email = ?, site = ?, ativo = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empresa.getTipoPessoa());
            stmt.setString(2, empresa.getNomeRazaoSocial());
            stmt.setString(3, empresa.getNomeFantasia());
            stmt.setString(4, empresa.getCpfCnpj());
            stmt.setString(5, empresa.getRgInscricaoEstadual());
            stmt.setString(6, empresa.getEndereco());
            stmt.setString(7, empresa.getNumero());
            stmt.setString(8, empresa.getComplemento());
            stmt.setString(9, empresa.getBairro());
            stmt.setString(10, empresa.getCidade());
            stmt.setString(11, empresa.getEstado());
            stmt.setString(12, empresa.getCep());
            stmt.setString(13, empresa.getTelefone());
            stmt.setString(14, empresa.getCelular());
            stmt.setString(15, empresa.getEmail());
            stmt.setString(16, empresa.getSite());
            stmt.setInt(17, empresa.getAtivo() ? 1 : 0);
            stmt.setInt(18, empresa.getId());

            stmt.executeUpdate();
            logger.info("Empresa atualizada: " + empresa.getId());

        } catch (SQLException e) {
            logger.error("Erro ao atualizar empresa", e);
            throw e;
        }
    }

    /**
     * Deleta empresa (lógico - apenas desativa)
     */
    public void deletar(Integer id) throws SQLException {
        String sql = "UPDATE tb_empresas SET ativo = 0 WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            logger.info("Empresa deletada (lógico): " + id);

        } catch (SQLException e) {
            logger.error("Erro ao deletar empresa", e);
            throw e;
        }
    }

    /**
     * Mapeia ResultSet para objeto Empresa
     */
    private Empresa mapResultSetToEmpresa(ResultSet rs) throws SQLException {
        Empresa empresa = new Empresa();
        empresa.setId(rs.getInt("id"));
        empresa.setTipoPessoa(rs.getString("tipo_pessoa"));
        empresa.setNomeRazaoSocial(rs.getString("nome_razao_social"));
        empresa.setNomeFantasia(rs.getString("nome_fantasia"));
        empresa.setCpfCnpj(rs.getString("cpf_cnpj"));
        empresa.setRgInscricaoEstadual(rs.getString("rg_inscricao_estadual"));
        empresa.setEndereco(rs.getString("endereco_completo"));
        empresa.setNumero(rs.getString("numero"));
        empresa.setComplemento(rs.getString("complemento"));
        empresa.setBairro(rs.getString("bairro"));
        empresa.setCidade(rs.getString("cidade"));
        empresa.setEstado(rs.getString("estado"));
        empresa.setCep(rs.getString("cep"));
        empresa.setTelefone(rs.getString("telefone"));
        empresa.setCelular(rs.getString("celular"));
        empresa.setEmail(rs.getString("email"));
        empresa.setSite(rs.getString("site"));
        empresa.setAtivo(rs.getInt("ativo") == 1);
        
        String dataCadastro = rs.getString("data_cadastro");
        if (dataCadastro != null && !dataCadastro.isEmpty()) {
            try {
                empresa.setDataCadastro(java.time.LocalDateTime.parse(dataCadastro.replace(" ", "T")));
            } catch (Exception e) {
                // Ignora erro de parsing de data
            }
        }
        
        return empresa;
    }
}



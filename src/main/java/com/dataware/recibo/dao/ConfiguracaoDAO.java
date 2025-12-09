package com.dataware.recibo.dao;

import com.dataware.recibo.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfiguracaoDAO {
    private static final Logger logger = LoggerFactory.getLogger(ConfiguracaoDAO.class);

    /**
     * Busca uma configuração por chave
     */
    public String buscarPorChave(String chave) throws SQLException {
        String sql = "SELECT valor FROM tb_configuracoes WHERE chave = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, chave);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("valor");
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar configuração: " + chave, e);
            throw e;
        }

        return null;
    }

    /**
     * Salva ou atualiza uma configuração
     */
    public void salvar(String chave, String valor) throws SQLException {
        String sql = "INSERT OR REPLACE INTO tb_configuracoes (chave, valor) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, chave);
            stmt.setString(2, valor);

            stmt.executeUpdate();
            logger.info("Configuração salva: " + chave);

        } catch (SQLException e) {
            logger.error("Erro ao salvar configuração: " + chave, e);
            throw e;
        }
    }

    /**
     * Busca o ID da empresa padrão
     */
    public Integer buscarEmpresaPadrao() throws SQLException {
        String valor = buscarPorChave("empresa_padrao_id");
        if (valor != null && !valor.isEmpty()) {
            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException e) {
                logger.error("Erro ao converter empresa_padrao_id", e);
            }
        }
        return null;
    }

    /**
     * Define a empresa padrão
     */
    public void salvarEmpresaPadrao(Integer empresaId) throws SQLException {
        salvar("empresa_padrao_id", empresaId.toString());
    }
}


package com.dataware.recibo.dao;

import com.dataware.recibo.model.Categoria;
import com.dataware.recibo.util.DatabaseConnection;
import com.dataware.recibo.util.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {
    private static final Logger logger = LoggerFactory.getLogger(CategoriaDAO.class);

    /**
     * Lista todas as categorias ativas da empresa logada
     */
    public List<Categoria> listar() throws SQLException {
        Integer empresaId = SessionManager.getInstance().getEmpresaSistemaId();
        if (empresaId == null) {
            throw new IllegalStateException("Nenhuma empresa logada");
        }

        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM tb_categorias WHERE empresa_sistema_id = ? AND ativo = 1 ORDER BY nome";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empresaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categorias.add(mapResultSetToCategoria(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar categorias", e);
            throw e;
        }

        return categorias;
    }

    /**
     * Busca categoria por ID
     */
    public Categoria buscarPorId(Integer id) throws SQLException {
        Integer empresaId = SessionManager.getInstance().getEmpresaSistemaId();
        String sql = "SELECT * FROM tb_categorias WHERE id = ? AND empresa_sistema_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setInt(2, empresaId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategoria(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar categoria por ID: " + id, e);
            throw e;
        }

        return null;
    }

    /**
     * Insere nova categoria
     */
    public Integer inserir(Categoria categoria) throws SQLException {
        Integer empresaId = SessionManager.getInstance().getEmpresaSistemaId();
        if (empresaId == null) {
            throw new IllegalStateException("Nenhuma empresa logada");
        }

        String sql = "INSERT INTO tb_categorias (empresa_sistema_id, nome, descricao, ativo) VALUES (?, ?, ?, 1)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, empresaId);
            stmt.setString(2, categoria.getNome());
            stmt.setString(3, categoria.getDescricao());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    Integer id = rs.getInt(1);
                    logger.info("Categoria inserida com ID: " + id);
                    return id;
                }
            }
            
            throw new SQLException("Falha ao obter ID da categoria inserida");

        } catch (SQLException e) {
            logger.error("Erro ao inserir categoria", e);
            throw e;
        }
    }

    /**
     * Atualiza categoria
     */
    public void atualizar(Categoria categoria) throws SQLException {
        Integer empresaId = SessionManager.getInstance().getEmpresaSistemaId();
        if (empresaId == null) {
            throw new IllegalStateException("Nenhuma empresa logada");
        }

        String sql = "UPDATE tb_categorias SET nome = ?, descricao = ?, ativo = ? WHERE id = ? AND empresa_sistema_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getDescricao());
            stmt.setInt(3, categoria.getAtivo() ? 1 : 0);
            stmt.setInt(4, categoria.getId());
            stmt.setInt(5, empresaId);

            stmt.executeUpdate();
            logger.info("Categoria atualizada: " + categoria.getId());

        } catch (SQLException e) {
            logger.error("Erro ao atualizar categoria", e);
            throw e;
        }
    }

    /**
     * Deleta categoria (lógico - apenas desativa)
     */
    public void deletar(Integer id) throws SQLException {
        Integer empresaId = SessionManager.getInstance().getEmpresaSistemaId();
        if (empresaId == null) {
            throw new IllegalStateException("Nenhuma empresa logada");
        }

        String sql = "UPDATE tb_categorias SET ativo = 0 WHERE id = ? AND empresa_sistema_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setInt(2, empresaId);
            stmt.executeUpdate();
            logger.info("Categoria deletada (lógico): " + id);

        } catch (SQLException e) {
            logger.error("Erro ao deletar categoria", e);
            throw e;
        }
    }

    /**
     * Mapeia ResultSet para objeto Categoria
     */
    private Categoria mapResultSetToCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setId(rs.getInt("id"));
        categoria.setEmpresaSistemaId(rs.getInt("empresa_sistema_id"));
        categoria.setNome(rs.getString("nome"));
        categoria.setDescricao(rs.getString("descricao"));
        categoria.setAtivo(rs.getInt("ativo") == 1);
        
        return categoria;
    }
}

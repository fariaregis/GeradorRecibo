package com.dataware.recibo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    
    private static final String DB_FILE = "recibo.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE;
    
    private static Connection connection = null;

    private DatabaseConnection() {
        // Construtor privado para impedir instanciação
    }

    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                boolean isNewDatabase = !new File(DB_FILE).exists();
                
                connection = DriverManager.getConnection(URL);
                connection.setAutoCommit(true);
                
                // Habilitar foreign keys no SQLite
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON;");
                }
                
                logger.info("Conexão com banco de dados SQLite estabelecida");
                
                // Se é banco novo, criar estrutura
                if (isNewDatabase) {
                    logger.info("Banco de dados novo detectado, criando estrutura...");
                    criarEstruturaBanco();
                }
            }
            return connection;
        } catch (SQLException e) {
            logger.error("Erro ao conectar ao banco de dados SQLite", e);
            throw e;
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Conexão com banco de dados fechada");
            }
        } catch (SQLException e) {
            logger.error("Erro ao fechar conexão", e);
        }
    }

    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            logger.error("Falha no teste de conexão", e);
            return false;
        }
    }
    
    private static void criarEstruturaBanco() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Criar tabela de empresas
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS tb_empresas (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    tipo_pessoa TEXT CHECK(tipo_pessoa IN ('F', 'J'))," +
                "    nome_razao_social TEXT NOT NULL," +
                "    nome_fantasia TEXT," +
                "    cpf_cnpj TEXT NOT NULL UNIQUE," +
                "    rg_inscricao_estadual TEXT," +
                "    endereco_completo TEXT," +
                "    numero TEXT," +
                "    complemento TEXT," +
                "    bairro TEXT," +
                "    cidade TEXT," +
                "    estado TEXT," +
                "    cep TEXT," +
                "    telefone TEXT," +
                "    celular TEXT," +
                "    email TEXT," +
                "    site TEXT," +
                "    ativo INTEGER DEFAULT 1," +
                "    data_cadastro TEXT DEFAULT (datetime('now', 'localtime'))" +
                ")"
            );
            
            // Criar tabela de categorias
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS tb_categorias (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    nome TEXT NOT NULL," +
                "    descricao TEXT," +
                "    ativo INTEGER DEFAULT 1," +
                "    empresa_sistema_id INTEGER NOT NULL," +
                "    FOREIGN KEY (empresa_sistema_id) REFERENCES tb_empresas(id)" +
                ")"
            );
            
            // Criar tabela de clientes
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS tb_clientes (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    tipo_pessoa TEXT CHECK(tipo_pessoa IN ('F', 'J'))," +
                "    nome_razao_social TEXT NOT NULL," +
                "    nome_fantasia TEXT," +
                "    cpf_cnpj TEXT," +
                "    rg_inscricao_estadual TEXT," +
                "    endereco_completo TEXT," +
                "    numero TEXT," +
                "    complemento TEXT," +
                "    bairro TEXT," +
                "    cidade TEXT," +
                "    estado TEXT," +
                "    cep TEXT," +
                "    telefone TEXT," +
                "    celular TEXT," +
                "    email TEXT," +
                "    observacoes TEXT," +
                "    ativo INTEGER DEFAULT 1," +
                "    data_cadastro TEXT DEFAULT (datetime('now', 'localtime'))," +
                "    empresa_sistema_id INTEGER NOT NULL," +
                "    FOREIGN KEY (empresa_sistema_id) REFERENCES tb_empresas(id)" +
                ")"
            );
            
            // Criar tabela de recibos
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS tb_recibos (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    numero_recibo TEXT NOT NULL," +
                "    cliente_id INTEGER NOT NULL," +
                "    categoria_id INTEGER," +
                "    valor REAL NOT NULL," +
                "    valor_extenso TEXT NOT NULL," +
                "    referente TEXT NOT NULL," +
                "    forma_pagamento TEXT," +
                "    observacoes TEXT," +
                "    data_emissao TEXT NOT NULL," +
                "    data_cadastro TEXT DEFAULT (datetime('now', 'localtime'))," +
                "    empresa_sistema_id INTEGER NOT NULL," +
                "    FOREIGN KEY (cliente_id) REFERENCES tb_clientes(id)," +
                "    FOREIGN KEY (categoria_id) REFERENCES tb_categorias(id)," +
                "    FOREIGN KEY (empresa_sistema_id) REFERENCES tb_empresas(id)" +
                ")"
            );
            
            // Criar tabela de configuração
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS tb_configuracoes (" +
                "    chave TEXT PRIMARY KEY," +
                "    valor TEXT" +
                ")"
            );
            
            logger.info("Estrutura do banco de dados criada com sucesso!");
            
            // Inserir dados de exemplo
            inserirDadosExemplo(stmt);
        }
    }
    
    private static void inserirDadosExemplo(Statement stmt) throws SQLException {
        logger.info("Inserindo dados de exemplo...");
        
        // 1. Inserir empresas de exemplo
        stmt.execute(
            "INSERT INTO tb_empresas (tipo_pessoa, nome_razao_social, cpf_cnpj, endereco_completo, " +
            "numero, bairro, cidade, estado, cep, celular, email, site) VALUES " +
            "('J', 'Empresa de Serviços Exemplo Ltda', '12.345.678/0001-90', 'Rua das Amostras', " +
            "'123', 'Centro', 'São Paulo', 'SP', '01001-000', '(11) 99999-9999', " +
            "'contato@empresaexemplo.com.br', 'www.empresaexemplo.com.br'), " +
            "('F', 'João da Silva', '123.456.789-00', 'Rua Exemplo', " +
            "'456', 'Centro', 'Rio de Janeiro', 'RJ', '20000-000', '(21) 98888-7777', " +
            "'contato@exemplo.com', NULL)"
        );
        
        // 2. Inserir categorias padrão para cada empresa
        stmt.execute(
            "INSERT INTO tb_categorias (nome, descricao, empresa_sistema_id) VALUES " +
            "('Prestação de Serviços', 'Serviços prestados em geral', 1), " +
            "('Venda de Produtos', 'Venda de mercadorias', 1), " +
            "('Aluguel', 'Locação de bens', 1), " +
            "('Consultoria', 'Serviços de consultoria', 1), " +
            "('Transporte', 'Serviços de transporte', 1), " +
            "('Prestação de Serviços', 'Serviços prestados', 2), " +
            "('Venda de Produtos', 'Vendas', 2)"
        );
        
        // 3. Inserir clientes de exemplo
        stmt.execute(
            "INSERT INTO tb_clientes (tipo_pessoa, nome_razao_social, nome_fantasia, cpf_cnpj, " +
            "endereco_completo, numero, bairro, cidade, estado, cep, telefone, celular, email, empresa_sistema_id) VALUES " +
            "('J', 'Laticínios Tirolez Ltda', 'Tirolez', '55.885.321/0001-02', " +
            "'Av. Pres. Juscelino Kubitschek', '1830', 'Vila Nova Conceição', 'São Paulo', 'SP', " +
            "'04543-000', '(11) 3333-4444', '(11) 99999-8888', 'contato@tirolez.com.br', 1), " +
            "('F', 'João Silva Santos', NULL, '123.456.789-00', " +
            "'Rua das Acácias', '456', 'Jardim Karaíba', 'Uberlândia', 'MG', " +
            "'38411-000', '(34) 3232-1111', '(34) 99876-5432', 'joao@email.com', 1), " +
            "('J', 'Supermercado Bom Preço Ltda', 'Bom Preço', '12.345.678/0001-90', " +
            "'Av. Rondon Pacheco', '2000', 'Tibery', 'Uberlândia', 'MG', " +
            "'38405-000', '(34) 3333-2222', '(34) 99988-7766', 'contato@bompreco.com', 1), " +
            "('F', 'Maria Oliveira Costa', NULL, '987.654.321-11', " +
            "'Rua Santos Dumont', '789', 'Centro', 'Uberlândia', 'MG', " +
            "'38400-100', '(34) 3234-5678', '(34) 99765-4321', 'maria@email.com', 1), " +
            "('J', 'Tech Solutions Informática Ltda', 'TechSolutions', '98.765.432/0001-10', " +
            "'Av. Afonso Pena', '500', 'Brasil', 'Uberlândia', 'MG', " +
            "'38400-130', '(34) 3330-4455', '(34) 99123-4567', 'contato@techsolutions.com', 1)"
        );
        
        // 4. Inserir recibos de exemplo
        stmt.execute(
            "INSERT INTO tb_recibos (numero_recibo, cliente_id, categoria_id, valor, valor_extenso, " +
            "referente, forma_pagamento, data_emissao, empresa_sistema_id) VALUES " +
            "('001-2025', 1, 1, 200.00, 'duzentos reais', " +
            "'Serviço de entrega expressa de documentos em São Paulo/SP', 'PIX', date('now', '-5 days'), 1), " +
            "('002-2025', 2, 1, 150.00, 'cento e cinquenta reais', " +
            "'Serviço de manutenção residencial', 'Dinheiro', date('now', '-3 days'), 1), " +
            "('003-2025', 3, 5, 350.00, 'trezentos e cinquenta reais', " +
            "'Transporte de mercadorias de Uberlândia para Araguari', 'Transferência Bancária', date('now', '-2 days'), 1), " +
            "('004-2025', 4, 1, 500.00, 'quinhentos reais', " +
            "'Consultoria em organização doméstica', 'Cartão de Crédito', date('now', '-1 days'), 1), " +
            "('005-2025', 5, 4, 1200.00, 'um mil e duzentos reais', " +
            "'Consultoria em sistemas de informação', 'PIX', date('now'), 1), " +
            "('006-2025', 1, 5, 450.00, 'quatrocentos e cinquenta reais', " +
            "'Entrega de equipamentos em Campinas/SP', 'PIX', date('now'), 1)"
        );
        
        // 5. Inserir configuração inicial (empresa padrão = 1)
        stmt.execute(
            "INSERT INTO tb_configuracoes (chave, valor) VALUES " +
            "('empresa_padrao_id', '1'), " +
            "('versao_sistema', '1.0.0')"
        );
        
        logger.info("✅ Dados de exemplo inseridos: 2 empresas, 7 categorias, 5 clientes, 6 recibos, 2 configurações");
    }
}


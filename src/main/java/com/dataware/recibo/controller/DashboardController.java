package com.dataware.recibo.controller;

import com.dataware.recibo.ReciboApplication;
import com.dataware.recibo.dao.ClienteDAO;
import com.dataware.recibo.dao.ReciboDAO;
import com.dataware.recibo.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @FXML
    private Label lblEmpresaLogada;

    @FXML
    private Label lblTotalRecibos;

    @FXML
    private Label lblTotalClientes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        carregarDadosEmpresa();
        carregarEstatisticas();
    }

    private void carregarDadosEmpresa() {
        if (SessionManager.getInstance().isLogado()) {
            lblEmpresaLogada.setText("Empresa: " + SessionManager.getInstance().getEmpresaLogada().getNomeRazaoSocial());
        }
    }

    private void carregarEstatisticas() {
        try {
            ReciboDAO reciboDAO = new ReciboDAO();
            ClienteDAO clienteDAO = new ClienteDAO();
            
            int totalRecibos = reciboDAO.listar().size();
            int totalClientes = clienteDAO.listar().size();
            
            lblTotalRecibos.setText(String.valueOf(totalRecibos));
            lblTotalClientes.setText(String.valueOf(totalClientes));
        } catch (SQLException e) {
            logger.error("Erro ao carregar estatísticas", e);
        }
    }

    @FXML
    private void handleNovoRecibo() {
        try {
            ReciboApplication.carregarTela("/com/dataware/recibo/recibo-form-view.fxml", 900, 700);
        } catch (IOException e) {
            logger.error("Erro ao carregar formulário de recibo", e);
            mostrarErro("Erro", "Não foi possível carregar o formulário de recibo.");
        }
    }

    @FXML
    private void handleListarRecibos() {
        try {
            ReciboApplication.carregarTela("/com/dataware/recibo/recibo-list-view.fxml", 1000, 600);
        } catch (IOException e) {
            logger.error("Erro ao carregar lista de recibos", e);
            mostrarErro("Erro", "Não foi possível carregar a lista de recibos.");
        }
    }

    @FXML
    private void handleClientes() {
        try {
            ReciboApplication.carregarTela("/com/dataware/recibo/cliente-list-view.fxml", 1000, 600);
        } catch (IOException e) {
            logger.error("Erro ao carregar lista de clientes", e);
            mostrarErro("Erro", "Não foi possível carregar a lista de clientes.");
        }
    }

    @FXML
    private void handleCategorias() {
        try {
            ReciboApplication.carregarTela("/com/dataware/recibo/categoria-list-view.fxml", 800, 500);
        } catch (IOException e) {
            logger.error("Erro ao carregar lista de categorias", e);
            mostrarErro("Erro", "Não foi possível carregar a lista de categorias.");
        }
    }

    @FXML
    private void handleTrocarEmpresa() {
        SessionManager.getInstance().logout();
        try {
            ReciboApplication.carregarTela("/com/dataware/recibo/login-view.fxml", 600, 400);
        } catch (IOException e) {
            logger.error("Erro ao carregar tela de login", e);
            mostrarErro("Erro", "Não foi possível voltar para a tela de login.");
        }
    }

    @FXML
    private void handleSair() {
        System.exit(0);
    }

    @FXML
    private void handleSobre() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sobre");
        alert.setHeaderText("Sistema de Gestão de Recibos");
        alert.setContentText("Versão: 1.0\n" +
                           "Desenvolvido por: DataWare Soluções\n" +
                           "© 2025 Todos os direitos reservados\n\n" +
                           "Sistema para geração e gerenciamento de recibos.");
        alert.showAndWait();
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}




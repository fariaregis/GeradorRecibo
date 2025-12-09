package com.dataware.recibo.controller;

import com.dataware.recibo.ReciboApplication;
import com.dataware.recibo.dao.ConfiguracaoDAO;
import com.dataware.recibo.dao.EmpresaDAO;
import com.dataware.recibo.model.Empresa;
import com.dataware.recibo.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private ComboBox<Empresa> empresaComboBox;

    @FXML
    private CheckBox chkEmpresaPadrao;

    @FXML
    private Button btnEntrar;

    @FXML
    private Button btnNovaEmpresa;

    @FXML
    private Label lblStatus;

    private EmpresaDAO empresaDAO;
    private ConfiguracaoDAO configuracaoDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        empresaDAO = new EmpresaDAO();
        configuracaoDAO = new ConfiguracaoDAO();
        carregarEmpresas();
    }

    private void carregarEmpresas() {
        try {
            List<Empresa> empresas = empresaDAO.listar();
            empresaComboBox.getItems().clear();
            empresaComboBox.getItems().addAll(empresas);
            
            if (empresas.isEmpty()) {
                // Não há empresas - redirecionar para primeiro acesso
                logger.info("Nenhuma empresa cadastrada. Redirecionando para primeiro acesso.");
                try {
                    ReciboApplication.carregarTela("/com/dataware/recibo/primeiro-acesso-view.fxml", 800, 650);
                } catch (IOException ex) {
                    logger.error("Erro ao carregar tela de primeiro acesso", ex);
                    mostrarErro("Erro", "Não foi possível carregar a tela de cadastro.");
                }
            } else {
                // Verificar se há empresa padrão
                try {
                    Integer empresaPadraoId = configuracaoDAO.buscarEmpresaPadrao();
                    if (empresaPadraoId != null && empresaPadraoId > 0) {
                        // Selecionar empresa padrão
                        for (Empresa emp : empresas) {
                            if (emp.getId().equals(empresaPadraoId)) {
                                empresaComboBox.getSelectionModel().select(emp);
                                chkEmpresaPadrao.setSelected(true);
                                logger.info("Empresa padrão carregada: " + emp.getNomeRazaoSocial());
                                // Auto-login após um pequeno delay para garantir que a UI foi carregada
                                javafx.application.Platform.runLater(() -> {
                                    try {
                                        Thread.sleep(100);
                                        handleEntrar();
                                    } catch (InterruptedException e) {
                                        logger.error("Erro no auto-login", e);
                                    }
                                });
                                return;
                            }
                        }
                    }
                } catch (SQLException ex) {
                    logger.error("Erro ao buscar empresa padrão", ex);
                }
                
                // Seleciona a primeira empresa por padrão
                empresaComboBox.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            logger.error("Erro ao carregar empresas", e);
            mostrarErro("Erro ao carregar empresas", "Não foi possível conectar ao banco de dados.\n" + e.getMessage());
            lblStatus.setText("Erro ao conectar ao banco de dados.");
        }
    }

    @FXML
    private void handleEntrar() {
        Empresa empresaSelecionada = empresaComboBox.getSelectionModel().getSelectedItem();
        
        if (empresaSelecionada == null) {
            lblStatus.setText("Por favor, selecione uma empresa.");
            return;
        }

        // Salvar empresa padrão se marcado
        if (chkEmpresaPadrao.isSelected()) {
            try {
                configuracaoDAO.salvarEmpresaPadrao(empresaSelecionada.getId());
                logger.info("Empresa padrão definida: " + empresaSelecionada.getNomeRazaoSocial());
            } catch (SQLException e) {
                logger.error("Erro ao salvar empresa padrão", e);
            }
        } else {
            // Remover empresa padrão se desmarcado
            try {
                configuracaoDAO.salvar("empresa_padrao_id", "");
            } catch (SQLException e) {
                logger.error("Erro ao remover empresa padrão", e);
            }
        }

        // Define a empresa na sessão
        SessionManager.getInstance().setEmpresaLogada(empresaSelecionada);
        
        logger.info("Empresa logada: " + empresaSelecionada.getNomeRazaoSocial());

        // Carrega o dashboard
        try {
            ReciboApplication.carregarTela("/com/dataware/recibo/dashboard-view.fxml", 1200, 700);
        } catch (IOException e) {
            logger.error("Erro ao carregar dashboard", e);
            mostrarErro("Erro", "Não foi possível carregar a tela principal.");
        }
    }

    @FXML
    private void handleNovaEmpresa() {
        try {
            ReciboApplication.carregarTela("/com/dataware/recibo/primeiro-acesso-view.fxml", 800, 650);
        } catch (IOException e) {
            logger.error("Erro ao carregar formulário de empresa", e);
            mostrarErro("Erro", "Não foi possível carregar o formulário de cadastro.");
        }
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}


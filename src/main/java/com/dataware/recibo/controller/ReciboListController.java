package com.dataware.recibo.controller;

import com.dataware.recibo.ReciboApplication;
import com.dataware.recibo.dao.ReciboDAO;
import com.dataware.recibo.model.Recibo;
import com.dataware.recibo.service.ReciboPDFService;
import com.dataware.recibo.service.ReciboPDFServicePro;
import com.dataware.recibo.util.FormatUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ReciboListController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(ReciboListController.class);

    @FXML
    private TextField txtBusca;

    @FXML
    private TableView<Recibo> tableRecibos;

    @FXML
    private Label lblTotal;

    private ReciboDAO reciboDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reciboDAO = new ReciboDAO();
        configurarTabela();
        carregarRecibos();
    }

    private void configurarTabela() {
        // Configurar coluna de ações
        TableColumn<Recibo, Void> colAcoes = (TableColumn<Recibo, Void>) tableRecibos.getColumns().get(5);
        colAcoes.setCellFactory(param -> new TableCell<>() {
            private final Button btnPdfPro = new Button("📄 PRO");
            private final Button btnPdfSimples = new Button("PDF");
            private final Button btnExcluir = new Button("🗑");
            private final HBox pane;

            {
                // Botão PRO - Destaque principal (verde)
                btnPdfPro.setStyle("-fx-background-color: #198754; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-cursor: hand;");
                btnPdfPro.setTooltip(new Tooltip("Gerar PDF Profissional (2 vias)"));
                
                // Botão PDF simples (azul discreto)
                btnPdfSimples.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-size: 10px; -fx-cursor: hand;");
                btnPdfSimples.setTooltip(new Tooltip("Gerar PDF Simples"));
                
                // Botão excluir (vermelho)
                btnExcluir.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand;");
                btnExcluir.setTooltip(new Tooltip("Excluir recibo"));
                
                btnPdfPro.setOnAction(event -> {
                    Recibo recibo = getTableView().getItems().get(getIndex());
                    handleGerarPDFPro(recibo);
                });
                
                btnPdfSimples.setOnAction(event -> {
                    Recibo recibo = getTableView().getItems().get(getIndex());
                    handleGerarPDF(recibo);
                });

                btnExcluir.setOnAction(event -> {
                    Recibo recibo = getTableView().getItems().get(getIndex());
                    handleExcluirRecibo(recibo);
                });

                pane = new HBox(5, btnPdfPro, btnPdfSimples, btnExcluir);
                pane.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
        
        // Formatar coluna de data
        @SuppressWarnings("unchecked")
        TableColumn<Recibo, Object> colData = (TableColumn<Recibo, Object>) tableRecibos.getColumns().get(1);
        colData.setCellFactory(col -> new TableCell<Recibo, Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Recibo recibo = getTableView().getItems().get(getIndex());
                    setText(FormatUtil.formatData(recibo.getDataEmissao()));
                }
            }
        });
        
        // Formatar coluna de valor
        @SuppressWarnings("unchecked")
        TableColumn<Recibo, Object> colValor = (TableColumn<Recibo, Object>) tableRecibos.getColumns().get(4);
        colValor.setCellFactory(col -> new TableCell<Recibo, Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Recibo recibo = getTableView().getItems().get(getIndex());
                    setText(FormatUtil.formatMoeda(recibo.getValor()));
                }
            }
        });
    }

    private void carregarRecibos() {
        try {
            List<Recibo> recibos = reciboDAO.listar();
            tableRecibos.getItems().clear();
            tableRecibos.getItems().addAll(recibos);
            lblTotal.setText("Total: " + recibos.size() + " recibos");
        } catch (SQLException e) {
            logger.error("Erro ao carregar recibos", e);
            mostrarErro("Erro", "Não foi possível carregar a lista de recibos.");
        }
    }

    @FXML
    private void handleBuscar() {
        String termo = txtBusca.getText();
        if (termo == null || termo.trim().isEmpty()) {
            carregarRecibos();
            return;
        }

        try {
            List<Recibo> recibos = reciboDAO.buscarPorNumero(termo);
            tableRecibos.getItems().clear();
            tableRecibos.getItems().addAll(recibos);
            lblTotal.setText("Encontrados: " + recibos.size() + " recibos");
        } catch (SQLException e) {
            logger.error("Erro ao buscar recibos", e);
            mostrarErro("Erro", "Não foi possível buscar recibos.");
        }
    }

    @FXML
    private void handleNovoRecibo() {
        try {
            ReciboApplication.carregarTela("/com/dataware/recibo/recibo-form-view.fxml", 900, 700);
        } catch (IOException e) {
            logger.error("Erro ao carregar formulário de recibo", e);
            mostrarErro("Erro", "Não foi possível abrir o formulário de recibo.");
        }
    }

    /**
     * Gera PDF Profissional (2 vias: Cliente e Arquivo)
     */
    private void handleGerarPDFPro(Recibo recibo) {
        try {
            Recibo reciboCompleto = reciboDAO.buscarPorIdCompleto(recibo.getId());
            
            if (reciboCompleto == null) {
                mostrarErro("Erro", "Recibo não encontrado.");
                return;
            }

            String tempDir = System.getProperty("java.io.tmpdir");
            String tempFileName = "Recibo_PRO_" + reciboCompleto.getNumeroRecibo() + ".pdf";
            File tempFile = new File(tempDir, tempFileName);
            
            // Usar o novo serviço PRO
            ReciboPDFServicePro pdfService = new ReciboPDFServicePro();
            pdfService.gerarPDF(reciboCompleto, tempFile.getAbsolutePath());
            
            // Abrir automaticamente
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(tempFile);
            }
            
            // Opção de salvar
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("PDF PRO Gerado");
            confirmacao.setHeaderText("Recibo profissional gerado com sucesso!");
            confirmacao.setContentText("O PDF contém 2 vias (Cliente e Arquivo).\nDeseja salvar em outro local?");
            
            ButtonType btnSalvar = new ButtonType("Salvar Como...");
            ButtonType btnOk = new ButtonType("OK");
            confirmacao.getButtonTypes().setAll(btnSalvar, btnOk);
            
            if (confirmacao.showAndWait().get() == btnSalvar) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Salvar Recibo PDF PRO");
                fileChooser.setInitialFileName("Recibo_PRO_" + reciboCompleto.getNumeroRecibo() + ".pdf");
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
                );

                File file = fileChooser.showSaveDialog(ReciboApplication.getPrimaryStage());
                
                if (file != null) {
                    java.nio.file.Files.copy(
                        tempFile.toPath(), 
                        file.toPath(), 
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                    );
                    mostrarSucesso("PDF PRO salvo com sucesso!\n" + file.getAbsolutePath());
                }
            }

        } catch (SQLException e) {
            logger.error("Erro ao buscar recibo", e);
            mostrarErro("Erro", "Não foi possível buscar os dados do recibo.");
        } catch (IOException e) {
            logger.error("Erro ao gerar PDF PRO", e);
            mostrarErro("Erro", "Não foi possível gerar o PDF PRO.\n" + e.getMessage());
        }
    }

    /**
     * Gera PDF simples (versão original)
     */
    private void handleGerarPDF(Recibo recibo) {
        try {
            // Buscar recibo completo
            Recibo reciboCompleto = reciboDAO.buscarPorIdCompleto(recibo.getId());
            
            if (reciboCompleto == null) {
                mostrarErro("Erro", "Recibo não encontrado.");
                return;
            }

            // Gerar PDF em arquivo temporário primeiro
            String tempDir = System.getProperty("java.io.tmpdir");
            String tempFileName = "Recibo_" + reciboCompleto.getNumeroRecibo() + "_temp.pdf";
            File tempFile = new File(tempDir, tempFileName);
            
            ReciboPDFService pdfService = new ReciboPDFService();
            pdfService.gerarPDF(reciboCompleto, tempFile.getAbsolutePath());
            
            // Abrir o PDF automaticamente
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(tempFile);
            }
            
            // Perguntar se deseja salvar em local específico
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("PDF Gerado");
            confirmacao.setHeaderText("O recibo foi aberto para visualização!");
            confirmacao.setContentText("Deseja salvar o PDF em um local específico?");
            
            ButtonType btnSalvar = new ButtonType("Salvar Como...");
            ButtonType btnCancelar = new ButtonType("Não, obrigado");
            confirmacao.getButtonTypes().setAll(btnSalvar, btnCancelar);
            
            if (confirmacao.showAndWait().get() == btnSalvar) {
                // Escolher local para salvar
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Salvar Recibo PDF");
                fileChooser.setInitialFileName("Recibo_" + reciboCompleto.getNumeroRecibo() + ".pdf");
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
                );

                File file = fileChooser.showSaveDialog(ReciboApplication.getPrimaryStage());
                
                if (file != null) {
                    // Copiar arquivo temporário para o local escolhido
                    java.nio.file.Files.copy(
                        tempFile.toPath(), 
                        file.toPath(), 
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                    );
                    mostrarSucesso("PDF salvo com sucesso!\n" + file.getAbsolutePath());
                }
            }

        } catch (SQLException e) {
            logger.error("Erro ao buscar recibo", e);
            mostrarErro("Erro", "Não foi possível buscar os dados do recibo.");
        } catch (IOException e) {
            logger.error("Erro ao gerar ou abrir PDF", e);
            mostrarErro("Erro", "Não foi possível gerar ou abrir o PDF do recibo.\n" + e.getMessage());
        }
    }

    private void handleExcluirRecibo(Recibo recibo) {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText("Deseja realmente excluir o recibo?");
        confirmacao.setContentText("Número: " + recibo.getNumeroRecibo() + "\nCliente: " + recibo.getCliente());

        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                reciboDAO.deletar(recibo.getId());
                carregarRecibos();
                mostrarSucesso("Recibo excluído com sucesso!");
            } catch (SQLException e) {
                logger.error("Erro ao excluir recibo", e);
                mostrarErro("Erro", "Não foi possível excluir o recibo.");
            }
        }
    }

    @FXML
    private void handleVoltar() {
        try {
            ReciboApplication.carregarTela("/com/dataware/recibo/dashboard-view.fxml", 1200, 700);
        } catch (IOException e) {
            logger.error("Erro ao voltar para dashboard", e);
        }
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}


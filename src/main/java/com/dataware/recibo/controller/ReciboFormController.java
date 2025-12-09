package com.dataware.recibo.controller;

import com.dataware.recibo.ReciboApplication;
import com.dataware.recibo.dao.CategoriaDAO;
import com.dataware.recibo.dao.ClienteDAO;
import com.dataware.recibo.dao.ReciboDAO;
import com.dataware.recibo.model.Categoria;
import com.dataware.recibo.model.Cliente;
import com.dataware.recibo.model.Recibo;
import com.dataware.recibo.service.ReciboPDFService;
import com.dataware.recibo.util.FormatUtil;
import com.dataware.recibo.util.NumeroExtensoUtil;
import com.dataware.recibo.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ReciboFormController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(ReciboFormController.class);

    @FXML
    private TextField txtNumeroRecibo;

    @FXML
    private DatePicker dpDataEmissao;

    @FXML
    private ComboBox<Categoria> cbCategoria;

    @FXML
    private ComboBox<Cliente> cbCliente;

    @FXML
    private Label lblDadosCliente;

    @FXML
    private TextField txtValor;

    @FXML
    private ComboBox<String> cbFormaPagamento;

    @FXML
    private Label lblValorExtenso;

    @FXML
    private TextArea txtReferente;

    @FXML
    private TextArea txtObservacoes;

    private ReciboDAO reciboDAO;
    private ClienteDAO clienteDAO;
    private CategoriaDAO categoriaDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reciboDAO = new ReciboDAO();
        clienteDAO = new ClienteDAO();
        categoriaDAO = new CategoriaDAO();

        configurarCampos();
        carregarDados();
        gerarProximoNumero();
    }

    private void configurarCampos() {
        // Data atual
        dpDataEmissao.setValue(LocalDate.now());

        // Formas de pagamento
        cbFormaPagamento.getItems().addAll(
                "Dinheiro", "PIX", "Transferência Bancária", 
                "Cartão de Débito", "Cartão de Crédito", "Cheque", "Outros"
        );

        // Listener para atualizar valor por extenso
        txtValor.textProperty().addListener((obs, oldValue, newValue) -> {
            atualizarValorExtenso();
        });

        // Listener para mostrar dados do cliente
        cbCliente.setOnAction(event -> {
            Cliente cliente = cbCliente.getSelectionModel().getSelectedItem();
            if (cliente != null) {
                mostrarDadosCliente(cliente);
            }
        });
    }

    private void carregarDados() {
        try {
            // Carregar categorias
            List<Categoria> categorias = categoriaDAO.listar();
            cbCategoria.getItems().clear();
            cbCategoria.getItems().addAll(categorias);
            if (!categorias.isEmpty()) {
                cbCategoria.getSelectionModel().selectFirst();
            }

            // Carregar clientes
            handleAtualizarClientes();
        } catch (SQLException e) {
            logger.error("Erro ao carregar dados", e);
            mostrarErro("Erro", "Não foi possível carregar os dados necessários.");
        }
    }

    @FXML
    private void handleAtualizarClientes() {
        try {
            List<Cliente> clientes = clienteDAO.listar();
            cbCliente.getItems().clear();
            cbCliente.getItems().addAll(clientes);
        } catch (SQLException e) {
            logger.error("Erro ao carregar clientes", e);
            mostrarErro("Erro", "Não foi possível carregar a lista de clientes.");
        }
    }

    private void gerarProximoNumero() {
        try {
            String proximoNumero = reciboDAO.gerarProximoNumero();
            txtNumeroRecibo.setText(proximoNumero);
        } catch (SQLException e) {
            logger.error("Erro ao gerar próximo número", e);
            txtNumeroRecibo.setText("000001");
        }
    }

    private void atualizarValorExtenso() {
        try {
            String valorTexto = txtValor.getText().replace(",", ".");
            if (!valorTexto.isEmpty()) {
                BigDecimal valor = new BigDecimal(valorTexto);
                String extenso = NumeroExtensoUtil.valorPorExtenso(valor);
                lblValorExtenso.setText(extenso);
            } else {
                lblValorExtenso.setText("Valor por extenso aparecerá aqui");
            }
        } catch (NumberFormatException e) {
            lblValorExtenso.setText("Valor inválido");
        }
    }

    private void mostrarDadosCliente(Cliente cliente) {
        StringBuilder dados = new StringBuilder();
        dados.append(cliente.getNomeRazaoSocial());
        
        if (cliente.getCpfCnpj() != null && !cliente.getCpfCnpj().isEmpty()) {
            dados.append(" | ").append(FormatUtil.formatCPFouCNPJ(cliente.getCpfCnpj()));
        }
        
        if (cliente.getCidade() != null) {
            dados.append(" | ").append(cliente.getCidade());
            if (cliente.getEstado() != null) {
                dados.append("/").append(cliente.getEstado());
            }
        }
        
        if (cliente.getTelefone() != null) {
            dados.append(" | Tel: ").append(FormatUtil.formatTelefone(cliente.getTelefone()));
        }
        
        lblDadosCliente.setText(dados.toString());
    }

    @FXML
    private void handleGerarRecibo() {
        if (!validarCampos()) {
            return;
        }

        try {
            // Criar objeto Recibo
            Recibo recibo = new Recibo();
            recibo.setEmpresaSistemaId(SessionManager.getInstance().getEmpresaSistemaId());
            recibo.setNumeroRecibo(txtNumeroRecibo.getText());
            recibo.setClienteId(cbCliente.getSelectionModel().getSelectedItem().getId());
            recibo.setCategoriaId(cbCategoria.getSelectionModel().getSelectedItem().getId());
            
            String valorTexto = txtValor.getText().replace(",", ".");
            recibo.setValor(new BigDecimal(valorTexto));
            recibo.setValorExtenso(lblValorExtenso.getText());
            
            recibo.setReferente(txtReferente.getText());
            recibo.setDataEmissao(dpDataEmissao.getValue());
            recibo.setFormaPagamento(cbFormaPagamento.getSelectionModel().getSelectedItem());
            recibo.setObservacoes(txtObservacoes.getText());

            // Inserir no banco
            Integer reciboId = reciboDAO.inserir(recibo);
            recibo.setId(reciboId);
            
            logger.info("Recibo inserido com ID: " + reciboId);

            // Perguntar se deseja gerar PDF
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Recibo Criado");
            confirmacao.setHeaderText("Recibo criado com sucesso!");
            confirmacao.setContentText("Deseja gerar o PDF do recibo agora?");
            
            if (confirmacao.showAndWait().get() == ButtonType.OK) {
                gerarPDF(reciboId);
            }

            // Limpar formulário
            handleLimpar();
            gerarProximoNumero();
            
            mostrarSucesso("Recibo criado com sucesso!");

        } catch (SQLException e) {
            logger.error("Erro ao criar recibo", e);
            mostrarErro("Erro", "Não foi possível criar o recibo.\n" + e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado", e);
            mostrarErro("Erro", "Erro inesperado ao criar recibo.");
        }
    }

    private void gerarPDF(Integer reciboId) {
        try {
            // Buscar recibo completo
            Recibo recibo = reciboDAO.buscarPorIdCompleto(reciboId);
            
            if (recibo == null) {
                mostrarErro("Erro", "Recibo não encontrado.");
                return;
            }

            // Gerar PDF em arquivo temporário primeiro
            String tempDir = System.getProperty("java.io.tmpdir");
            String tempFileName = "Recibo_" + recibo.getNumeroRecibo() + "_temp.pdf";
            File tempFile = new File(tempDir, tempFileName);
            
            ReciboPDFService pdfService = new ReciboPDFService();
            pdfService.gerarPDF(recibo, tempFile.getAbsolutePath());
            
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
                fileChooser.setInitialFileName("Recibo_" + recibo.getNumeroRecibo() + ".pdf");
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

    @FXML
    private void handleLimpar() {
        txtValor.clear();
        txtReferente.clear();
        txtObservacoes.clear();
        cbCliente.getSelectionModel().clearSelection();
        cbFormaPagamento.getSelectionModel().clearSelection();
        lblDadosCliente.setText("");
        lblValorExtenso.setText("Valor por extenso aparecerá aqui");
        dpDataEmissao.setValue(LocalDate.now());
    }

    @FXML
    private void handleVoltar() {
        try {
            ReciboApplication.carregarTela("/com/dataware/recibo/dashboard-view.fxml", 1200, 700);
        } catch (IOException e) {
            logger.error("Erro ao voltar para dashboard", e);
        }
    }

    private boolean validarCampos() {
        if (txtNumeroRecibo.getText().isEmpty()) {
            mostrarErro("Validação", "Número do recibo é obrigatório.");
            return false;
        }

        if (cbCliente.getSelectionModel().getSelectedItem() == null) {
            mostrarErro("Validação", "Selecione um cliente.");
            return false;
        }

        if (cbCategoria.getSelectionModel().getSelectedItem() == null) {
            mostrarErro("Validação", "Selecione uma categoria.");
            return false;
        }

        if (txtValor.getText().isEmpty()) {
            mostrarErro("Validação", "Informe o valor do recibo.");
            return false;
        }

        try {
            String valorTexto = txtValor.getText().replace(",", ".");
            BigDecimal valor = new BigDecimal(valorTexto);
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarErro("Validação", "O valor deve ser maior que zero.");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarErro("Validação", "Valor inválido.");
            return false;
        }

        if (txtReferente.getText().isEmpty()) {
            mostrarErro("Validação", "Informe a que se refere o recibo.");
            return false;
        }

        if (dpDataEmissao.getValue() == null) {
            mostrarErro("Validação", "Selecione a data de emissão.");
            return false;
        }

        return true;
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


package com.dataware.recibo.controller;

import com.dataware.recibo.ReciboApplication;
import com.dataware.recibo.dao.ClienteDAO;
import com.dataware.recibo.model.Cliente;
import com.dataware.recibo.util.FormatUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClienteListController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(ClienteListController.class);

    @FXML
    private TextField txtBusca;

    @FXML
    private TableView<Cliente> tableClientes;

    @FXML
    private Label lblTotal;

    private ClienteDAO clienteDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clienteDAO = new ClienteDAO();
        configurarTabela();
        carregarClientes();
    }

    private void configurarTabela() {
        // Configurar coluna de ações
        TableColumn<Cliente, Void> colAcoes = (TableColumn<Cliente, Void>) tableClientes.getColumns().get(6);
        colAcoes.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnExcluir = new Button("Excluir");
            private final HBox pane;

            {
                btnEditar.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 11px;");
                btnExcluir.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 11px;");
                
                btnEditar.setOnAction(event -> {
                    Cliente cliente = getTableView().getItems().get(getIndex());
                    handleEditarCliente(cliente);
                });

                btnExcluir.setOnAction(event -> {
                    Cliente cliente = getTableView().getItems().get(getIndex());
                    handleExcluirCliente(cliente);
                });

                pane = new HBox(5, btnEditar, btnExcluir);
                pane.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void carregarClientes() {
        try {
            List<Cliente> clientes = clienteDAO.listar();
            tableClientes.getItems().clear();
            
            for (Cliente cliente : clientes) {
                // Formatar CPF/CNPJ
                if (cliente.getCpfCnpj() != null) {
                    cliente.setCpfCnpj(FormatUtil.formatCPFouCNPJ(cliente.getCpfCnpj()));
                }
                // Formatar telefone
                if (cliente.getTelefone() != null) {
                    cliente.setTelefone(FormatUtil.formatTelefone(cliente.getTelefone()));
                }
            }
            
            tableClientes.getItems().addAll(clientes);
            lblTotal.setText("Total: " + clientes.size() + " clientes");
        } catch (SQLException e) {
            logger.error("Erro ao carregar clientes", e);
            mostrarErro("Erro", "Não foi possível carregar a lista de clientes.");
        }
    }

    @FXML
    private void handleBuscar() {
        String termo = txtBusca.getText();
        if (termo == null || termo.trim().isEmpty()) {
            carregarClientes();
            return;
        }

        try {
            List<Cliente> clientes = clienteDAO.buscarPorNome(termo);
            tableClientes.getItems().clear();
            
            for (Cliente cliente : clientes) {
                if (cliente.getCpfCnpj() != null) {
                    cliente.setCpfCnpj(FormatUtil.formatCPFouCNPJ(cliente.getCpfCnpj()));
                }
                if (cliente.getTelefone() != null) {
                    cliente.setTelefone(FormatUtil.formatTelefone(cliente.getTelefone()));
                }
            }
            
            tableClientes.getItems().addAll(clientes);
            lblTotal.setText("Encontrados: " + clientes.size() + " clientes");
        } catch (SQLException e) {
            logger.error("Erro ao buscar clientes", e);
            mostrarErro("Erro", "Não foi possível buscar clientes.");
        }
    }

    @FXML
    private void handleNovoCliente() {
        abrirFormularioCliente(null);
    }

    private void handleEditarCliente(Cliente cliente) {
        abrirFormularioCliente(cliente);
    }

    private void abrirFormularioCliente(Cliente cliente) {
        try {
            ReciboApplication.carregarTela("/com/dataware/recibo/cliente-form-view.fxml", 800, 700);
            // TODO: Passar o cliente para edição se não for null
        } catch (IOException e) {
            logger.error("Erro ao abrir formulário de cliente", e);
            mostrarErro("Erro", "Não foi possível abrir o formulário.");
        }
    }

    private void handleExcluirCliente(Cliente cliente) {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText("Deseja realmente excluir o cliente?");
        confirmacao.setContentText(cliente.getNomeRazaoSocial());

        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                clienteDAO.deletar(cliente.getId());
                carregarClientes();
                mostrarSucesso("Cliente excluído com sucesso!");
            } catch (SQLException e) {
                logger.error("Erro ao excluir cliente", e);
                mostrarErro("Erro", "Não foi possível excluir o cliente.");
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

    private void mostrarInfo(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}


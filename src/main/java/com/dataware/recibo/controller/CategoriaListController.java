package com.dataware.recibo.controller;

import com.dataware.recibo.ReciboApplication;
import com.dataware.recibo.dao.CategoriaDAO;
import com.dataware.recibo.model.Categoria;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CategoriaListController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(CategoriaListController.class);

    @FXML
    private TableView<Categoria> tableCategorias;

    @FXML
    private Label lblTotal;

    private CategoriaDAO categoriaDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoriaDAO = new CategoriaDAO();
        configurarTabela();
        carregarCategorias();
    }

    private void configurarTabela() {
        // Configurar coluna de ações
        TableColumn<Categoria, Void> colAcoes = (TableColumn<Categoria, Void>) tableCategorias.getColumns().get(3);
        colAcoes.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnExcluir = new Button("Excluir");
            private final HBox pane;

            {
                btnEditar.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 11px;");
                btnExcluir.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 11px;");
                
                btnEditar.setOnAction(event -> {
                    Categoria categoria = getTableView().getItems().get(getIndex());
                    handleEditarCategoria(categoria);
                });

                btnExcluir.setOnAction(event -> {
                    Categoria categoria = getTableView().getItems().get(getIndex());
                    handleExcluirCategoria(categoria);
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

    private void carregarCategorias() {
        try {
            List<Categoria> categorias = categoriaDAO.listar();
            tableCategorias.getItems().clear();
            tableCategorias.getItems().addAll(categorias);
            lblTotal.setText("Total: " + categorias.size() + " categorias");
        } catch (SQLException e) {
            logger.error("Erro ao carregar categorias", e);
            mostrarErro("Erro", "Não foi possível carregar a lista de categorias.");
        }
    }

    @FXML
    private void handleNovaCategoria() {
        abrirFormularioCategoria(null);
    }

    private void handleEditarCategoria(Categoria categoria) {
        abrirFormularioCategoria(categoria);
    }

    private void abrirFormularioCategoria(Categoria categoria) {
        // Dialog para adicionar/editar categoria
        Dialog<Categoria> dialog = new Dialog<>();
        dialog.setTitle(categoria == null ? "Nova Categoria" : "Editar Categoria");
        dialog.setHeaderText(categoria == null ? "Cadastrar nova categoria" : "Editar categoria");

        ButtonType salvarButtonType = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(salvarButtonType, ButtonType.CANCEL);

        // Campos do formulário
        TextField txtNome = new TextField();
        txtNome.setPromptText("Nome da categoria");
        if (categoria != null) {
            txtNome.setText(categoria.getNome());
        }

        TextArea txtDescricao = new TextArea();
        txtDescricao.setPromptText("Descrição");
        txtDescricao.setPrefRowCount(3);
        if (categoria != null && categoria.getDescricao() != null) {
            txtDescricao.setText(categoria.getDescricao());
        }

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
        content.getChildren().addAll(
                new Label("Nome:"), txtNome,
                new Label("Descrição:"), txtDescricao
        );
        dialog.getDialogPane().setContent(content);

        // Converter resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == salvarButtonType) {
                Categoria cat = categoria != null ? categoria : new Categoria();
                cat.setNome(txtNome.getText());
                cat.setDescricao(txtDescricao.getText());
                return cat;
            }
            return null;
        });

        Optional<Categoria> resultado = dialog.showAndWait();
        resultado.ifPresent(cat -> {
            try {
                if (cat.getId() == null) {
                    categoriaDAO.inserir(cat);
                    mostrarSucesso("Categoria cadastrada com sucesso!");
                } else {
                    cat.setAtivo(true);
                    categoriaDAO.atualizar(cat);
                    mostrarSucesso("Categoria atualizada com sucesso!");
                }
                carregarCategorias();
            } catch (SQLException e) {
                logger.error("Erro ao salvar categoria", e);
                mostrarErro("Erro", "Não foi possível salvar a categoria.");
            }
        });
    }

    private void handleExcluirCategoria(Categoria categoria) {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText("Deseja realmente excluir a categoria?");
        confirmacao.setContentText(categoria.getNome());

        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                categoriaDAO.deletar(categoria.getId());
                carregarCategorias();
                mostrarSucesso("Categoria excluída com sucesso!");
            } catch (SQLException e) {
                logger.error("Erro ao excluir categoria", e);
                mostrarErro("Erro", "Não foi possível excluir a categoria.");
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




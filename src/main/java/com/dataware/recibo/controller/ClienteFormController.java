package com.dataware.recibo.controller;

import com.dataware.recibo.ReciboApplication;
import com.dataware.recibo.dao.ClienteDAO;
import com.dataware.recibo.model.Cliente;
import com.dataware.recibo.util.TextFieldFormatter;
import com.dataware.recibo.util.ValidatorUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ClienteFormController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(ClienteFormController.class);

    @FXML private Label lblTitulo;
    @FXML private RadioButton rbPessoaFisica;
    @FXML private RadioButton rbPessoaJuridica;
    @FXML private TextField txtNomeRazaoSocial;
    @FXML private TextField txtCpfCnpj;
    @FXML private TextField txtRgInscricaoEstadual;
    @FXML private TextField txtCep;
    @FXML private TextField txtEndereco;
    @FXML private TextField txtNumero;
    @FXML private TextField txtComplemento;
    @FXML private TextField txtBairro;
    @FXML private TextField txtCidade;
    @FXML private ComboBox<String> cbEstado;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtCelular;
    @FXML private TextField txtEmail;
    @FXML private TextArea txtObservacoes;
    @FXML private Label lblDocumento;
    @FXML private Label lblRgIe;

    private ToggleGroup tipoGrupo;
    private ClienteDAO clienteDAO;
    private Cliente clienteEdicao;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clienteDAO = new ClienteDAO();
        
        // Configurar grupo de radio buttons
        tipoGrupo = new ToggleGroup();
        rbPessoaFisica.setToggleGroup(tipoGrupo);
        rbPessoaJuridica.setToggleGroup(tipoGrupo);
        
        // Listener para mudança de tipo
        tipoGrupo.selectedToggleProperty().addListener((obs, old, newVal) -> {
            atualizarLabels();
        });
        
        // Carregar estados
        cbEstado.getItems().addAll(
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA",
            "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN",
            "RS", "RO", "RR", "SC", "SP", "SE", "TO"
        );
        
        // Aplicar formatadores
        TextFieldFormatter.formatarCPFouCNPJ(txtCpfCnpj);
        TextFieldFormatter.formatarCEP(txtCep);
        TextFieldFormatter.formatarTelefoneOuCelular(txtTelefone);
        TextFieldFormatter.formatarCelular(txtCelular);
        
        atualizarLabels();
    }

    public void setCliente(Cliente cliente) {
        this.clienteEdicao = cliente;
        lblTitulo.setText("Editar Cliente");
        preencherCampos(cliente);
    }

    private void preencherCampos(Cliente cliente) {
        if ("J".equals(cliente.getTipoPessoa())) {
            rbPessoaJuridica.setSelected(true);
        } else {
            rbPessoaFisica.setSelected(true);
        }
        
        txtNomeRazaoSocial.setText(cliente.getNomeRazaoSocial());
        txtCpfCnpj.setText(cliente.getCpfCnpj());
        txtRgInscricaoEstadual.setText(cliente.getRgInscricaoEstadual());
        txtCep.setText(cliente.getCep());
        txtEndereco.setText(cliente.getEndereco());
        txtNumero.setText(cliente.getNumero());
        txtComplemento.setText(cliente.getComplemento());
        txtBairro.setText(cliente.getBairro());
        txtCidade.setText(cliente.getCidade());
        cbEstado.setValue(cliente.getEstado());
        txtTelefone.setText(cliente.getTelefone());
        txtCelular.setText(cliente.getCelular());
        txtEmail.setText(cliente.getEmail());
        txtObservacoes.setText(cliente.getObservacoes());
    }

    private void atualizarLabels() {
        boolean isPessoaJuridica = rbPessoaJuridica.isSelected();
        
        lblDocumento.setText(isPessoaJuridica ? "* CNPJ:" : "* CPF:");
        txtCpfCnpj.setPromptText(isPessoaJuridica ? "00.000.000/0000-00" : "000.000.000-00");
        
        lblRgIe.setText(isPessoaJuridica ? "Inscrição Estadual:" : "RG:");
        txtRgInscricaoEstadual.setPromptText(isPessoaJuridica ? "Inscrição Estadual" : "RG");
    }

    @FXML
    private void handleSalvar() {
        if (!validarCampos()) {
            return;
        }

        try {
            Cliente cliente = clienteEdicao != null ? clienteEdicao : new Cliente();
            
            cliente.setTipoPessoa(rbPessoaJuridica.isSelected() ? "J" : "F");
            cliente.setNomeRazaoSocial(txtNomeRazaoSocial.getText().trim());
            cliente.setCpfCnpj(txtCpfCnpj.getText().replaceAll("[^0-9]", ""));
            cliente.setRgInscricaoEstadual(txtRgInscricaoEstadual.getText().trim());
            cliente.setCep(txtCep.getText().replaceAll("[^0-9]", ""));
            cliente.setEndereco(txtEndereco.getText().trim());
            cliente.setNumero(txtNumero.getText().trim());
            cliente.setComplemento(txtComplemento.getText().trim());
            cliente.setBairro(txtBairro.getText().trim());
            cliente.setCidade(txtCidade.getText().trim());
            cliente.setEstado(cbEstado.getValue());
            cliente.setTelefone(txtTelefone.getText().replaceAll("[^0-9]", ""));
            cliente.setCelular(txtCelular.getText().replaceAll("[^0-9]", ""));
            cliente.setEmail(txtEmail.getText().trim());
            cliente.setObservacoes(txtObservacoes.getText().trim());
            cliente.setAtivo(true);

            if (clienteEdicao != null) {
                // Atualizar
                clienteDAO.atualizar(cliente);
                mostrarSucesso("Cliente atualizado com sucesso!");
            } else {
                // Inserir
                Integer id = clienteDAO.inserir(cliente);
                logger.info("Cliente cadastrado com ID: " + id);
                mostrarSucesso("Cliente cadastrado com sucesso!");
            }

            handleVoltar();

        } catch (SQLException e) {
            logger.error("Erro ao salvar cliente", e);
            mostrarErro("Erro", "Não foi possível salvar o cliente.\n" + e.getMessage());
        }
    }

    @FXML
    private void handleVoltar() {
        try {
            ReciboApplication.carregarTela("/com/dataware/recibo/cliente-list-view.fxml", 1000, 600);
        } catch (IOException e) {
            logger.error("Erro ao voltar para lista de clientes", e);
        }
    }

    private boolean validarCampos() {
        // Nome/Razão Social
        if (txtNomeRazaoSocial.getText().trim().isEmpty()) {
            mostrarErro("Validação", "Nome/Razão Social é obrigatório.");
            return false;
        }

        // CPF/CNPJ
        String documento = txtCpfCnpj.getText().replaceAll("[^0-9]", "");
        if (documento.isEmpty()) {
            mostrarErro("Validação", "CPF/CNPJ é obrigatório.");
            return false;
        }

        if (rbPessoaFisica.isSelected()) {
            if (!ValidatorUtil.validarCPF(documento)) {
                mostrarErro("Validação", "CPF inválido.");
                return false;
            }
        } else {
            if (!ValidatorUtil.validarCNPJ(documento)) {
                mostrarErro("Validação", "CNPJ inválido.");
                return false;
            }
        }

        // E-mail (se preenchido)
        if (!txtEmail.getText().trim().isEmpty()) {
            if (!ValidatorUtil.validarEmail(txtEmail.getText().trim())) {
                mostrarErro("Validação", "E-mail inválido.");
                return false;
            }
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


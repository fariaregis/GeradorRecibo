package com.dataware.recibo.controller;

import com.dataware.recibo.ReciboApplication;
import com.dataware.recibo.dao.CategoriaDAO;
import com.dataware.recibo.dao.EmpresaDAO;
import com.dataware.recibo.model.Categoria;
import com.dataware.recibo.model.Empresa;
import com.dataware.recibo.util.SessionManager;
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
import java.util.List;
import java.util.ResourceBundle;

public class PrimeiroAcessoController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(PrimeiroAcessoController.class);

    @FXML private RadioButton rbPessoaFisica;
    @FXML private RadioButton rbPessoaJuridica;
    @FXML private TextField txtNomeRazaoSocial;
    @FXML private TextField txtNomeFantasia;
    @FXML private TextField txtCpfCnpj;
    @FXML private TextField txtRgInscricaoEstadual;
    @FXML private TextField txtInscricaoMunicipal;
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
    @FXML private TextField txtSite;
    
    @FXML private Label lblNomeFantasia;
    @FXML private Label lblDocumento;
    @FXML private Label lblRgIe;
    @FXML private Label lblInscMunicipal;

    private ToggleGroup tipoGrupo;
    private EmpresaDAO empresaDAO;
    private CategoriaDAO categoriaDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        empresaDAO = new EmpresaDAO();
        categoriaDAO = new CategoriaDAO();
        
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

    private void atualizarLabels() {
        boolean isPessoaJuridica = rbPessoaJuridica.isSelected();
        
        lblDocumento.setText(isPessoaJuridica ? "* CNPJ:" : "* CPF:");
        txtCpfCnpj.setPromptText(isPessoaJuridica ? "00.000.000/0000-00" : "000.000.000-00");
        
        lblRgIe.setText(isPessoaJuridica ? "Inscrição Estadual:" : "RG:");
        txtRgInscricaoEstadual.setPromptText(isPessoaJuridica ? "Inscrição Estadual" : "RG");
        
        lblNomeFantasia.setText(isPessoaJuridica ? "Nome Fantasia:" : "Apelido:");
        
        // Mostrar/ocultar inscrição municipal
        lblInscMunicipal.setVisible(isPessoaJuridica);
        lblInscMunicipal.setManaged(isPessoaJuridica);
        txtInscricaoMunicipal.setVisible(isPessoaJuridica);
        txtInscricaoMunicipal.setManaged(isPessoaJuridica);
    }

    @FXML
    private void handleSalvar() {
        if (!validarCampos()) {
            return;
        }

        try {
            // Criar objeto Empresa
            Empresa empresa = new Empresa();
            empresa.setTipoPessoa(rbPessoaJuridica.isSelected() ? "J" : "F");
            empresa.setNomeRazaoSocial(txtNomeRazaoSocial.getText().trim());
            empresa.setNomeFantasia(txtNomeFantasia.getText().trim());
            empresa.setCpfCnpj(txtCpfCnpj.getText().replaceAll("[^0-9]", ""));
            empresa.setRgInscricaoEstadual(txtRgInscricaoEstadual.getText().trim());
            empresa.setInscricaoMunicipal(txtInscricaoMunicipal.getText().trim());
            empresa.setCep(txtCep.getText().replaceAll("[^0-9]", ""));
            empresa.setEndereco(txtEndereco.getText().trim());
            empresa.setNumero(txtNumero.getText().trim());
            empresa.setComplemento(txtComplemento.getText().trim());
            empresa.setBairro(txtBairro.getText().trim());
            empresa.setCidade(txtCidade.getText().trim());
            empresa.setEstado(cbEstado.getValue());
            empresa.setTelefone(txtTelefone.getText().replaceAll("[^0-9]", ""));
            empresa.setCelular(txtCelular.getText().replaceAll("[^0-9]", ""));
            empresa.setEmail(txtEmail.getText().trim());
            empresa.setSite(txtSite.getText().trim());
            empresa.setAtivo(true);

            // Inserir no banco
            Integer empresaId = empresaDAO.inserir(empresa);
            empresa.setId(empresaId);
            
            logger.info("Empresa cadastrada com ID: " + empresaId);

            // Criar categorias padrão
            criarCategoriaPadrao(empresaId);

            // Definir empresa na sessão
            SessionManager.getInstance().setEmpresaLogada(empresa);

            // Ir para o dashboard
            mostrarSucesso("Empresa cadastrada com sucesso!");
            ReciboApplication.carregarTela("/com/dataware/recibo/dashboard-view.fxml", 1200, 700);

        } catch (SQLException e) {
            logger.error("Erro ao cadastrar empresa", e);
            mostrarErro("Erro", "Não foi possível cadastrar a empresa.\n" + e.getMessage());
        } catch (IOException e) {
            logger.error("Erro ao carregar dashboard", e);
            mostrarErro("Erro", "Não foi possível carregar o sistema.");
        }
    }

    @FXML
    private void handleCancelar() {
        try {
            // Verificar se há empresas cadastradas
            List<Empresa> empresas = empresaDAO.listar();
            
            if (!empresas.isEmpty()) {
                // Há empresas, voltar para tela de login
                ReciboApplication.carregarTela("/com/dataware/recibo/login-view.fxml", 600, 400);
            } else {
                // Não há empresas, não pode cancelar
                mostrarErro("Atenção", "É necessário cadastrar pelo menos uma empresa para usar o sistema.");
            }
        } catch (SQLException e) {
            logger.error("Erro ao verificar empresas", e);
            mostrarErro("Erro", "Não foi possível verificar as empresas cadastradas.");
        } catch (IOException e) {
            logger.error("Erro ao voltar para login", e);
            mostrarErro("Erro", "Não foi possível voltar para o login.");
        }
    }

    private void criarCategoriaPadrao(Integer empresaId) throws SQLException {
        SessionManager.getInstance().setEmpresaLogada(new Empresa(empresaId, ""));
        
        String[][] categorias = {
            {"Prestação de Serviços", "Recibos referentes a prestação de serviços"},
            {"Aluguel", "Recibos de pagamento de aluguel"},
            {"Venda de Produtos", "Recibos de venda de produtos"},
            {"Consultoria", "Recibos de serviços de consultoria"},
            {"Manutenção", "Recibos de serviços de manutenção"},
            {"Comissão", "Recibos de pagamento de comissões"},
            {"Outros", "Outros tipos de recibos"}
        };

        for (String[] cat : categorias) {
            Categoria categoria = new Categoria();
            categoria.setNome(cat[0]);
            categoria.setDescricao(cat[1]);
            categoriaDAO.inserir(categoria);
        }
        
        logger.info("Categorias padrão criadas para empresa ID: " + empresaId);
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


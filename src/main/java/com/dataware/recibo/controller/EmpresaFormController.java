package com.dataware.recibo.controller;

import com.dataware.recibo.ReciboApplication;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class EmpresaFormController {
    private static final Logger logger = LoggerFactory.getLogger(EmpresaFormController.class);

    @FXML
    private void handleVoltar() {
        try {
            ReciboApplication.carregarTela("/com/dataware/recibo/login-view.fxml", 600, 400);
        } catch (IOException e) {
            logger.error("Erro ao voltar", e);
        }
    }
}




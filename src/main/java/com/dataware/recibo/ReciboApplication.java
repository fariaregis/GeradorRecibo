package com.dataware.recibo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ReciboApplication extends Application {
    private static final Logger log = LoggerFactory.getLogger(ReciboApplication.class);
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setTitle("Sistema de Recibos");
        
        // Configuração inicial da janela
        primaryStage.setMaximized(true);
        
        // Tela inicial de login
        carregarTela("/com/dataware/recibo/login-view.fxml", 600, 400);
        
        primaryStage.show();
        log.info("Aplicativo iniciado com sucesso");
    }

    // Método auxiliar para carregar telas
    public static void carregarTela(String fxmlPath, double width, double height) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(ReciboApplication.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            log.error("Erro ao carregar tela: " + fxmlPath, e);
            throw e;
        }
    }

    // Retorna a janela principal
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}


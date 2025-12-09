module com.dataware.recibo {
    // Módulos JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;

    // Módulos Java padrão
    requires java.sql;
    requires java.desktop;
    
    // Logging (SLF4J)
    requires org.slf4j;
    
    // PDF (iText) - usando nomes automáticos
    requires kernel;
    requires layout;
    requires io;

    // Abre pacotes para o JavaFX usar reflexão
    opens com.dataware.recibo to javafx.fxml;
    opens com.dataware.recibo.controller to javafx.fxml;
    opens com.dataware.recibo.model to javafx.base;
    
    // Exporta pacotes públicos
    exports com.dataware.recibo;
    exports com.dataware.recibo.controller;
    exports com.dataware.recibo.model;
}
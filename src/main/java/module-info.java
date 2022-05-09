module com.example.ap {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires html2pdf;
    requires com.j2html;
    requires org.junit.jupiter.api;

    opens com.cashcash.ap to javafx.fxml;
    exports com.cashcash.ap;
}
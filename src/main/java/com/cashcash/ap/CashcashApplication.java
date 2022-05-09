package com.cashcash.ap;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Classe correspondant au point d'entrée de l'application. Elle charge le fichier FXML ainsi que le fichier CSS
 * et permet de définir les paramètres de la fenêtre principale de l'application.
 *
 * @author Nidal
 * @version 1.0
 * @since 1.0
 */
public class CashcashApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("client-view.fxml")));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("style.css")).toExternalForm());
        stage.setTitle("Cashcash - Application Métier");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    /**
     * Cette méthode permet d'invoquer une Alert. Cela permet à un employé d'avoir une confirmation de la réussite (ou
     * de l'échec) de ses actions.
     *
     * @param titre Correspond au titre de la fenêtre Alert
     * @param msg Correspond au message à indiquer à l'utiisateur
     * @param alertType Correspond au type d'Alert à créer (INFORMATION, CONFIRMATION, WARNING, ERROR)
     */
    public static void alert(String titre, String msg, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(titre);

        // Header Text: null
        alert.setHeaderText(null);
        alert.setContentText(msg);

        alert.showAndWait();
    }
}
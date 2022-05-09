package com.cashcash.ap;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Cette classe représente le contrôleur de la fenêtre create-contrat-view.fxml. Le contrôleur n'étant pas indiqué dans
 * le fichier FXML, les éléments de la page sont configurés ici (ComboBox typeContrat & Button btnCreateContrat).
 * Le contrôleur n'est pas indiqué dans le FXML car nous avons besoin de passer le client précédemment chargé
 * au constructeur de cette classe. Cela doit obligatoirement se faire manuellement.
 *
 * @author Nidal
 * @version 1.0
 * @since 1.0
 */
public class CreateContratViewController {

    @FXML
    private ComboBox<String> typeContrat;
    @FXML
    private Button btnCreateContrat;

    private final PersistanceSQL sql;
    private final Client client;

    /**
     * Cette méthode s'exécute automatiquement après l'exécution du constructeur. Elle permet de paramétrer les éléments
     * de la page
     *
     * @exception SQLException Échec de la requête ou résultat vide
     */
    @FXML
    public void initialize() {
       try {

           // Création et préparation du statement pour obj TypeContrat
           Connection con = sql.getCon();
           String query = "SELECT * FROM typecontrat;";
           PreparedStatement stmt = con.prepareStatement(query);
           stmt.execute();
           ResultSet rs = stmt.executeQuery();

           // Itération du résultat de la requête et ajout des TypeContrat dans la ComboBox
           while(rs.next()) {
               TypeContrat tc = (TypeContrat) sql.chargerDepuisBase(rs.getString("RefTypeContrat"), "TypeContrat");
               typeContrat.getItems().add(tc.getRefTypeContrat());
               System.out.println(tc.getRefTypeContrat());
           }
           typeContrat.getSelectionModel().selectFirst();

           // Définition de la méthode à appeler avec le Button btnCreateContrat
           btnCreateContrat.setOnAction(actionEvent -> {
               try {
                   creerContratCli(actionEvent);
               } catch (SQLException e) {
                   System.err.println("Erreur lors de la création du contrat du client");
                   e.printStackTrace();
               }
           });

       } catch(SQLException e) {
           System.err.println("Impossible de récupérer les informations depuis la table TypeContrat");
           e.printStackTrace();
       }
    }

    /**
     * Le constructeur prend un paramètre Client qui permet de charger le client de la page précédente.
     * Il prend également un paramètre PersistanceSQL pour réutiliser les identifiants SQL de la page précédente.
     *
     * @param cli correspond à l'objet Client chargé dans la page principale
     * @param sql correspond à l'objet PersistanceSQL utilisé par la page principale
     */
    public CreateContratViewController(Client cli, PersistanceSQL sql) {
        this.sql = sql;
        this.client = cli;
    }

    /**
     * Cette méthode permet de créer un contrat pour le client sélectionné. Le type de contrat et choisi grâce à la
     * ComboBox.
     *
     * @param event pour signaler que la méthode est appelée par un bouton
     * @throws SQLException Provient d'un problème pour charger le TypeContrat depuis la BDD ou d'un problème d'insertion
     */
    public void creerContratCli(ActionEvent event) throws SQLException {

        TypeContrat tc = (TypeContrat) sql.chargerDepuisBase(typeContrat.getValue(), "TypeContrat");
        ContratDeMaintenance c = new ContratDeMaintenance("1", LocalDate.now(), tc, client.getNumClient());
        sql.rangerDansBase(c);

    }
}

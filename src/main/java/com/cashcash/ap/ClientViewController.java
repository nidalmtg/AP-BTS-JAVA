package com.cashcash.ap;

import com.itextpdf.html2pdf.HtmlConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import static j2html.TagCreator.*;

public class ClientViewController {
    private final PersistanceSQL sql;
    private Client client;
    @FXML
    private Button btnXml, btnNewContrat, btnRelance, btnAjtAuContrat;
    @FXML
    private Label dateVente, dateInst, prixVente, emplacement, codeTypeMat;
    @FXML
    private Label dateVenteHC, dateInstHC, prixVenteHC, emplacementHC, codeTypeMatHC;
    @FXML
    private Label numContrat, dateSignature, dateEcheance, typeContrat, nbJoursRestant;
    @FXML
    private TextField matriculeClient;
    @FXML
    private Label numCli, nomCli, raisonSociale, siren, codeApe, adresse, tel, email;
    @FXML
    private ComboBox<String> matSousContrat, matHorsContrat;

    public ClientViewController() {
        sql = new PersistanceSQL("localhost", "ap", 3307);
    }

    /**
     * Cette méthode permet de rechercher un client de la base de données. L'objet Client est passé à l'attribut du
     * contrôleur et est chargé avec son contrat (s'il existe) ainsi que ses matériels. La méthode gère également
     * l'affichage des données du client
     *
     * @param event Pour signaler que la méthode est appelée par un bouton
     * @throws SQLException Apparaît si un résultat est vide ou la requête connaît un problème
     */
    @FXML
    protected void rechercheCli(ActionEvent event) {

        try {

            this.client = (Client) sql.chargerDepuisBase(matriculeClient.getText(), "Client");

            if(this.client == null) {
                String msg = "Impossible de trouver le client numéro "+matriculeClient.getText();
                CashcashApplication.alert("Recherche client", msg, Alert.AlertType.WARNING);
                throw new NullPointerException(msg);
            }

            // Affichage infos client
            numCli.setText("Matricule du client : "+client.getNumClient());
            nomCli.setText("Nom du client : "+client.getNomClient());
            raisonSociale.setText("Raison sociale du client : "+client.getRaisonSociale());
            siren.setText("Siren : "+client.getSiren());
            codeApe.setText("Code Ape du client : "+client.getCodeApe());
            adresse.setText("Adresse du client : "+client.getAdresse());
            tel.setText("Téléphone du client : "+client.getTel());
            email.setText("Email du client : "+client.getEmail());

            // Affichage contrat
            if(client.getLeContrat() != null) {

                numContrat.setText("Numéro du contrat : "+client.getLeContrat().getNumContrat());
                dateSignature.setText("Date de signature : "+client.getLeContrat().getDateSignature().toString());
                dateSignature.setVisible(true);
                dateEcheance.setText("Date d'échéance : "+client.getLeContrat().getDateEcheance().toString());
                dateEcheance.setVisible(true);
                typeContrat.setText("Type de contrat : "+client.getLeContrat().getTypeContrat().getRefTypeContrat());
                typeContrat.setVisible(true);

                if(client.getLeContrat().calcNbJoursRestants() < 1) {
                    nbJoursRestant.setText("Expiré depuis "+Math.abs(client.getLeContrat().calcNbJoursRestants())+" jours");
                    nbJoursRestant.setStyle("-fx-text-fill: #dc0b0b");
                    nbJoursRestant.setVisible(true);
                    btnNewContrat.setVisible(false);
                    btnRelance.setVisible(true);
                }
                else {
                    nbJoursRestant.setText("Jours restant avant expiration : "+client.getLeContrat().calcNbJoursRestants());
                    nbJoursRestant.setStyle("-fx-text-fill: #3fdc0b");
                    nbJoursRestant.setVisible(true);
                    btnNewContrat.setVisible(false);
                    btnRelance.setVisible(false);
                }
            }
            else {
                numContrat.setText("Aucun contrat souscris");
                dateSignature.setVisible(false);
                dateEcheance.setVisible(false);
                typeContrat.setVisible(false);
                nbJoursRestant.setVisible(false);

                btnNewContrat.setVisible(true);
                btnRelance.setVisible(false);
            }

            // Chargement & Affichage matériel sous contrat
            ArrayList<Materiel> x = client.getMaterielSousContrat();
            matSousContrat.getItems().clear();
            for(Materiel m : x) {
                matSousContrat.getItems().add(m.getNumSerie());
            }

            // Chargement & Affichage matériel hors contrat
            Connection con = sql.getCon();
            String query = "SELECT *, DATEDIFF(NOW(), `contratdemaintenance`.`DateSignature`)" +
                    "FROM `materiel`" +
                    "LEFT JOIN `contratdemaintenance` ON `materiel`.`NumContrat` = `contratdemaintenance`.`NumContrat`"+
                    "WHERE `materiel`.`NumClient` = ? AND `materiel`.`NumContrat` IS null ";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, client.getNumClient());
            stmt.execute();
            ResultSet rs = stmt.executeQuery();

            matHorsContrat.getItems().clear();
            while (rs.next()) {
                Materiel m = (Materiel) sql.chargerDepuisBase(rs.getString("NumSerie"), "Materiel");
                matHorsContrat.getItems().add(m.getNumSerie());
            }

            dateVente.setText("");
            dateInst.setText("");
            prixVente.setText("");
            emplacement.setText("");
            codeTypeMat.setText("");
            dateVenteHC.setText("");
            dateInstHC.setText("");
            prixVenteHC.setText("");
            emplacementHC.setText("");
            codeTypeMatHC.setText("");

            btnAjtAuContrat.setVisible(false);
            btnXml.setVisible(true);

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Cette méthode permet d'afficher dynamiquement les informations sur un Matériel sous contrat. La sélection du
     * matériel à afficher se fait grâce à la ComboBox.
     *
     * @param event Pour signaler que la méthode est appelée par un bouton
     */
    @FXML
    protected void getMaterielSousContratInfo(ActionEvent event) {

        Materiel m = (Materiel) sql.chargerDepuisBase(matSousContrat.getValue(), "Materiel");
        dateVente.setText("Date de vente : "+m.getDateVente().toString());
        dateInst.setText("Date d'installation : "+m.getDateVente().toString());
        prixVente.setText("Prix de vente : "+ m.getPrixVente());
        emplacement.setText("Emplacement : "+m.getEmplacement());
        codeTypeMat.setText("Code Type Materiel : "+m.getType().getLibelle());

    }
    /**
     * Cette méthode permet d'afficher dynamiquement les informations sur un Matériel hors contrat. La sélection du
     * matériel à afficher se fait grâce à la ComboBox.
     *
     * @param event Pour signaler que la méthode est appelée par un bouton
     */
    @FXML
    protected void getMaterielHorsContratInfo(ActionEvent event) {

        Materiel m = (Materiel) sql.chargerDepuisBase(matHorsContrat.getValue(), "Materiel");
        dateVenteHC.setText("Date de vente : "+m.getDateVente().toString());
        dateInstHC.setText("Date d'installation : "+m.getDateVente().toString());
        prixVenteHC.setText("Prix de vente : "+ m.getPrixVente());
        emplacementHC.setText("Emplacement : "+m.getEmplacement());
        codeTypeMatHC.setText("Code Type Materiel : "+m.getType().getLibelle());

        btnAjtAuContrat.setVisible(client.getLeContrat() != null && client.getLeContrat().calcNbJoursRestants() > 0 && matHorsContrat.getItems().size() >= 1);

    }

    /**
     * Cette méthode permet de générer un fichier XML correspondant à l'ensemble des matériels pour le client chargé.
     * Elle se base sur la méthode générerXml() de la classe Materiel pour les nœuds <materiel>. Cette méthode-ci
     * permet de rassembler tous les matériels d'un client et de vérifier s'ils sont sous contrat ou hors contrat.
     *
     * @param event Pour signaler que la méthode est appelée par un bouton
     *
     * @throws ParserConfigurationException Apparaît si la méthode rencontre un problème lors de la création du fichier.
     * @throws TransformerException Apparaît si la méthode rencontre un problème lors de la conversion DOM vers XML.
     * @throws SQLException Apparaît s'il existe un problème avec la requête SQL ou si un résultat est vide.
     */
    @FXML
    protected void genererXml(ActionEvent event) {

        try {
            Connection con = sql.getCon();

            // Création et préparation du statement
            String query = "SELECT `NumSerie`, `NumContrat` FROM `materiel` WHERE `NumClient` = ? AND `NumContrat` IS null;";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, client.getNumClient());
            stmt.execute();

            // Exécution du statement
            ResultSet rs = stmt.executeQuery();

            // Création document
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            // root element
            Element root = document.createElement("listeMateriel");
            document.appendChild(root);

            // set an attribute to root element
            Attr attr = document.createAttribute("idClient");
            attr.setValue(client.getNumClient());
            root.setAttributeNode(attr);

            // sous contrat element
            Element sousContrat = document.createElement("sousContrat");
            root.appendChild(sousContrat);

            // get liste mat sous contrat
            ArrayList<Materiel> matSousContrat = client.getMaterielSousContrat();

            for(Materiel m : matSousContrat) {
                Node firstDocImportedNode = document.importNode(m.xmlMateriel(), true);
                sousContrat.appendChild(firstDocImportedNode);
            }

            // hors contrat element
            Element horsContrat = document.createElement("horsContrat");
            root.appendChild(horsContrat);

            // Itération prochain résultat
            while (rs.next()) {
                Materiel mat = (Materiel) sql.chargerDepuisBase(rs.getString("NumSerie"), "Materiel");
                Node firstDocImportedNode = document.importNode(mat.xmlMateriel(), true);
                horsContrat.appendChild(firstDocImportedNode);
            }

            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File("materielClient-"+client.getNumClient()+".xml"));

            transformer.transform(domSource, streamResult);

            CashcashApplication.alert("Génération XML", "Le fichier a correctement été généré !", Alert.AlertType.INFORMATION);

        } catch (ParserConfigurationException | TransformerException e) {
            System.err.println("Erreur rencontrée lors de la création du document");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Erreur SQL. Peut provenir d'une requête ou d'un résultat vide");
            e.printStackTrace();
        }
    }

    /**
     * Cette méthode permet de créer une nouvelle fenêtre pour créer un contrat au client
     *
     * @param event Pour signaler que la méthode est appelée par un bouton
     * @throws IOException Apparaît s'il y a un problème lors du chargement du fichier FXML
     */
    @FXML
    protected void vueCreerContratCli(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("create-contrat-view.fxml"));
        CreateContratViewController controller = new CreateContratViewController(client, sql);
        loader.setController(controller);
        Scene secondScene = new Scene(loader.load());
        Stage newWindow = new Stage();
        newWindow.setTitle("Cashcash - Créer un contrat");
        newWindow.setScene(secondScene);
        newWindow.show();

    }

    /**
     * Cette méthode permet de créer un courrier de relance au cas où le contrat du client est expiré
     *
     * @param event Pour signaler que la méthode est appelée par un bouton
     * @throws IOException Apparaît s'il y a un problème lors de la création du fichier PDF
     */
    @FXML
    protected void genererRelance(ActionEvent event) {
        try {
            // Génération HTML
            String html;
            html = body(
                        p("CashCash"),
                        p("1 rue des SLAM"),
                        p("+339 20 70 80 90"),
                        br(),
                        p(client.getNomClient()).withStyle("text-align: right"),
                        p(client.getRaisonSociale()).withStyle("text-align: right"),
                        p(client.getAdresse()).withStyle("text-align: right"),
                        br(),br(),
                        p("Objet : Relance - Contrat n°"+client.getLeContrat().getNumContrat()),
                        br(),
                        p("Lille, le "+ LocalDate.now()),
                        br(),
                        p("Madame, Monsieur,"),
                        br(),
                        p("Sauf erreur ou omission de notre part, le paiement pour le renouvellement du contrat n°"
                                +client.getLeContrat().getNumContrat()+" daté du "+client.getLeContrat().getDateEcheance()
                                +" n'a pas été effectué à ce jour. Ainsi, en cas d'incident, votre contrat de maintenance " +
                                "ne vous protège plus."),
                        br(),
                        p("Si vous souhaitez toujours bénéficier d'un contrat de maintenance pour protéger vos matériels, " +
                                "merci de procéder à un règlement dans les plus brefs délais."),
                        br(),
                        p("Veuillez agréer, Madame, Monsieur, l'expression de mes salutations distinguées")
                    ).toString();

            // Conversion String HTML vers fichier PDF
            HtmlConverter.convertToPdf(html, new FileOutputStream("relance-contrat-num-"
                                            +client.getLeContrat().getNumContrat()+".pdf"));

            CashcashApplication.alert("Génération de la relance",
                    "Le courrier de relance a correctement été généré !",
                    Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cette méthode permet de couvrir un matériel hors contrat via le contrat du client (si le client possède un contrat)
     *
     * @param event Pour signifier que la méthode est appelée par un bouton
     *
     * @throws SQLException Si la requête SQL rencontre un problème
     */
    @FXML
    protected void ajouterAuContrat(ActionEvent event) {

        try {
            Connection con = sql.getCon();

            // Création et préparation du statement
            String sql = "UPDATE `materiel` SET `NumContrat` = ? WHERE `NumSerie` = ?;";
            PreparedStatement stmt = con.prepareStatement(sql);
            System.out.println(client.getLeContrat().getNumContrat());
            stmt.setString(1, client.getLeContrat().getNumContrat());
            stmt.setString(2, matHorsContrat.getValue());
            stmt.executeUpdate();

            CashcashApplication.alert("Ajout du matériel au contrat",
                    "Le matériel est dorénavant couvert par le contrat du client dont la référence est : "
                            +client.getLeContrat().getNumContrat(),
                    Alert.AlertType.INFORMATION);

        } catch(SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

    }
}

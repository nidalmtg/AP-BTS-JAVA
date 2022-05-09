package com.cashcash.ap;

import javafx.scene.control.Alert;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static javafx.application.Platform.exit;

/**
 * Objet permettant de se connecter à la base de données et d'effectuer des opérations (SELECT, INSERT)
 *
 * @author Nidal
 * @version 1.0
 * @since 1.0
 */
public class PersistanceSQL {

    private String ip, nom;
    private int port;
    private Connection con;

    public String getIp() {
        return ip;
    }
    public String getNom() {
        return nom;
    }
    public int getPort() {
        return port;
    }
    public Connection getCon() {
        return con;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public void setPort(int port) {
        this.port = port;
    }

    public PersistanceSQL(String ip, String nom, int port) {

        try {
            this.ip = ip;
            this.nom = nom;
            this.port = port;
            this.con=DriverManager.getConnection( "jdbc:mysql://"+ this.ip +":"+ this.port +"/"+nom+"","root","");

        } catch (SQLException e) {
            String msg = "La connexion à la base de donnée a échouée";
            CashcashApplication.alert("Connexion BDD", msg, Alert.AlertType.ERROR);
            System.err.println(msg);
            e.printStackTrace();
            exit();
        }
    }

    /**
     * Permet d'effectuer une requête SELECT dans la BDD et retourne un objet du type
     * correspondant à la table où la requête est effectuée. La requête prend l'id recherché en premier
     * paramètre et le nom du type d'objet à retourner en second paramètre.
     *
     * @param id correspond à l'id à rechercher dans la base de donnée
     * @param nomClasse correspond au nom de la classe de l'objet à retourner
     *
     * @return le résultat de la requête "SELECT * FROM nomClasse WHERE PrimaryKey=id"
     *
     * @exception SQLException : Apparaît pour raisons diverses : Résultat vide, requête invalide...
     */
    public Object chargerDepuisBase(String id, String nomClasse) {

        try {
            switch (nomClasse) {
                case "Client" -> {

                    // Création et préparation du statement
                    String sql = "select * from "+nomClasse+" where NumClient=?";
                    PreparedStatement stmt = con.prepareStatement(sql);
                    stmt.setString(1, id);
                    stmt.execute();

                    // Exécution du statement
                    ResultSet rs = stmt.executeQuery();

                    // Itération prochain résultat
                    rs.next();

                    // Récupération des données dans des variables
                    String numClient =  rs.getString("NumClient");
                    String nomClient =  rs.getString("NomClient");
                    String raisonSociale =  rs.getString("RaisonSociale");
                    String siren =  rs.getString("Siren");
                    String codeApe =  rs.getString("CodeApe");
                    String adresse =  rs.getString("Adresse");
                    String mail =  rs.getString("MailClient");
                    String tel =  rs.getString("TelClient");

                    // Création et préparation du statement2
                    String sql2 = "select * from materiel where NumClient=?";
                    PreparedStatement stmt2 = con.prepareStatement(sql2);
                    stmt2.setString(1, id);
                    stmt2.execute();

                    // Exécution du statement
                    ResultSet rs2 = stmt2.executeQuery();
                    ArrayList<Materiel> listeMat = new ArrayList<>();

                    // Récupération Materiel du client dans arraylist
                    while(rs2.next()) {
                        listeMat.add((Materiel) chargerDepuisBase(rs2.getString("NumSerie"), "Materiel"));
                    }

                    // Création et préparation du statement3
                    sql2 = "select * from contratdemaintenance where NumClient=?";
                    stmt2 = con.prepareStatement(sql2);
                    stmt2.setString(1, id);
                    stmt2.execute();

                    // Exécution du statement
                    rs2 = stmt2.executeQuery();

                    ContratDeMaintenance leContrat;
                    if (rs2.next()) {
                        leContrat = (ContratDeMaintenance) chargerDepuisBase(rs2.getString("NumContrat"), "ContratDeMaintenance");
                    }
                    else {
                        leContrat = null;
                    }

                    // Création de l'objet Client
                    Client cli = new Client(numClient, nomClient, raisonSociale, siren, codeApe, adresse, tel, mail, listeMat, leContrat);

                    return cli;
                }

                case "Materiel" -> {
                    // Création et préparation du statement
                    String sql = "select * from "+nomClasse+" where NumSerie=?";
                    PreparedStatement stmt = con.prepareStatement(sql);
                    stmt.setString(1, id);
                    stmt.execute();

                    // Exécution du statement
                    ResultSet rs = stmt.executeQuery();
                    rs.next();

                    // Création et préparation du statement pour obj TypeMat
                    String sql2 = "SELECT * FROM typemateriel WHERE CodeTypeMat=?";
                    PreparedStatement stmt2 = con.prepareStatement(sql2);
                    stmt2.setString(1, rs.getString("CodeTypeMat"));
                    stmt2.execute();
                    ResultSet rs2 = stmt2.executeQuery();

                    // Itération prochain résultat
                    rs2.next();

                    // Récupération des données dans des variables
                    String numSerie =  rs.getString("NumSerie");
                    String numContrat =  rs.getString("NumContrat");
                    System.out.println(numContrat);
                    LocalDate dateVente =  LocalDate.parse(rs.getString("DateVente"));
                    LocalDate dateInst =  LocalDate.parse(rs.getString("DateInst"));
                    double prixVente =  rs.getDouble("PrixVente");
                    String emplacement =  rs.getString("Emplacement");
                    TypeMateriel codeTypeMat =  new TypeMateriel(rs2.getString("CodeTypeMat"),rs2.getString("Libelle"));

                    if (numContrat != null) {
                        ContratDeMaintenance c = (ContratDeMaintenance) chargerDepuisBase(rs.getString("NumContrat"),"ContratDeMaintenance");
                        // Création de l'objet Materiel
                        return new Materiel(numSerie, dateVente, dateInst, prixVente, emplacement, codeTypeMat, c);
                    }
                    else {
                        ContratDeMaintenance c = null;
                        // Création de l'objet Materiel
                        return new Materiel(numSerie, dateVente, dateInst, prixVente, emplacement, codeTypeMat, c);
                    }

                }

                case "ContratDeMaintenance" -> {
                    // Création et préparation du statement
                    String sql = "select * from "+nomClasse+" where NumContrat=?";
                    PreparedStatement stmt = con.prepareStatement(sql);
                    stmt.setString(1, id);
                    stmt.execute();

                    // Exécution du statement
                    ResultSet rs = stmt.executeQuery();
                    rs.next();

                    // Création et préparation du statement pour obj TypeContrat
                    String sql2 = "SELECT * FROM typecontrat WHERE RefTypeContrat=?";
                    PreparedStatement stmt2 = con.prepareStatement(sql2);
                    stmt2.setString(1, rs.getString("RefTypeContrat"));
                    stmt2.execute();
                    ResultSet rs2 = stmt2.executeQuery();

                    // Itération prochain résultat
                    rs2.next();

                    // Récupération des données dans des variables
                    String numContrat =  rs.getString("NumContrat");
                    LocalDate dateSignature = LocalDate.parse(rs.getString("DateSignature"));
                    TypeContrat typeContrat =  new TypeContrat(rs2.getString("RefTypeContrat"),rs2.getDouble("TauxApplicable"), LocalTime.parse(rs2.getString("DelaiIntervention")));
                    String numCli =  rs.getString("NumClient");

                    // Création de l'objet Materiel
                    return new ContratDeMaintenance(numContrat, dateSignature, typeContrat, numCli);
                }

                case "TypeContrat" -> {

                    // Création et préparation du statement pour obj TypeContrat
                    String sql = "SELECT * FROM typecontrat WHERE RefTypeContrat=?";
                    PreparedStatement stmt = con.prepareStatement(sql);
                    stmt.setString(1, id);
                    stmt.execute();
                    ResultSet rs = stmt.executeQuery();

                    // Itération prochain résultat
                    rs.next();

                    // Récupération des données dans des variables
                    return new TypeContrat(rs.getString("RefTypeContrat"),rs.getDouble("TauxApplicable"), LocalTime.parse(rs.getString("DelaiIntervention")));
                }
            }
        }
        catch (SQLException e) {
            System.err.println("Erreur SQL");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Permet d'effectuer une requête INSERT dans la BDD avec un objet passé en paramètre.
     *
     * @param obj correspond à l'objet à insérer dans la base de donnée
     *
     * @throws SQLException : Apparaît pour raisons diverses : Résultat vide, requête invalide...
     */
    public void rangerDansBase(Object obj) {

        try {
            switch(obj.getClass().getSimpleName()) {

                case "ContratDeMaintenance" -> {

                    obj = (ContratDeMaintenance) obj;

                    String sql = "INSERT INTO contratdemaintenance (`NumContrat`, `DateSignature`, `RefTypeContrat`, `NumClient`) VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt = con.prepareStatement(sql);
                    stmt.setString(1, ((ContratDeMaintenance) obj).getNumClient());
                    stmt.setString(2, (((ContratDeMaintenance) obj).getDateSignature().toString()));
                    stmt.setString(3, ((ContratDeMaintenance) obj).getTypeContrat().getRefTypeContrat());
                    stmt.setString(4, ((ContratDeMaintenance) obj).getNumClient());
                    stmt.executeUpdate();
                    con.close();

                }

                default -> throw new IllegalStateException("Unexpected value: " + obj.getClass());
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL");
            e.printStackTrace();
        }
    }

    /**
     * A DOCUMENTER
     */
    public int loginEmp(String email, String password) throws SQLException {

        try {
            // Création et préparation du statement
            String sql = "select Matricule, MailEmp, MdpEmp from employe where MailEmp=? and MdpEmp=MD5(?);";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.execute();

            // Exécution du statement
            ResultSet rs = stmt.executeQuery();
            if(rs.next())
                return 1;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
        return 0;
    }

}

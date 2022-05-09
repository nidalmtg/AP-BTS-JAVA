package com.cashcash.ap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Objet représentant la table Client
 *
 * @author Nidal
 * @version 1.0
 * @since 1.0
 */
public class Client {

    private String numClient, nomClient, raisonSociale, siren, codeApe, adresse, tel, email;
    private ArrayList<Materiel> lesMateriels;
    private ContratDeMaintenance leContrat;

    public String getNumClient() {
        return numClient;
    }

    public String getNomClient() {
        return nomClient;
    }

    public String getRaisonSociale() {
        return raisonSociale;
    }

    public String getSiren() {
        return siren;
    }

    public String getCodeApe() {
        return codeApe;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getTel() {
        return tel;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<Materiel> getLesMateriels() {
        return lesMateriels;
    }

    public ContratDeMaintenance getLeContrat() {
        return leContrat;
    }

    public void setNumClient(String numClient) {
        this.numClient = numClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public void setRaisonSociale(String raisonSociale) {
        this.raisonSociale = raisonSociale;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public void setCodeApe(String codeApe) {
        this.codeApe = codeApe;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLesMateriels(ArrayList<Materiel> lesMateriels) {
        this.lesMateriels = lesMateriels;
    }

    public void setLeContrat(ContratDeMaintenance leContrat) {
        this.leContrat = leContrat;
    }

    public Client() {}

    public Client(String numClient, String nomClient, String raisonSociale, String siren, String codeApe, String adresse,
                  String tel, String email, ArrayList<Materiel> lesMateriels, ContratDeMaintenance leContrat)
    {
        this.numClient = numClient;
        this.nomClient = nomClient;
        this.raisonSociale = raisonSociale;
        this.siren = siren;
        this.codeApe = codeApe;
        this.adresse = adresse;
        this.tel = tel;
        this.email = email;
        this.lesMateriels = lesMateriels;
        this.leContrat = leContrat;
    }

    /**
     * Cette méthode permet de retourner une ArrayList de Materiel correspondant à l'ensemble
     * des matériels couverts par un contrat de maintenance du client
     *
     * @return la liste des matériels du client couverts par son contrat de maintenance
     *
     * @exception SQLException : Apparaît pour raisons diverses : Résultat vide, requête invalide...
     */
    public ArrayList<Materiel> getMaterielSousContrat() {

        try {
            ArrayList<Materiel> matSousContrat = new ArrayList<>();
            PersistanceSQL pSql = new PersistanceSQL("localhost","ap",3307);
            Connection con = pSql.getCon();

            // Requête sql
            String sql = "SELECT `materiel`.*, `contratdemaintenance`.`RefTypeContrat`, `contratdemaintenance`.`DateSignature`" +
                    "FROM `materiel`" +
                    "LEFT JOIN `contratdemaintenance` ON `materiel`.`NumContrat` = `contratdemaintenance`.`NumContrat`" +
                    "WHERE `materiel`.`NumClient` = ? AND `materiel`.`NumContrat` IS NOT NULL;";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1,numClient);
            stmt.execute();
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                String numSerie = rs.getString("NumSerie");
                matSousContrat.add((Materiel) pSql.chargerDepuisBase(numSerie, "Materiel"));
            }

            return matSousContrat;

        } catch (SQLException e) {
            System.err.println("Erreur SQL");
            e.printStackTrace();
        }

        return null;
    }

}

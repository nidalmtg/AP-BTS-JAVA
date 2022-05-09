package com.cashcash.ap;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * Objet représentant la table ContratDeMaintenance
 *
 * @author Nidal
 * @version 1.0
 * @since 1.0
 */
public class ContratDeMaintenance {

    private String numContrat, numClient;
    private LocalDate dateSignature;
    private LocalDate dateEcheance;
    private TypeContrat typeContrat;
    private ArrayList<Materiel> materielsAssures;

    public String getNumContrat() {
        return numContrat;
    }

    public String getNumClient() {
        return numClient;
    }

    public LocalDate getDateSignature() {
        return dateSignature;
    }

    public LocalDate getDateEcheance() {
        return dateEcheance;
    }

    public TypeContrat getTypeContrat() {
        return typeContrat;
    }

    public ArrayList<Materiel> getMaterielsAssures() {
        return materielsAssures;
    }

    public void setNumContrat(String numContrat) {
        this.numContrat = numContrat;
    }

    public void setNumClient(String numClient) {
        this.numClient = numClient;
    }

    public void setDateSignature(LocalDate dateSignature) {
        this.dateSignature = dateSignature;
    }

    public void setDateEcheance(LocalDate dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public void setTypeContrat(TypeContrat typeContrat) {
        this.typeContrat = typeContrat;
    }

    public void setMaterielsAssures(ArrayList<Materiel> materielsAssures) {
        this.materielsAssures = materielsAssures;
    }

    public ContratDeMaintenance(String numContrat, LocalDate dateSignature, TypeContrat typeContrat, String numClient) {
        this.numContrat = numContrat;
        this.dateSignature = dateSignature;
        this.dateEcheance = dateSignature.plusYears(1);
        this.typeContrat = typeContrat;
        this.numClient = numClient;
        //this.materielsAssures = materielsAssures;
    }

    /**
     * Renvoie le nombre de jours avant que le contrat n'arrive à expiration, ou, le cas échéant, le nombre de jours
     * depuis lequel le contrat n'est plus valide
     *
     * @return Le nombre de jour avant l'échéance du contrat (peut retourner une valeur négative qui représente le nombre
     * de jour depuis lequel le contrat est expiré
     */
    public long calcNbJoursRestants() {
        return ChronoUnit.DAYS.between(LocalDate.now(), dateEcheance);
    }
}

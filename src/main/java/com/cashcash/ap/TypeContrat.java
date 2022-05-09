package com.cashcash.ap;

import java.time.LocalTime;

/**
 * Objet représentant la table TypeContrat
 *
 * @author Nidal
 * @version 1.0
 * @since 1.0
 */
public class TypeContrat {

    private String refTypeContrat;
    private double tauxApplicable;
    LocalTime delaiIntervention;

    public String getRefTypeContrat() {
        return refTypeContrat;
    }

    public double getTauxApplicable() {
        return tauxApplicable;
    }

    public LocalTime getDelaiIntervention() {
        return delaiIntervention;
    }

    public void setRefTypeContrat(String refTypeContrat) {
        this.refTypeContrat = refTypeContrat;
    }

    public void setTauxApplicable(double tauxApplicable) {
        this.tauxApplicable = tauxApplicable;
    }

    public void setDelaiIntervention(LocalTime delaiIntervention) {
        this.delaiIntervention = delaiIntervention;
    }

    public TypeContrat(String refTypeContrat, double tauxApplicable, LocalTime delaiIntervention) {
        this.refTypeContrat = refTypeContrat;
        this.tauxApplicable = tauxApplicable;
        this.delaiIntervention = delaiIntervention;
    }
}

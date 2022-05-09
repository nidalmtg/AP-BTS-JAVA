package com.cashcash.ap;

/**
 * Objet représentant la table TypeMateriel
 *
 * @author Nidal
 * @version 1.0
 * @since 1.0
 */
public class TypeMateriel {

    private String refInterne, libelle;

    public String getLibelle() {
        return libelle;
    }

    public String getRefInterne() {
        return refInterne;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public void setRefInterne(String refInterne) {
        this.refInterne = refInterne;
    }

    public TypeMateriel(String ref, String lib) {
        refInterne = ref;
        libelle = lib;
    }

}

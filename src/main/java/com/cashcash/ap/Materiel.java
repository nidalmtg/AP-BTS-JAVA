package com.cashcash.ap;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Objet représentant la table Materiel
 *
 * @author Nidal
 * @version 1.0
 * @since 1.0
 */
public class Materiel {

    private String numSerie, emplacement;
    private LocalDate dateVente, dateInstallation;
    private double prixVente;
    private TypeMateriel type;
    private ContratDeMaintenance contratMaintenance;


    public String getNumSerie() {
        return numSerie;
    }

    public LocalDate getDateVente() {
        return dateVente;
    }

    public LocalDate getDateInstallation() { return dateInstallation; }

    public String getEmplacement() {
        return emplacement;
    }

    public double getPrixVente() {
        return prixVente;
    }

    public TypeMateriel getType() {
        return type;
    }

    public ContratDeMaintenance getContratMaintenance() {
        return contratMaintenance;
    }

    public void setNumSerie(String numSerie) {
        this.numSerie = numSerie;
    }

    public void setDateVente(LocalDate dateVente) {
        this.dateVente = dateVente;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public void setPrixVente(double prixVente) {
        this.prixVente = prixVente;
    }

    public void setType(TypeMateriel type) {
        this.type = type;
    }

    public void setContratMaintenance(ContratDeMaintenance contratMaintenance) {
        this.contratMaintenance = contratMaintenance;
    }

    public Materiel(String numSerie) {
        this.numSerie = numSerie;
    }
    public Materiel(String numSerie, LocalDate dateVente, LocalDate dateInstallation, double prixVente,
                    String emplacement, TypeMateriel type, ContratDeMaintenance contratMaintenance) {
        this.numSerie = numSerie;
        this.dateVente = dateVente;
        this.dateInstallation = dateInstallation;
        this.prixVente = prixVente;
        this.emplacement = emplacement;
        this.type = type;
        this.contratMaintenance = contratMaintenance;
    }

    /**
     * Cette méthode permet de retourner un objet Node correspondant au code XML représentant le matériel
     *
     * @return un objet Node qui pourra être réinjecté dans un autre document
     *
     * @exception ParserConfigurationException : Apparaît si la création du document rencontre un problème
     */
    public Node xmlMateriel() {

        try {
            // Création document
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            // root element
            Element root = document.createElement("materiel");
            document.appendChild(root);

            // set an attribute to materiel element
            Attr attr = document.createAttribute("numSerie");
            attr.setValue(String.valueOf(getNumSerie()));
            root.setAttributeNode(attr);

            // type element
            Element type = document.createElement("type");
            attr = document.createAttribute("refInterne");
            attr.setValue(getType().getRefInterne());
            type.setAttributeNode(attr);
            attr = document.createAttribute("libelle");
            attr.setValue(getType().getLibelle());
            type.setAttributeNode(attr);
            root.appendChild(type);

            // date_vente elements
            Element dateVente = document.createElement("date_vente");
            dateVente.appendChild(document.createTextNode(getDateVente().toString()));
            root.appendChild(dateVente);

            // date_installation elements
            Element dateInstallation = document.createElement("date_installation");
            dateInstallation.appendChild(document.createTextNode(getDateInstallation().toString()));
            root.appendChild(dateInstallation);

            // prix_vente elements
            Element prixVente = document.createElement("prix_vente");
            prixVente.appendChild(document.createTextNode(String.valueOf(getPrixVente())));
            root.appendChild(prixVente);

            // emplacement elements
            Element emplacement = document.createElement("emplacement");
            emplacement.appendChild(document.createTextNode(getEmplacement()));
            root.appendChild(emplacement);

            if (!Objects.isNull(contratMaintenance)) {
                // nbJoursAvantEcheance elements
                Element nbJoursAvantEcheance = document.createElement("nbJoursAvantEcheance");
                nbJoursAvantEcheance.appendChild(document.createTextNode(String.valueOf(getContratMaintenance().calcNbJoursRestants())));
                root.appendChild(nbJoursAvantEcheance);
            }

            return document.getDocumentElement();

        } catch(ParserConfigurationException e) {
            System.err.println("Problème lors de la création du document");
            e.printStackTrace();
        }

        return null;
    }
}

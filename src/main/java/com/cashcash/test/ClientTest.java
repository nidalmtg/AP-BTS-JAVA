package com.cashcash.test;

import com.cashcash.ap.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientTest {

    Client client;

    @BeforeEach
    void setUp() {
        // Instanciation du client
        client = new Client();

        // Définition d'un contrat de maintenance pour le client
        ContratDeMaintenance contratCli = new ContratDeMaintenance(
                "NUM0",
                LocalDate.of(2021, 11, 27),
                new TypeContrat("REF_CONTRAT_0", 12.0, LocalTime.now()),
                "CLIENT_0"
        );

        // Création de matériels pour le client
        Materiel mat1 = new Materiel("NUM_SERIE_1");
        Materiel mat2 = new Materiel("NUM_SERIE_2");
        Materiel mat3 = new Materiel("NUM_SERIE_3");
        Materiel mat4 = new Materiel("NUM_SERIE_4");
        Materiel mat5 = new Materiel("NUM_SERIE_5");

        // Ajout des matériels 1, 2, 4 dans le contrat du client
        mat1.setContratMaintenance(contratCli);
        mat2.setContratMaintenance(contratCli);
        mat5.setContratMaintenance(contratCli);

        // Création d'une ArrayList de matériels pour le client avec ceux instanciés plus tôt
        ArrayList<Materiel> materiels = new ArrayList<>();
        materiels.add(mat1);
        materiels.add(mat2);
        materiels.add(mat3);
        materiels.add(mat4);
        materiels.add(mat5);
        client.setLesMateriels(materiels);
    }

    @Test
    @DisplayName("Le test doit retourner une ArrayList de Materiel avec " +
            "les numéros de série 1, 2 et 5")
    void testGetMatSousContrat() throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        // ArrayList à retrouver :
        ArrayList<Materiel> matAttendus = new ArrayList<>();
        matAttendus.add(new Materiel("NUM_SERIE_1"));
        matAttendus.add(new Materiel("NUM_SERIE_2"));
        matAttendus.add(new Materiel("NUM_SERIE_5"));

        assertEquals(matAttendus, client.getMaterielSousContrat(),
                "");
    }
}

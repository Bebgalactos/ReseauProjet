package fr.ul.miage.lutakhato;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {

    @ParameterizedTest(name = "{0} + {1} = {2}")
    @CsvSource({
            "1, 1, 2",
            "2, 2, 4",
            "49, 51, 100",
            "1, 100, 101"
    })
    public static void testAppend(int zero, int one, int two){

    }
    @org.junit.Test
    public void testDelAvecClesExistantes() {
        // Réinitialiser la Map database avant chaque test
        Server.database.clear();

        // Ajouter les clés à supprimer dans la Map database
        Server.database.put("key1", new ServerObject(0, -1, "valeur1"));
        Server.database.put("key2", new ServerObject(0, -1, "valeur2"));
        Server.database.put("key3", new ServerObject(0, -1, "valeur3"));

        // Appeler la méthode del avec les clés existantes
        int nbSuccess = Server.del(new String[]{"key1", "key2"});

        // Vérifier que les clés ont été supprimées de la Map database
        assertNull(Server.database.get("key1"));
        assertNull(Server.database.get("key2"));
        assertNotNull(Server.database.get("key3"));

        // Vérifier que le nombre de clés supprimées est correct
        assertEquals(2, nbSuccess);
    }

    @org.junit.Test
    public void testDelAvecClesInexistantes() {
        // Réinitialiser la Map database avant chaque test
        Server.database.clear();

        // Ajouter une clé existante dans la Map database
        Server.database.put("key1", new ServerObject(0, -1, "valeur1"));

        // Appeler la méthode del avec des clés inexistantes
        int nbSuccess = Server.del(new String[]{"key2", "key3"});

        // Vérifier que les clés inexistantes n'ont pas été supprimées de la Map database
        assertNotNull(Server.database.get("key1"));

        // Vérifier que le nombre de clés supprimées est correct
        assertEquals(0, nbSuccess);
    }

    @org.junit.Test
    public void testDelAvecCleExistanteEtCleInexistante() {
        // Réinitialiser la Map database avant chaque test
        Server.database.clear();

        // Ajouter une clé existante dans la Map database
        Server.database.put("key1", new ServerObject(0, -1, "valeur1"));

        // Appeler la méthode del avec une clé existante et une clé inexistante
        int nbSuccess = Server.del(new String[]{"key1", "key2"});

        // Vérifier que la clé existante a été supprimée de la Map database
        assertNull(Server.database.get("key1"));

        // Vérifier que le nombre de clés supprimées est correct
        assertEquals(1, nbSuccess);
    }











}

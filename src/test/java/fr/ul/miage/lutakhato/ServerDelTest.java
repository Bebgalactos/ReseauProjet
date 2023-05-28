package fr.ul.miage.lutakhato;

import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

import java.net.Socket;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerDelTest {

    @Test
    @CsvSource({
            "1, 1, 2",
            "2, 2, 4",
            "49, 51, 100",
            "1, 100, 101"
    })
    public void testDelAvecClesExistantes() {
        // Réinitialiser la Map database avant chaque test
        ServerThread server = new ServerThread(new Client(new Socket()));

        // Ajouter les clés à supprimer dans la Map database
        server.getDatabase().put("key1", new ServerObject(0, "valeur1"));
        server.getDatabase().put("key2", new ServerObject(0, "valeur2"));
        server.getDatabase().put("key3", new ServerObject(0, "valeur3"));

        // Appeler la méthode del avec les clés existantes
        int nbSuccess = server.del(new String[]{"key1", "key2"});

        // Vérifier que les clés ont été supprimées de la Map database
        assertAll(
                () -> assertNull(server.getDatabase().get("key1")),
                () -> assertNull(server.getDatabase().get("key2")),
                () -> assertNotNull(server.getDatabase().get("key3"))
        );

        // Vérifier que le nombre de clés supprimées est correct
        assertEquals(2, nbSuccess);
    }

    @Test
    public void testDelAvecClesInexistantes() {
        // Réinitialiser la Map database avant chaque test
        ServerThread server = new ServerThread(new Client(new Socket()));

        // Ajouter une clé existante dans la Map database
        server.getDatabase().put("key1", new ServerObject(0, "valeur1"));

        // Appeler la méthode del avec des clés inexistantes
        int nbSuccess = server.del(new String[]{"key2", "key3"});


        assertAll(
                // Vérifier que les clés inexistantes n'ont pas été supprimées de la Map database
                () -> assertNotNull(server.getDatabase().get("key1")),

                // Vérifier que le nombre de clés supprimées est correct
                () -> assertEquals(0, nbSuccess));
    }

    @Test
    public void testDelAvecCleExistanteEtCleInexistante() {
        // Réinitialiser la Map database avant chaque test
        ServerThread server = new ServerThread(new Client(new Socket()));

        // Ajouter une clé existante dans la Map database
        server.getDatabase().put("key1", new ServerObject(0, "valeur1"));

        // Appeler la méthode del avec une clé existante et une clé inexistante
        int nbSuccess = server.del(new String[]{"key1", "key2"});

        assertAll(
                // Vérifier que la clé existante a été supprimée de la Map database
                () -> assertNull(server.getDatabase().get("key1")),

                // Vérifier que le nombre de clés supprimées est correct
                () -> assertEquals(1, nbSuccess));
    }

    @Test
    public void testDelExpire() {
        // Réinitialiser la Map database avant chaque test
        ServerThread server = new ServerThread(new Client(new Socket()));

        // Ajouter une clé existante dans la Map database
        server.getDatabase().put("key1", new ServerObject(0, "valeur1"));

        // Appeler la méthode del avec une clé existante et une clé inexistante
        int nbSuccess = server.del(new String[]{"key1", "key2"});

        assertAll(
                // Vérifier que la clé existante a été supprimée de la Map database
                () -> assertNull(server.getDatabase().get("key1")),

                // Vérifier que le nombre de clés supprimées est correct
                () -> assertEquals(1, nbSuccess));
    }
}

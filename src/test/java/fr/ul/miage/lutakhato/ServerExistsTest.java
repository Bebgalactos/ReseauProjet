package fr.ul.miage.lutakhato;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;


public class ServerExistsTest {

    private ServerThread server;

    @BeforeEach
    public void setUp() {
        server = new ServerThread(client);
        server.getDatabase().put("key1", new ServerObject(0, "valeur1"));
        server.getDatabase().put("key2", new ServerObject(0, "valeur2"));
        server.getDatabase().put("key3", new ServerObject(0, "valeur3"));
    }

    @Test
    public void testExistsAvecClesExistantes() {
        // Appeler la méthode exists avec des clés existantes
        int nbSuccess = server.exists(new String[]{"key1", "key2"});

        // Vérifier que le nombre de clés existantes est correct
        Assertions.assertEquals(2, nbSuccess);
    }

    @Test
    public void testExistsAvecClesExistantesEtNonExistantes() {
        // Appeler la méthode exists avec des clés existantes et non existantes
        int nbSuccess = server.exists(new String[]{"key1", "key2", "key4", "key5"});

        // Vérifier que le nombre de clés existantes est correct
        Assertions.assertEquals(2, nbSuccess);
    }

    @Test
    public void testExistsAvecClesSansExpiration() {
        // Ajouter des clés sans expiration dans la base de données
        server.getDatabase().put("key1", new ServerObject(-1, "valeur1"));
        server.getDatabase().put("key2", new ServerObject(-1, "valeur2"));

        // Appeler la méthode exists avec les clés sans expiration
        int nbSuccess = server.exists(new String[]{"key1", "key2"});

        // Vérifier que le nombre de clés existantes est correct
        Assertions.assertEquals(2, nbSuccess);
    }





}


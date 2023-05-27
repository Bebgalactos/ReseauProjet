package fr.ul.miage.lutakhato;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

public class SeverDecrTest {

    @Test
    public void testDecr() {
        // Instance du server
        Server server = new Server();

        // Ajouter une clé à décrémenter dans la Map database
        server.set("key1", 0, new String[0]);

        // Appeler la méthode decr avec la clé existante
        int newValue = server.decr("key1");

        // Appeler la méthode decr avec une nouvelle clé
        int newValue2 = server.decr("key2");

        assertAll(
                // Vérifier que la valeur a été décrémentée dans la Map database
                () -> assertEquals(-1, newValue),
                () -> assertNotNull(server.getDatabase().get("key1")),
                () -> assertEquals(-1, server.getDatabase().get("key1").getValue()),

                // Vérifier que la nouvelle clé et sa valeur ont été ajoutées dans la Map database
                () -> assertEquals(0, newValue2),
                () -> assertNotNull(server.getDatabase().get("key2")),
                () -> assertEquals(0, server.getDatabase().get("key2").getValue())
        );
    }

}

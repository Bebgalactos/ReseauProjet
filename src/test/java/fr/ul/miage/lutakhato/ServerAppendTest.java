package fr.ul.miage.lutakhato;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

public class ServerAppendTest {

    @Test
    public void testAppend_ExistingKey() {
        // Instance du server
        ServerThread server = new ServerThread(client);

        // Ajouter une clé existante dans la Map database
        server.set("key1", "value1", new String[0]);

        // Appeler la méthode append avec la clé existante
        int length = server.append("key1", " appended");

        // Vérifier que la valeur a été correctement ajoutée à la clé existante dans la Map database
        assertAll(
                () -> assertEquals(15, length),
                () -> assertEquals("value1 appended", server.get("key1"))
        );
    }

    @Test
    public void testAppend_NewKey() {
        // Instance du server
        ServerThread server = new ServerThread(client);

        server.set("key1", "", new String[0]);

        // Appeler la méthode append avec une nouvelle clé
        int length = server.append("key1", "appended");

        // Vérifier que la nouvelle clé et sa valeur ont été ajoutées dans la Map database
        assertAll(
                () -> assertEquals(8, length),
                () -> assertNotNull(server.getDatabase().get("key1")),
                () -> assertEquals("appended", server.getDatabase().get("key1").getValue())
        );
    }

}

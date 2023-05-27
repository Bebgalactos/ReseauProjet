package fr.ul.miage.lutakhato;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

public class SeverDecrTest {

    @Test
    public void testDecr() {
        // Réinitialiser la Map database avant chaque test
        Server.getDatabase().clear();

        // Ajouter une clé à décrémenter dans la Map database
        Server.getDatabase().put("key1", new ServerObject(0, "valeur1"));

        // Appeler la méthode decr avec la clé existante
        int newValue = Server.decr("key1");

        // Vérifier que la valeur a été décrémentée dans la Map database
        assertEquals(-1, newValue);
        assertEquals(-1, Server.getDatabase().get("key1").getValue());

        // Appeler la méthode decr avec une nouvelle clé
        newValue = Server.decr("key2");

        // Vérifier que la nouvelle clé et sa valeur ont été ajoutées dans la Map database
        assertEquals(-1, newValue);
        assertNotNull(Server.getDatabase().get("key2"));
        assertEquals(-1, Server.getDatabase().get("key2").getValue());
    }

}

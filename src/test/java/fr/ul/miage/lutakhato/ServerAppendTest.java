package fr.ul.miage.lutakhato;

public class ServerAppendTest {
    @Test
    public void testAppend_ExistingKey() {
        // Réinitialiser la Map database avant chaque test
        Server.getDatabase().clear();

        // Ajouter une clé existante dans la Map database
        Server.getDatabase().put("key1", new ServerObject(0, "value1"));

        // Appeler la méthode append avec la clé existante
        int length = Server.append("key1", " appended");

        // Vérifier que la valeur a été correctement ajoutée à la clé existante dans la Map database
        assertEquals(17, length);
        assertEquals("value1 appended", Server.getDatabase().get("key1").getValue());
    }

    @Test
    public void testAppend_NewKey() {
        // Réinitialiser la Map database avant chaque test
        Server.getDatabase().clear();

        // Appeler la méthode append avec une nouvelle clé
        int length = Server.append("key1", "appended");

        // Vérifier que la nouvelle clé et sa valeur ont été ajoutées dans la Map database
        assertEquals(0, length);
        assertNotNull(Server.getDatabase().get("key1"));
        assertEquals("appended", Server.getDatabase().get("key1").getValue());
    }

}

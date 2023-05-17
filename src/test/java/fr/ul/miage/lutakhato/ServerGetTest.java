package fr.ul.miage.lutakhato;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServerGetTest {
    @Test
    public void testGet(){
        // Instance du serveur
        Server test = new Server();

        // Tests
        test.set("initial value", 115, new String[0]);
        assertAll(
                () -> assertEquals(115, test.get("initial value")),
                () -> assertNull(test.get("valeur initiale"))
        );
    }
}

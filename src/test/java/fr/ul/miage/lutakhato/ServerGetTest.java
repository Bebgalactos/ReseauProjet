package fr.ul.miage.lutakhato;

import org.junit.jupiter.api.Test;

import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class ServerGetTest {
    @Test
    public void testGet(){
        // Instance du serveur
        ServerThread test = new ServerThread(new Client(new Socket()));

        // Tests
        test.set("initial value - int", 115, new String[0]);
        test.set("initial value - str", "Valeur de test", new String[0]);
        assertAll(
                () -> assertEquals(115, test.get("initial value - int")),
                () -> assertEquals("Valeur de test", test.get("initial value - str")),
                () -> assertEquals("nil", test.get("valeur initiale"))
        );
    }
}

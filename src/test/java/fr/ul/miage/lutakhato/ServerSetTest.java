package fr.ul.miage.lutakhato;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class ServerSetTest {

    @ParameterizedTest(name = "{0}, {1}")
    @CsvSource({
            "ks001,simple",
            "ks002,System of a down",
            "ks003,/K-pop/\\",
            "ks004,àéùîäÄËÖ",
            "ks004,",
    })
    public void testSetString(String key, String value){
        // Instance du serveur
        ServerThread server = new ServerThread(new Client(new Socket()));

        //Tests
        server.set(key, value, new String[0]);
        assertAll(
                () -> assertTrue(server.getDatabase().containsKey(key)),
                () -> assertEquals(server.getDatabase().get(key).getValue(), value)
        );
    }

    @ParameterizedTest(name = "{0}, {1}")
    @CsvSource({
            "ki001, -2147483647",
            "ki003, 0",
            "ki004, 2147483647"
    })
    public void testSetInteger(String key, Integer value){
        // Instance du serveur
        ServerThread server = new ServerThread(new Client(new Socket()));

        // Variables
        String[] options = new String[]{};

        // Tests
        server.set(key, value, options);
        assertAll(
                () -> assertTrue(server.getDatabase().containsKey(key)),
                () -> assertEquals(server.getDatabase().get(key).getValue(), value)
        );
    }

    @Test
    public void testSetOverwrite(){
        // Instance du serveur
        ServerThread server = new ServerThread(new Client(new Socket()));

        // Variables
        String[] options = new String[]{};
        String key = "key";
        String value1 = "value1";
        String value2 = "value2";

        // Tests
        server.set(key, value1, options);
        server.set(key, value2, options);
        assertEquals(server.getDatabase().get(key).getValue(), value2);
    }
}

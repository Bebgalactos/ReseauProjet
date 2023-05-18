package fr.ul.miage.lutakhato;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
        Server server = new Server();

        // Variables
        String[] options = new String[]{};

        //Tests
        server.set(key, value, options);
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
        Server server = new Server();

        // Variables
        String[] options = new String[]{};

        // Tests
        server.set(key, value, options);
        assertAll(
                () -> assertTrue(server.getDatabase().containsKey(key)),
                () -> assertEquals(server.getDatabase().get(key).getValue(), value)
        );
    }
}

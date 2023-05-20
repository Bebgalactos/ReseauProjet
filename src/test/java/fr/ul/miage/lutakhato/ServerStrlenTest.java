package fr.ul.miage.lutakhato;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import fr.ul.miage.lutakhato.Server;

import static org.junit.jupiter.api.Assertions.*;

public class ServerStrlenTest {

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "straight",
            "saturday",
            "THE SUN",
            "library on shutdown",
            "i am a god",
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()-=_+[]{}|;':,.<>/?`~éàèêëïîôöüùûçÄÖÜßáíóúñ¿¡æøåÅÆØÑ"
    })
    public void testStrlen(String entry){
        Server server = new Server();
        String entryKey = entry + "key";

        server.set(entryKey, entry, new String[]{});
        int strlen = server.get(entryKey).toString().length();

        assertTrue(strlen == entry.length());
    }

}

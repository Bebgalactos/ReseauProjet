package fr.ul.miage.lutakhato;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static fr.ul.miage.lutakhato.Server.syntaxCheck;
import static org.junit.jupiter.api.Assertions.*;

public class ServerSyntaxCheckTest {

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "SET ki001 bande",
            "APPEND ki001 _passante",
            "GET ki002",
            "EXISTS ki001 ki002 key17 team17 trombone hurluberlu",
            "EXPIRE ki002 100",
            "EXPIRE ki002 -100",
            "INCR ki002",
            "DECR ki002",
            "SET ki002 1000",
            "SET ki002 -1000",
            "GET ki002",
            "DEL ki002",
            "DEL ksjhfbisevbofesief",
    })
    public void testSyntax(String entry){
        assertTrue(syntaxCheck(entry));
    }

}

package fr.ul.miage.lutakhato;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static fr.ul.miage.lutakhato.Server.syntaxCheck;
import static org.junit.jupiter.api.Assertions.*;

public class ServerSyntaxCheckTest {

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "append ki001 _passante",
            "APPEND ki001 toAppend",
            "GET ki002",
            "EXISTS ki001 ki002 key17 team17 trombone hurluberlu",
            "EXPIRE ki002 100",
            "EXPIRE ki002 -100",
            "EXPIRE ki002 -100 NX",
            "EXPIRE ki002 -100 XX",
            "EXPIRE ki002 -100 GR",
            "EXPIRE ki002 -100 LT",
            "EXPIRE ki002 -100 NX XX GR LT",
            "INCR ki002",
            "DECR ki002",
            "set ki001 bande",
            "SET ki002 1000",
            "SET ki002 -1000",
            "get ki002",
            "DEL ki002",
            "DEL ksjhfbisevbofesief 0",
    })
    public void testGoodSyntax(String entry){
        assertTrue(syntaxCheck(entry));
    }


    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "SET ki001 bande lmao",
            "GET ki002 asd",
            "EXPIRE ki002",
            "EXPIRE ki002 cheh",
            "GET ki002 aze",
            "EXPIRE ki002 1000 NX XX RT",
            "INCR ki002 azd", "DECR ki002 chehçamarcheplus", "APPEND ki001 toAppend options?AhEnFaitNon",
            "APPEND ki001",
            "SET lolThisIsNotANumberWhatchaGonnaDo",
            "APPEND", "DECR", "DEL", "EXISTS", "EXPIRE", "GET", "INCR", "SET"
    })
    public void testBadSyntax(String entry){
        assertFalse(syntaxCheck(entry));
    }

}

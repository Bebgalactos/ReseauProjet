package fr.ul.miage.lutakhato;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static fr.ul.miage.lutakhato.Server.syntaxCheck;
import static org.junit.jupiter.api.Assertions.*;

public class ServerSyntaxCheckTest {

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "set ki001 barrels",
            "set                ki001                barrels",
            "SET ki002 10000",
            "SET ki002 -1000",
            "SET ki002 10000 EX 10",
            "SET ki002 10000 PX 10000",
            "SET ki002 10000 NX",
            "SET ki002 10000 XX",
            "SET ki002 10000 EX 10 NX",
            "SET ki002 10000 EX 10 XX",
            "SET ki002 10000 PX 10000 NX",
            "SET ki002 10000 PX 10000 XX",
            "SET ki002 plouk EX 10 XX",
            "SET ki002 plouk PX 10000 NX",
            "SET ki002 plouk PX 10000 XX",
            "SET ki002 plouk EX 10",
            "SET ki002 plouk PX 10000",
            "SET ki002 plouk NX",
            "SET ki002 plouk XX",
            "SET ki002 plouk EX 10 NX",
            "SET ki002 plouk EX 10 XX",
            "SET ki002 plouk PX 10000 NX",
            "SET ki002 plouk PX 10000 XX",
            "set ki001 \"barrels with spaces between them\"",
            "set \"key with spaces\" barrels",
            "set \"key with spaces\" \"barrels with spaces between them\"",
            "set \"key with spaces\" \"\\\"cette chaine de caractères est entre guillemets\\\"\"",
            "set \"key with spaces\" \"cette \\\"chaine de caractères\\\" n'est pas entre guillemets\"",
            "set \"key with spaces\" \\",
    })
    public void testGoodSyntaxSet(String entry){
        assertTrue(syntaxCheck(entry));
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "SET ki001",
            "SET ki001 barrels spaced",
            "SET ki002 -1000 NX XX",
            "SET ki002 -1000 EX 10 PX 10000",
            "SET ki002 -1000 EX 10 EX 10",
            "SET ki002 -1000 PX 10000 PX 10000",
            "SET ki002 -1000 NX NX",
            "SET ki002 -1000 XX XX",
    })
    public void testBadSyntaxSet(String entry){
        assertFalse(syntaxCheck(entry));
    }

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
            "get ki002",
            "DEL ki002",
            "DEL ksjhfbisevbofesief 0",
    })
    public void testGoodSyntax(String entry){
        assertTrue(syntaxCheck(entry));
    }


    @ParameterizedTest(name = "{0}")
    @CsvSource({
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

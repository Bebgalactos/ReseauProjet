package fr.ul.miage.lutakhato;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ServerTest {

    @ParameterizedTest(name = "{0} + {1} = {2}")
    @CsvSource({
            "1, 1, 2",
            "2, 2, 4",
            "49, 51, 100",
            "1, 100, 101"
    })
    public static void testAppend(int zero, int one, int two){

    }
}

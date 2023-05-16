package fr.ul.miage.lutakhato;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ServerTest {
    @Test
    @ParameterizedTest(name = "{0} + {1} = {2}")
    @CsvSource({
            "1, 1, 2",
            "2, 2, 4",
            "49, 51, 100",
            "1, 100, 101"
    })
    public void testAppend(int zero, int one, int two){

    }
}

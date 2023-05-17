package fr.ul.miage.lutakhato;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServerExpireTest {

    @Test
    public void testExpire(){
        // Instance du serveur
        Server test = new Server();

        // Tests - valeurs de base
        test.set("initial value - int", 115, new String[0]);
        test.set("initial value - no option", 115, new String[0]);
        test.set("initial value - 0", 115, new String[0]);
        test.set("initial value - 1", 115, new String[0]);
        test.set("initial value - 2", 115, new String[0]);
        test.set("initial value - 3", 115, new String[0]);

        // Ajout des "expire"s
        // valeur de base (sans option)
        test.expire("initial value - no option", 10, new String[0]);
        // expire seulement si n'en possède pas
        test.expire("initial value - 0", 10, new String[]{"NX"});
        // expire seulement si en possède un
        test.expire("initial value - 1", 10, new String[]{"NX"});
        test.expire("initial value - 1", 115, new String[]{"XX"});
        // expire seulement si le nouveau est plus grand
        test.expire("initial value - 2", 10, new String[]{"NX"});
        test.expire("initial value - 2", 15, new String[]{"GR"});
        // expire seulement si le nouveau est plus petit
        test.expire("initial value - 3", 10, new String[]{"NX"});
        test.expire("initial value - 3", 5, new String[]{"LT"});

        assertAll(
                () -> assertEquals(-1, test.getDatabase().get("initial value - int").getExpireMillis()),
                () -> assertEquals(10 * 1000, test.getDatabase().get("initial value - no option").getExpireMillis()),
                () -> assertEquals(10 * 1000, test.getDatabase().get("initial value - 0").getExpireMillis()),
                () -> assertEquals(115 * 1000, test.getDatabase().get("initial value - 1").getExpireMillis()),
                () -> assertEquals(15 * 1000, test.getDatabase().get("initial value - 2").getExpireMillis()),
                () -> assertEquals(5 * 1000, test.getDatabase().get("initial value - 3").getExpireMillis())
        );
    }
}

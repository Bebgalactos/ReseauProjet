package fr.ul.miage.lutakhato;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServerExpireTest {

    @Test
    public void testExpire(){
        // Instance du serveur
        Server test = new Server();

        // Tests - valeurs de base
        test.set("init", 115, new String[]{});
        test.set("sans opt", 115, new String[]{"ex","10"});
        test.set("si aucun", 115, new String[]{"ex","10"});
        test.set("poss dej", 115, new String[]{"ex","10"});
        test.set("plus grand", 115, new String[]{"ex","10"});
        test.set("plus petit", 115, new String[]{"ex","10"});

        // expire seulement si en possède un
        test.expire("poss dej", 115, new String[]{"XX"});
        // expire seulement si le nouveau est plus grand
        test.expire("plus grand", 15, new String[]{"GT"});
        // expire seulement si le nouveau est plus petit
        test.expire("plus petit", 1, new String[]{"LT"});

        assertAll(
                () -> assertEquals(-1, test.getDatabase().get("init").getExpireMillis()),
                () -> assertEquals(10 * 1000, test.getDatabase().get("sans opt").getExpireMillis()),
                () -> assertEquals(10 * 1000, test.getDatabase().get("si aucun").getExpireMillis()),
                () -> assertEquals(115 * 1000, test.getDatabase().get("poss dej").getExpireMillis()),
                () -> assertEquals(15 * 1000, test.getDatabase().get("plus grand").getExpireMillis()),
                () -> assertEquals(1 * 1000, test.getDatabase().get("plus petit").getExpireMillis())
        );
    }
}

package fr.ul.miage.lutakhato;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ServerRequestsTest {
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "set ks001 simple",
            "set ks001 simple GET"
    })
    public void testSetStringRequest(String request) {
        Server serverCall = new Server();
        serverCall.callFunction(request);

        Server serverDirect = new Server();
        String[] requestTable = request.split(" ");

        Map<String, ServerObject> newDatabase = new HashMap<>();
        newDatabase.put(requestTable[1], new ServerObject(requestTable[2]));
        serverDirect.setDatabase(newDatabase);

        assertAll(
                () -> assertEquals(
                        serverCall.getDatabase().get(requestTable[1]).getValue(),
                        serverDirect.getDatabase().get(requestTable[1]).getValue()),
                () -> assertEquals(
                        serverCall.getDatabase().get(requestTable[1]).getExpireMillis(),
                        serverDirect.getDatabase().get(requestTable[1]).getExpireMillis())
        );
    }
}

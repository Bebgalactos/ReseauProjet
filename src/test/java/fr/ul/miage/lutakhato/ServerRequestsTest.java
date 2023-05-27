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
        ServerThread serverCall = new ServerThread(client);
        serverCall.callFunction(request);

        ServerThread serverDirect = new ServerThread(client);
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

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "set ks001 simple EX 10",
            "set ks001 10000l EX 100 GET"
    })
    public void testSetStringRequestWithCreationTime(String request) {
        ServerThread serverCall = new ServerThread(client);
        serverCall.callFunction(request);

        ServerThread serverDirect = new ServerThread(client);
        String[] requestTable = request.split(" ");

        Map<String, ServerObject> newDatabase = new HashMap<>();
        newDatabase.put(requestTable[1], new ServerObject(Integer.parseInt(requestTable[4]) * 1000, requestTable[2]));
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

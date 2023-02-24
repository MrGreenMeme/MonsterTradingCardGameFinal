package Server;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RequestTest {
    @Test
    void contentLengthTest() {
        Request request;
        BufferedReader bufferedReader = new BufferedReader(new StringReader(
                "GET /messages/cards HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "Key: value\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: 9\r\n" +
                        "\r\n" +
                        "{id:test}"));

        Map<String, String> result = new HashMap<>();
        result.put("host:","localhost");
        result.put("key:","value");
        result.put("content-type:","application/json");
        result.put("content-length:","9");

        Http_Parser httpParser = new Http_Parser(bufferedReader);
        request = httpParser.parse();

        assertEquals(9, request.getContentLength());
    }
}
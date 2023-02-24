package Server;

import org.junit.jupiter.api.Test;

import javax.imageio.IIOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class Http_ParserTest {
    @Test
    public void testReadHttpHeader() throws Exception {
        String input = "GET /path HTTP/1.1\n" +
                "Content-Length: 10\n" +
                "Accept: application/json\n" +
                "\n";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        Http_Parser httpParser = new Http_Parser(reader);
        Request request = httpParser.readHttpHeader(reader);
        assertEquals("GET", request.getHttp_Method());
        assertEquals("/path", request.getUrl_Content());
        assertEquals("HTTP/1.1", request.getHttp_Version());
    }

    @Test
    public void testInvalidHttpHeader() throws IOException {
        String request = "GET /test.html\n" +
                 "Content-Length: 9\n" +
                "\n";
        BufferedReader reader = new BufferedReader(new StringReader(request));
        Http_Parser httpParser = new Http_Parser(reader);
        Request request1 = httpParser.readHttpHeader(reader);
        assertNull(request1);
    }

    @Test
    void testUnwrapSuccess() {
        Request request;
        BufferedReader reader = new BufferedReader(new StringReader(
                "GET /cards HTTP/1.1\r\n" +
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

        Http_Parser httpParser = new Http_Parser(reader);
        request = httpParser.parse();

        assertEquals("GET", request.getHttp_Method());
        assertEquals("/cards", request.getUrl_Content());
        assertEquals("HTTP/1.1", request.getHttp_Version());
        assertEquals(result, request.getHttp_header());
        assertEquals("{id:test}", request.getHttp_Body());
    }

}
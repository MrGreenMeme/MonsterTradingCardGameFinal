package Server;

import java.io.BufferedReader;
import java.io.IOException;

public class Http_Parser {
    BufferedReader bufferedReader;
    public Http_Parser (BufferedReader bufferedReader){
        this.bufferedReader = bufferedReader;
    }

    public Request parse() {
        Request request;

        try {
            request = readHttpHeader(bufferedReader);

            if (request != null) {
                int contentLength = request.getContentLength();
                request.setHttp_Body(readHttpBody(bufferedReader, contentLength));
                return request;
            } else {
                return null;
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public Request readHttpHeader(BufferedReader bufferedReader) throws IOException {
        Request request = new Request();
        String message;
        message = bufferedReader.readLine();

        if(message == null){
            return null;
        }
        String[] parts = message.split(" ");
            if(parts.length == 3){
                request.setHttp_Method(parts[0]);
                request.setUrl_Content(parts[1]);
                request.setHttp_Version(parts[2]);
            } else {
                return null;
            }

        while((message = bufferedReader.readLine()) != null){
            if(message.isBlank()){
                break;
            }
            parts = message.split(" ", 2);
            if(parts.length == 2){
                request.add_http_headers(parts[0].toLowerCase(), parts[1]);
            }
        }
        return request;
    }

    public String readHttpBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        StringBuilder stringBuilder = new StringBuilder(10000);
        int count = 0;
        int input;
        while (bufferedReader.ready() && (input = bufferedReader.read()) != -1) {
            stringBuilder.append((char)input);
            count++;
            if(count >= contentLength){
                break;
            }
        }
        return stringBuilder.toString();
    }
 }

package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//ThreadPool
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args){
        System.out.println("starting server");
        try{
            ServerSocket serverSocket = new ServerSocket(10001,5);

            // Foreach client new Thread
            ExecutorService executorService = Executors.newFixedThreadPool(10);

            while(true){
                Socket clientSocket = serverSocket.accept();
                executorService.execute(() -> {
                try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))){
                    // Request from Client
                    Request request;
                    Http_Parser httpParser = new Http_Parser(bufferedReader);
                    request = httpParser.parse();

                    if(request != null){
                        System.out.println("--Header--");
                        System.out.println("    " + request.getHttp_Method() + " " + request.getUrl_Content() + " " + request.getHttp_Version());
                        for(Map.Entry<String, String> entry : request.getHttp_header().entrySet()){
                            System.out.println("    " + entry.getKey() + " " + entry.getValue());
                        }
                        System.out.println("--Body--");
                        System.out.println(request.getHttp_Body());
                        System.out.println("---------------");
                    }
                    // Response from Server
                    ResponseHandler responseHandler = new ResponseHandler(bufferedWriter);
                    responseHandler.response(request);

                } catch (IOException e){
                    e.printStackTrace();
                } finally {
                    try {
                        // Close client connection
                        clientSocket.close();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }

                });
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

package Server;

import Database.DatabaseConnection;
import Game.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResponseHandler {
    BufferedWriter bufferedWriter;

    public ResponseHandler(BufferedWriter bufferedWriter){
        this.bufferedWriter = bufferedWriter;
    }

    public void response(Request request){
        Response response = new Response("400 Bad Request");
        if(request != null && request.getHttp_header() != null){
            String[] parts = request.getUrl_Content().split("/");
            User user;
            if((parts.length == 2 || parts.length == 3)){
                switch (parts[1]){
                    case "delete":
                        response = deleteAll(request);
                        break;
                    case "users":
                        response = users(request);
                        break;
                    case "sessions":
                        response = sessions(request);
                        break;
                    case "packages":
                        response = packages(request);
                        break;
                    case "transactions":
                        if(parts.length != 3){
                            break;
                        }
                        if(parts[2].equals("packages")){
                            user = authorizeUser(request);
                            if(user != null){
                                response = transactionsPackages(user, request);
                            } else {
                                response = new Response("401 Unauthorized");
                            }
                        }
                        break;
                    case "cards":
                        user = authorizeUser(request);
                        if (user != null){
                            response = showCards(user,request);
                        } else {
                            response = new Response("401 Unauthorized");
                        }
                        break;
                    case "deck":
                        user = authorizeUser(request);
                        if (user != null){
                            response = requestDeck(user,request);
                        } else {
                            response = new Response("401 Unauthorized");
                        }
                        break;
                    case "stats":
                        user = authorizeUser(request);
                        if (user != null){
                            response = stats(user,request);
                        } else {
                            response = new Response("401 Unauthorized");
                        }
                        break;
                    case "score":
                        user = authorizeUser(request);
                        if (user != null){
                            response = scoreBoard(request);
                        } else {
                            response = new Response("401 Unauthorized");
                        }
                        break;
                    case "battles":
                        user = authorizeUser(request);
                        if (user != null){
                            response = battle(request,user);
                        } else {
                            response = new Response("401 Unauthorized");
                        }
                        break;
                    case "tradings":
                        user = authorizeUser(request);
                        if (user != null){
                            response = trade(request,user);
                        } else {
                            response = new Response("401 Unauthorized");
                        }
                        break;
                    default:
                        break;

                }
            }
        }
        try {
            bufferedWriter.write(response.getHttp_Version() + " " + response.getHttp_Status() + "\r\n");
            bufferedWriter.write("Server: " + response.getServer() + "\r\n");
            bufferedWriter.write("Content-Type: " + response.getContentType() + "\r\n");
            bufferedWriter.write("Content-Length: " + response.getContentLength() + "\r\n\r\n");
            bufferedWriter.write(response.getMessage());
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Response deleteAll(Request request){
        Response response = new Response("400 Bad Request");
        //UserHandler userHandler = UserHandler.getInstance();
        if(request.getHttp_Method().equals("DELETE")){
            try {
                Connection connection = DatabaseConnection.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM package;");
                preparedStatement.executeUpdate();
                preparedStatement = connection.prepareStatement("DELETE FROM trade;");
                preparedStatement.executeUpdate();
                preparedStatement = connection.prepareStatement("DELETE FROM card;");
                preparedStatement.executeUpdate();
                preparedStatement = connection.prepareStatement("DELETE FROM users;");
                preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.close();
                response.setHttp_Status("200 OK");
            } catch (SQLException e){
                e.printStackTrace();
                response.setHttp_Status("409 Conflict");
                return response;
            }
        }
        return response;
    }

    private User authorizeUser (Request request){
        User user = null;
        if(request.getHttp_header().containsKey("authorization:")){
            UserHandler userHandler = UserHandler.getInstance();
            user = userHandler.authorizeToken(request.getHttp_header().get("authorization:"));
        }
        return user;
    }

    private Response battle(Request request, User user){
        Response response = new Response("400 Bad Request");
        if("POST".equals(request.getHttp_Method())){
            Battle battle = Battle.getInstance();
            String payload = battle.addUser(user);
            if(payload != null){
                response.setMessage(payload);
                response.setHttp_Status("200 OK");
            }
        }
        return response;
    }

    private Response scoreBoard(Request request){
        Battle battle = Battle.getInstance();
        Response response = new Response("400 Bad Request");
        if("GET".equals(request.getHttp_Method())){
            response.setMessage(battle.getScoreboard());
            response.setHttp_Status("200 OK");
        }
        return response;
    }

    private Response stats(User user,Request request){
        Response response = new Response("400 Bad Request");
        if ("GET".equals(request.getHttp_Method())) {
            response.setHttp_Status("200 OK");
            response.setMessage(user.showStats());
        }
        return response;
    }

    private Response users(Request request){
        UserHandler userHandler = UserHandler.getInstance();
        Response response = new Response("400 Bad Request");
        ObjectMapper objectMapper;
        User user;

        switch (request.getHttp_Method()){
            case "GET":
                user = authorizeUser(request);
                if(user != null){
                    String[] parts = request.getUrl_Content().split("/");
                    if(parts.length == 3){
                        if(user.getUsername().equals(parts[2])){
                            response.setMessage(user.showUserInfo());
                            if(response.getMessage() != null){
                                response.setHttp_Status("200 OK");
                            } else {
                                response.setHttp_Status("404 Not found");
                            }
                        } else {
                            response.setHttp_Status("401 Unauthorized");
                        }
                    }
                } else {
                    response.setHttp_Status("401 Unauthorized");
                }
                break;
            case "POST":
                objectMapper = new ObjectMapper();
                try {
                    JsonNode jsonNode = objectMapper.readTree(request.getHttp_Body());
                    if ( jsonNode.has("Username") && jsonNode.has("Password")){
                        if (userHandler.registerUser(jsonNode.get("Username").asText(),jsonNode.get("Password").asText())) {
                            response.setHttp_Status("201 Created");
                        } else {
                            response.setHttp_Status("409 Conflict");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "PUT":
                user = authorizeUser(request);
                if (user != null){
                    String[] editUser = request.getUrl_Content().split("/");
                    if (editUser.length == 3){
                        if (user.getUsername().equals(editUser[2])){
                            objectMapper = new ObjectMapper();
                            try {
                                JsonNode jsonNode = objectMapper.readTree(request.getHttp_Body());
                                if ( jsonNode.has("Name") && jsonNode.has("Bio") && jsonNode.has("Image")){
                                    if (user.setUserInfo(jsonNode.get("Name").asText(),jsonNode.get("Bio").asText(),jsonNode.get("Image").asText())){
                                        response.setHttp_Status("200 OK");
                                    } else {
                                        response.setHttp_Status("404 Not Found");
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            response.setHttp_Status("401 Unauthorized");
                        }
                    }
                } else {
                    response.setHttp_Status("401 Unauthorized");
                }
                break;
            default:
                break;
        }
        return response;
    }

    private Response sessions(Request request){
        UserHandler userHandler = UserHandler.getInstance();
        Response response = new Response("400 Bad Request");
        ObjectMapper objectMapper = new ObjectMapper();
        switch (request.getHttp_Method()){
            case "POST":
                try {
                    JsonNode jsonNode = objectMapper.readTree(request.getHttp_Body());
                    if ( jsonNode.has("Username") && jsonNode.has("Password")){
                        if (userHandler.loginUser(jsonNode.get("Username").asText(),jsonNode.get("Password").asText())) {
                            response.setHttp_Status("200 OK");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "DELETE":
                try {
                    JsonNode jsonNode = objectMapper.readTree(request.getHttp_Body());
                    if ( jsonNode.has("Username") && jsonNode.has("Password")){
                        if (userHandler.logOutUser(jsonNode.get("Username").asText(),jsonNode.get("Password").asText())) {
                            response.setHttp_Status("200 OK");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        return response;
    }

    private Response packages(Request request){
        CardHandler cardHandler = CardHandler.getInstance();
        Response response = new Response("400 Bad Request");
        if (request.getHttp_Method().equals("POST")){
            UserHandler userHandler = UserHandler.getInstance();
            if (request.getHttp_header().containsKey("authorization:") && !userHandler.isAdmin(request.getHttp_header().get("authorization:"))){
                response.setHttp_Status("403 Forbidden");
                return response;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
                List<TemplateCard> cards = objectMapper.readValue(request.getHttp_Body(), new TypeReference<>(){});
                if (cards.size() == 5){
                    List<TemplateCard> createdCards = new ArrayList<>();
                    for (TemplateCard card: cards){
                        if (cardHandler.registerNewCard(card.getId(),card.getName(),card.getDamage())) {
                            createdCards.add(card);
                        } else {
                            for (TemplateCard card_tmp: createdCards){
                                cardHandler.deleteCard(card_tmp.getId());
                            }
                            return response;
                        }
                    }
                    if(cardHandler.createNewPackage(cards)){
                        response.setHttp_Status("201 Created");
                    } else {
                        for (TemplateCard card_tmp: createdCards){
                            cardHandler.deleteCard(card_tmp.getId());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    private Response transactionsPackages(User user,Request request){
        CardHandler cardHandler = CardHandler.getInstance();
        Response response = new Response("400 Bad Request");
        if (request.getHttp_Method().equals("POST")) {
            if (cardHandler.acquireNewPackage(user)){
                response.setHttp_Status("200 OK");
            } else {
                response.setHttp_Status("409 Conflict");
            }
        }
        return response;
    }

    private Response showCards(User user,Request request){
        Response response = new Response("400 Bad Request");
        if ("GET".equals(request.getHttp_Method())) {
            String json = CardHandler.getInstance().showUserCards(user);
            if (json != null) {
                response.setHttp_Status("200 OK");
                response.setMessage(json);
            } else {
                response.setHttp_Status("410 Error");
            }
        }
        return response;
    }

    private Response requestDeck(User user,Request request){
        Response response = new Response("400 Bad Request");
        CardHandler cardHandler = CardHandler.getInstance();
        switch (request.getHttp_Method()) {
            case "GET":
                String json = cardHandler.showUserDeck(user);
                if (json != null){
                    response.setHttp_Status("200 OK");
                    response.setMessage(json);
                } else {
                    response.setHttp_Status("410 Error");
                }
                break;
            case "PUT":
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    List<String> ids = objectMapper.readValue(request.getHttp_Body(), new TypeReference<>(){});
                    if (ids.size() == 4){
                        if (cardHandler.createDeck(user,ids)){
                            response.setHttp_Status("201 Created");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        return response;
    }

    private Response trade (Request request, User user){
        Trading trade = Trading.getInstance();
        Response response = new Response("400 Bad Request");
        String[] parts;

        switch (request.getHttp_Method()){
            case "GET":
                response.setMessage(trade.checkTradingDeals());
                response.setHttp_Status("200 OK");
                break;
            case "POST":
                parts = request.getUrl_Content().split("/");
                ObjectMapper objectMapper = new ObjectMapper();
                if(parts.length == 3){
                    try {
                        JsonNode jsonNode = objectMapper.readTree(request.getHttp_Body());
                        if(jsonNode.has("CardToTrade")){
                            if(trade.tradeCards(user, parts[2], jsonNode.get("CardToTrade").asText())){
                                response.setHttp_Status("200 OK");
                            }
                        }
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JsonNode jsonNode = objectMapper.readTree(request.getHttp_Body());
                        if(jsonNode.has("Id") && jsonNode.has("CardToTrade") && jsonNode.has("Type") && jsonNode.has("MinimumDamage")){
                            if(trade.tradeWithCard(user,jsonNode.get("Id").asText(),jsonNode.get("CardToTrade").asText(),(float)jsonNode.get("MinimumDamage").asDouble(),jsonNode.get("Type").asText())){
                                response.setHttp_Status("201 Created");
                            }
                        }
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
                break;
            case "DELETE":
                parts = request.getUrl_Content().split("/");
                if(parts.length == 3){
                    if(trade.removeTradedCards(user, parts[2])){
                        response.setHttp_Status("200 OK");
                    }
                }
                break;
            default:
                break;
        }
        return response;
    }


}

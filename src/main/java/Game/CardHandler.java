package Game;

import Database.DatabaseConnection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CardHandler {

    private static CardHandler single_instance = null;

    public static CardHandler getInstance(){
        if(single_instance == null){
            single_instance = new CardHandler();
        }
        return single_instance;
    }

    public boolean createNewPackage(List<TemplateCard> cards){
        if(cards.size() != 5){
            return false;
        }
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            for(TemplateCard card: cards){
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(cardid) FROM card WHERE cardid = ? AND collection IS NULL;");
                preparedStatement.setString(1, card.getId());
                ResultSet rs = preparedStatement.executeQuery();
                preparedStatement.close();
                if(!rs.next() || rs.getInt(1) != 1){
                    rs.close();
                    connection.close();
                    return false;
                }
                rs.close();
            }
            connection.close();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO package(cardid_1, cardid_2, cardid_3, cardid_4, cardid_5) VALUES(?,?,?,?,?);");
            preparedStatement.setString(1,cards.get(0).getId());
            preparedStatement.setString(2,cards.get(1).getId());
            preparedStatement.setString(3,cards.get(2).getId());
            preparedStatement.setString(4,cards.get(3).getId());
            preparedStatement.setString(5,cards.get(4).getId());
            int affectedRows = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            if(affectedRows != 1){
                return false;
            }
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String convertToJson(ResultSet resultSet) throws SQLException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        while (resultSet.next()){
            ObjectNode card = objectMapper.createObjectNode();
            card.put("ID", resultSet.getString(1));
            card.put("Name", resultSet.getString(2));
            card.put("Damage", resultSet.getString(3));
            arrayNode.add(card);
        }
        resultSet.close();
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
    }

    public String showUserCards(User user){
        try{
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT cardid, name, damage FROM card WHERE owner = ?;");
            preparedStatement.setString(1, user.getUsername());
            String jsonString = convertToJson(preparedStatement.executeQuery());
            preparedStatement.close();
            connection.close();
            return jsonString;
        } catch (SQLException | JsonProcessingException e){
            e.printStackTrace();
            return null;
        }
    }
    public String showUserDeck(User user){
        try{
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT cardid, name, damage FROM card WHERE owner = ? AND collection = 'deck';");
            preparedStatement.setString(1, user.getUsername());
            String jsonString = convertToJson(preparedStatement.executeQuery());
            preparedStatement.close();
            connection.close();
            return jsonString;
        } catch (SQLException | JsonProcessingException e){
            e.printStackTrace();
            return null;
        }
    }

    public boolean acquireNewPackage(User user){
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM package;");
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
            if(!resultSet.next()){
                resultSet.close();
                connection.close();
                return false;
            }
            int packageID = resultSet.getInt(1);
            if(!user.acquirePackage()) {
                return false;
            }

            PreparedStatement prepareStatement_Card = connection.prepareStatement("UPDATE card SET owner = ?, collection = 'stack' WHERE cardid = ?;");
            for(int i = 2; i < 7; i++){
                prepareStatement_Card.setString(1, user.getUsername());
                prepareStatement_Card.setString(2, resultSet.getString(i));
                prepareStatement_Card.executeUpdate();
            }
            resultSet.close();
            prepareStatement_Card.close();
            PreparedStatement preparedStatement_Package = connection.prepareStatement("DELETE FROM package WHERE packageid = ?;");
            preparedStatement_Package.setInt(1, packageID);
            preparedStatement_Package.executeUpdate();
            preparedStatement_Package.close();
            connection.close();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ElementType createNewElementType (String elementType){
        if(elementType.toLowerCase().contains("fire")){
            return ElementType.Fire;
        }
        else if(elementType.toLowerCase().contains("water")){
            return ElementType.Water;
        } else {
            return ElementType.Normal;
        }
    }

    public MonsterType createNewMonsterType (String monsterType){
        if(monsterType.toLowerCase().contains("goblin")){
            return MonsterType.Goblin;
        }
        else if(monsterType.toLowerCase().contains("dragon")){
            return MonsterType.Dragon;
        }
        else if(monsterType.toLowerCase().contains("wizard")){
            return MonsterType.Wizard;
        }
        else if(monsterType.toLowerCase().contains("ork")){
            return MonsterType.Ork;
        }
        else if(monsterType.toLowerCase().contains("knight")){
            return MonsterType.Knight;
        }
        else if(monsterType.toLowerCase().contains("kraken")){
            return MonsterType.Kraken;
        }
        else if(monsterType.toLowerCase().contains("fireelf")){
            return MonsterType.FireElf;
        }
        else if(monsterType.toLowerCase().contains("troll")){
            return MonsterType.Troll;
        }
        else {
            return null;
        }
    }

    public Deck getUserDeck (User user){
        Deck deck = null;
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT cardid, name, damage FROM card WHERE owner = ? AND collection = 'deck';");
            preparedStatement.setString(1,user.getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
            List<Card> cards = new ArrayList<>();
            while(resultSet.next()){
                String cardName = resultSet.getString(2);
                if(!cardName.contains("spell")){
                    cards.add(new MonsterCard(resultSet.getString(1), cardName, resultSet.getFloat(3), createNewMonsterType(cardName), createNewElementType(cardName)));
                } else {
                    cards.add(new SpellCard(resultSet.getString(1), cardName, resultSet.getFloat(3), createNewElementType(cardName)));
                }
            }
            deck = new Deck(cards);
            resultSet.close();
            connection.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return deck;
    }

    public boolean registerNewCard(String id, String cardName, float damage){
        if(!id.isEmpty() && !cardName.isEmpty()){
            ElementType elementType = createNewElementType(cardName);
            MonsterType monsterType = createNewMonsterType(cardName);
            try{
                Connection connection = DatabaseConnection.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO card(cardid, name, damage) VALUES(?,?,?);");
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, cardName);
                preparedStatement.setFloat(3, damage);
                int affectedRows = preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.close();
                if(affectedRows == 0){
                    return false;
                }

            } catch (SQLException e){
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    public void deleteCard(String cardID){
        try{
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM card WHERE cardid = ? AND collection IS NULL");
            preparedStatement.setString(1, cardID);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean createDeck(User user, List<String> cardID){
        if(cardID.size() != 4){
            return false;
        }
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            List<String> checkOwnedCards = new LinkedList<>();

            for(String card_ID: cardID){
                if(checkOwnedCards.contains(card_ID)){
                    connection.close();
                    return false;
                }
                checkOwnedCards.add(card_ID);
                //--------------------------------------------//
                if(Trading.getInstance().cardAtTrade(card_ID)){
                    return false;
                }

                PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(cardid) FROM card WHERE cardid = ? AND owner = ?;");
                preparedStatement.setString(1, card_ID);
                preparedStatement.setString(2, user.getUsername());
                ResultSet resultSet = preparedStatement.executeQuery();
                if(!resultSet.next() || resultSet.getInt(1) != 1){
                    preparedStatement.close();
                    resultSet.close();
                    connection.close();
                    return false;
                }
            }
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE card SET collection = 'stack' WHERE owner = ?;");
            preparedStatement.setString(1,user.getUsername());
            preparedStatement.executeUpdate();

            for(String card_ID : cardID){
                preparedStatement = connection.prepareStatement("UPDATE card SET collection = 'deck' WHERE owner = ? AND cardid = ?;");
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, card_ID);
                preparedStatement.executeUpdate();
            }
            preparedStatement.close();
            connection.close();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

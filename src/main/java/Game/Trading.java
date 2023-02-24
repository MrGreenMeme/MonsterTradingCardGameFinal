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

public class Trading {

    private static Trading single_instance;
    public static Trading getInstance(){
        if(single_instance == null){
            single_instance = new Trading();
        }
        return single_instance;
    }

    public String checkTradingDeals(){
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT tradeid, card.cardid, name, damage, owner, mindamage, type FROM trade JOIN card ON card.cardid = trade.cardid;");
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode arrayNode = objectMapper.createArrayNode();
            while (resultSet.next()) {
                ObjectNode deal = objectMapper.createObjectNode();
                deal.put("TradeID", resultSet.getString(1));
                deal.put("CardID", resultSet.getString(2));
                deal.put("Name", resultSet.getString(3));
                deal.put("Damage", resultSet.getString(4));
                deal.put("Owner", resultSet.getString(5));
                deal.put("MinDamage", resultSet.getString(6));
                deal.put("Type", resultSet.getString(7));
                arrayNode.add(deal);
            }
            resultSet.close();
            connection.close();
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
            } catch (SQLException | JsonProcessingException e){
                e.printStackTrace();
                return null;
        }
    }

    public boolean removeTradedCards(User user, String id) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT card.owner FROM card JOIN trade ON card.cardid = trade.cardid WHERE trade.tradeid = ?;");
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(!resultSet.next() || !resultSet.getString(1).equals(user.getUsername())){
                resultSet.close();
                connection.close();
                return false;
            }
            resultSet.close();
            preparedStatement = connection.prepareStatement("DELETE FROM trade WHERE tradeid = ?;");
            preparedStatement.setString(1, id);
            int affectedRows = preparedStatement.executeUpdate();
            preparedStatement.close();
            if(affectedRows != 1 ){
                connection.close();
                return false;
            }
            connection.close();
            return true;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean tradeWithCard (User user, String tradeID, String cardID, float minDamage, String type){
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            // card available
            if(cardAtTrade(cardID)){
                return false;
            }

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(cardid) FROM card WHERE owner = ? AND cardid = ? AND collection LIKE 'stack';");
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2,cardID);
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
            if(!resultSet.next() || resultSet.getInt(1) != 1){
                resultSet.close();
                connection.close();
                return false;
            }
            resultSet.close();
            preparedStatement = connection.prepareStatement("INSERT INTO trade(tradeid, cardid, mindamage, type) VALUES(?,?,?,?);");
            preparedStatement.setString(1, tradeID);
            preparedStatement.setString(2, cardID);
            preparedStatement.setFloat(3, minDamage);
            preparedStatement.setString(4, type);
            int affectedRows = preparedStatement.executeUpdate();
            preparedStatement.close();
            if(affectedRows != 1){
                return false;
            }
            connection.close();
            return true;

        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean cardAtTrade(String cardID){
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(cardid) FROM trade WHERE cardid = ?;");
            preparedStatement.setString(1, cardID);
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
            if(resultSet.next() && resultSet.getInt(1) == 1){
                resultSet.close();
                connection.close();
                return true;
            }
            resultSet.close();
            connection.close();
            return false;

        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean tradeCards(User user, String tradeID, String cardID){
        if(user == null){
            return false;
        }
        try {
            String cardName;
            float cardDamage;
            String offeredCardID;
            String offeredCardOwner;
            float minDamage;
            String type;

            Connection connection = DatabaseConnection.getInstance().getConnection();
            if(cardAtTrade(cardID)){
                return false;
            }
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT name, damage FROM card WHERE owner = ? AND cardid = ? AND collection LIKE 'stack';");
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, cardID);
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
            if(!resultSet.next()){
                resultSet.close();
                connection.close();
                return false;
            }
            cardName = resultSet.getString(1);
            cardDamage = resultSet.getFloat(2);
            resultSet.close();

            preparedStatement = connection.prepareStatement("SELECT trade.cardid, owner, mindamage, type FROM trade JOIN card ON trade.cardid = card.cardid = card.cardid WHERE tradeid = ?;");
            preparedStatement.setString(1, tradeID);
            resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
            if(!resultSet.next()){
                resultSet.close();
                connection.close();
                return false;
            }
            offeredCardID = resultSet.getString(1);
            offeredCardOwner = resultSet.getString(2);
            minDamage = resultSet.getFloat(3);
            type = resultSet.getString(4);
            resultSet.close();

            CardHandler cardHandler = CardHandler.getInstance();

            if(offeredCardOwner.equalsIgnoreCase(user.getUsername())){
                return false;
            }
            if(cardDamage < minDamage){
                return false;
            }
            if(type.equalsIgnoreCase("monster")){
                if(cardName.contains("spell") || cardName.contains("Spell")){
                    return false;
                } else if (cardHandler.createNewMonsterType(cardName) != cardHandler.createNewMonsterType(type)){
                    return false;
                }
            }

            preparedStatement = connection.prepareStatement("UPDATE card SET owner = ? WHERE cardid = ?;");
            preparedStatement.setString(1, offeredCardOwner);
            preparedStatement.setString(2, cardID);
            preparedStatement.executeUpdate();
            preparedStatement.close();

            preparedStatement = connection.prepareStatement("UPDATE card SET owner = ? WHERE cardid = ?;");
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, offeredCardID);
            preparedStatement.executeUpdate();
            preparedStatement.close();

            preparedStatement = connection.prepareStatement("DELETE FROM trade WHERE tradeid = ?;");
            preparedStatement.setString(1, tradeID);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            return true;

        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}

package Game;

import Database.DatabaseConnection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String username;
    private String name;
    private String bio;
    private String image;
    private Integer coins;
    private Integer wins;
    private Integer gamesPlayed;
    private Integer elo;

    public User(String username, String name, String bio, String image, int coins, int gamesPlayed, int wins, int elo) {
        this.username = username;
        this.name = name;
        this.bio = bio;
        this.image = image;
        this.coins = coins;
        this.gamesPlayed = gamesPlayed;
        this.wins = wins;
        this.elo = elo;
    }

    public boolean UpdateStats() {
        try{
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET wins = ?, gamesplayed = ?, elo = ? WHERE username = ?;");
            preparedStatement.setInt(1,wins);
            preparedStatement.setInt(2,gamesPlayed);
            preparedStatement.setInt(3,elo);
            preparedStatement.setString(4,username);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            return true;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public String showStats() {
        try{
            Map<String, Integer> statsMap = new HashMap<>();
            statsMap.put("Wins:", wins);
            statsMap.put("Games:", gamesPlayed);
            return new ObjectMapper().writeValueAsString(statsMap);
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return null;
    }

    public String showUserInfo() {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("Name:", name);
            map.put("Bio:", bio);
            map.put("Image:", image);
            map.put("Coins:", coins.toString());
            return new ObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean setUserInfo(String name, String bio, String image) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET name = ?, bio = ?, image = ? WHERE username = ?;");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, bio);
            preparedStatement.setString(3, image);
            preparedStatement.setString(4, username);
            int affectedRows = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            if(affectedRows == 1){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean acquirePackage(){
        try{
            if(coins < 5){
                return false;
            }
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET coins = ? WHERE username = ?;");
            preparedStatement.setInt(1, coins - 5);
            preparedStatement.setString(2,username);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            return true;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean battleWin() {
        wins++;
        gamesPlayed++;
        elo+= 3;
        return UpdateStats();
    }

    public boolean battleLoose() {
        gamesPlayed++;
        elo-= 5;
        return UpdateStats();
    }

    public boolean battleDraw() {
        gamesPlayed++;
        return UpdateStats();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public int getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(Integer gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public Integer getElo() {
        return elo;
    }

    public void setElo(Integer elo) {
        this.elo = elo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

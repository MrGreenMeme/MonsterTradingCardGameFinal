package Game;

import Database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserHandler {

    private static UserHandler single_instance = null;

    public static UserHandler getInstance(){
        if(single_instance == null){
            single_instance = new UserHandler();
        }
        return single_instance;
    }

    public User authorizeToken(String token){
        try{
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT username, name, bio, image, coins, gamesplayed, wins, elo FROM users WHERE token = ? AND loggedin = TRUE;");
            preparedStatement.setString(1, token);
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
            if(!resultSet.next()){
                resultSet.close();
                connection.close();
                return null;
            }
            User user = new User(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getInt(5), resultSet.getInt(6), resultSet.getInt(7), resultSet.getInt(8));
            resultSet.close();
            connection.close();
            return user;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean isAdmin (String token){
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(username) FROM users WHERE token = ? AND admin = TRUE AND loggedin = TRUE;");
            preparedStatement.setString(1, token);
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
            if(!resultSet.next() || resultSet.getInt(1) != 1){
                resultSet.close();
                connection.close();
                return false;
            }
            connection.close();
            return true;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerUser (String username, String password){
        String token = "Basic "+ username + "-mtcgToken";
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement("SELECT COUNT(username) FROM users WHERE username = ?;");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
            if (!resultSet.next() || resultSet.getInt(1) > 0){
                //preparedStatement.close();
                return false;
            }
            if(username.equals("admin")){
                preparedStatement = connection.prepareStatement("INSERT INTO users(username, password,token, admin) VALUES(?,?,?,TRUE);");
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO users(username, password, token) VALUES(?,?,?);");
            }
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3,token);
            int affectedRows = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            return affectedRows != 0;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean loginUser (String username, String password){
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET loggedin = TRUE WHERE username = ? AND password = ?;");
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,password);
            int affectedRows = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            if(affectedRows == 1){
                return true;
            }
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean logOutUser (String username, String password){
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET loggedin = FALSE WHERE username = ? AND password = ?;");
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,password);
            int affectedRows = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            if(affectedRows == 1){
                return true;
            }
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }



}

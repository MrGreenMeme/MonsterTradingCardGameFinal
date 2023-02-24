package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

        private static DatabaseConnection instance;

        public static DatabaseConnection getInstance(){ // Singleton
            if(DatabaseConnection.instance == null){
                DatabaseConnection.instance = new DatabaseConnection();
            }
            return DatabaseConnection.instance;
        }
        public Connection getConnection(){
            try{
                return DriverManager.getConnection("jdbc:postgresql://localhost:5432/testing", "postgres", "admin");

            } catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        }
}

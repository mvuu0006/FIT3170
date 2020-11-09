package monash.edu.git;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import com.mysql.cj.jdbc.MysqlDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.util.HashMap;

public class DatabaseHandler {
    /**
     * This class is designed to clean up the controller(s) by abstracting the code into a separate class
     */

    private String databaseURL;
    private String databaseUsername;
    private String databasePassword; // TODO: Find a different way to handle this

    public DatabaseHandler(String dbURL, String dbUser, String dbPassword) {
        this.databaseURL = dbURL;
        this.databaseUsername = dbUser;
        this.databasePassword = dbPassword;
    }

    public JSONArray executeQuery(String sqlScript, HashMap<String, FieldType> fields) throws ClassNotFoundException {
        // Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = null;
        JSONArray rowArray = new JSONArray();
        try {
            // conn = DriverManager.getConnection(databaseURL, databaseUsername, databasePassword);
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setUser(databaseUsername);
            dataSource.setURL(databaseURL);
            dataSource.setPassword(databasePassword);
            conn = dataSource.getConnection();
        }
        catch (SQLException e) {
            throw new Error("Problem", e);
        }
        finally {
            try {
                if (conn != null) {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sqlScript);
                    boolean empty = true;
                    while (rs.next()) {
                        empty = false;
                        JSONObject row = new JSONObject();
                        for (String field: fields.keySet()) {
                            switch (fields.get(field)) {
                                case INT:
                                    int intVal = rs.getInt(field);
                                    row.put(field, intVal);
                                    break;
                                case STRING:
                                    String strVal = rs.getString(field);
                                    row.put(field, strVal);
                                    break;
                                default:
                                    break;
                            }
                        }
                        rowArray.put(row);
                    }
                    conn.close();
                    if (empty) {
                        throw new NoEntryException();
                    }
                }
            }
            catch (SQLException | JSONException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return rowArray;
    }

    public int executeUpdate(String sqlScript) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = null;
        int returnInt = 0;
        try {
            conn = DriverManager.getConnection(databaseURL, databaseUsername, databasePassword);
        }
        catch (SQLException e) {
            throw new Error("Problem", e);
        }
        finally {
            try {
                if (conn != null) {
                    Statement stmt = conn.createStatement();
                    int rs = stmt.executeUpdate(sqlScript);
                    conn.close();
                    returnInt = rs;
                }
            }
            catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return returnInt;
    }
}

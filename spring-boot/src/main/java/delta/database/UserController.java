package delta.database;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

/**
 * Created by lenovo on 23.02.2017.
 */
public class UserController {

    public Connection conn;

    public UserController() {
        try {
            DriverManager.deregisterDriver(new com.mysql.jdbc.Driver());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        openConnection();
        closeConnection();
    }

    private String passHash(String pass){
        String hash = "";
        try {
            byte[] bytesOfMessage = new byte[0];
            bytesOfMessage = pass.getBytes("UTF-8");
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            byte[] thedigest = md.digest(bytesOfMessage);
            hash = new String(thedigest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return hash;
    }

    private int getUser(String token) throws SQLException {
        openConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = conn.prepareStatement(Queries.GET_SESSION);
        preparedStatement.setString(1, token);
        ResultSet rs = preparedStatement.executeQuery();

        int userid = -1;
        java.util.Date expDate = new java.util.Date(0);
        while (rs.next()) {
            userid = rs.getInt("id");
            expDate = rs.getTimestamp("expiration_date");
        }
        if (new java.util.Date(System.currentTimeMillis()).after(expDate)) {
            PreparedStatement deletion = null;
            deletion = conn.prepareStatement(Queries.DELETE_SESSION);
            deletion.setString(1, token);
            deletion.executeUpdate();
            deletion.close();
            userid = -1;
        }

        preparedStatement.close();
        closeConnection();
        return userid;
    }

    public void openConnection() {
        try {
            conn = DriverManager.getConnection(Context.getPropertyByName("DB_URL"),
                    Context.getPropertyByName("DB_USER"), Context.getPropertyByName("DB_PASS"));
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createUser(String name, String pass, String desc) throws SQLException {
        openConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = conn.prepareStatement(Queries.CREATE_USER);
        preparedStatement.setString(1, name);
        String hash = passHash(pass);
        preparedStatement.setString(2, hash);
        preparedStatement.setString(3, desc);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        closeConnection();
    }

    public String auth(String name, String pass) throws SQLException {
        openConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = conn.prepareStatement(Queries.GET_USER_BY_NAME);
        preparedStatement.setString(1, name);
        ResultSet rs = preparedStatement.executeQuery();

        int userid = -1;
        String passhash = "pass";
        while (rs.next()) {
            userid = rs.getInt("ID");
            passhash = rs.getString("passwordhash");
        }
        preparedStatement.close();

        String hash = passHash(pass);

        if (!hash.equals(passhash)) {
            throw new SQLInvalidAuthorizationSpecException("Wrong password");
        }

        preparedStatement = conn.prepareStatement(Queries.CREATE_SESSION);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis() + 10 * 60 * 1000);
        preparedStatement.setInt(1, userid);
        String sessionToken = RandomStringUtils.randomAlphanumeric(10);
        preparedStatement.setString(2, sessionToken);
        preparedStatement.setTimestamp(3, timestamp);
        preparedStatement.executeUpdate();

        preparedStatement.close();
        closeConnection();
        return sessionToken;
    }

    public void deleteUser(String token, String name, String pass) throws SQLException {
        int userid = getUser(token);
        if (userid == -1) {
            throw new SQLInvalidAuthorizationSpecException();
        }

        openConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = conn.prepareStatement(Queries.DELETE_USER);
        preparedStatement.setString(1, name);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        closeConnection();
    }

    public void updateUserPass(String token, String name, String pass) throws SQLException {
        int userid = getUser(token);
        if (userid == -1) {
            throw new SQLInvalidAuthorizationSpecException();
        }

        openConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = conn.prepareStatement(Queries.UPDATE_USER_PASS);
        String hash = "";
        try {
            byte[] bytesOfMessage = new byte[0];
            bytesOfMessage = pass.getBytes("UTF-8");
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            byte[] thedigest = md.digest(bytesOfMessage);
            hash = new String(thedigest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        preparedStatement.setString(1, hash);
        preparedStatement.setString(2, name);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        closeConnection();
    }

    public void updateUserDesc(String token, String name, String desc) throws SQLException {
        int userid = getUser(token);
        if (userid == -1) {
            throw new SQLInvalidAuthorizationSpecException();
        }

        openConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = conn.prepareStatement(Queries.UPDATE_USER_DESC);
        preparedStatement.setString(1, desc);
        preparedStatement.setString(2, name);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        closeConnection();
    }
}

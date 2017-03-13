package delta.database;

import delta.entity.Goods;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 16.02.2017.
 */
public class GoodsController {

    public Connection conn;

    public GoodsController() {
        try {
            DriverManager.deregisterDriver(new com.mysql.jdbc.Driver());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        openConnection();
        closeConnection();
    }

    private int getUser(String token) throws SQLException {
        openConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = conn.prepareStatement(Queries.GET_SESSION);
        preparedStatement.setString(1, token);
        ResultSet rs = preparedStatement.executeQuery();

        int userid = -1;
        java.util.Date expDate = new java.util.Date(0);
        while(rs.next()){
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
            System.out.println("Connection Failed!");
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

    public void addGoods(String token, String name, int cost, String desc) throws SQLException {
        int userid = getUser(token);
        if (userid == -1){
            throw new SQLInvalidAuthorizationSpecException();
        }

        openConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = conn.prepareStatement(Queries.CREATE_GOODS);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, desc);
        preparedStatement.setInt(3, cost);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        closeConnection();
    }

    public Goods getGoodsByID(String token, int id) throws SQLException {
        int userid = getUser(token);
        if (userid == -1){
            throw new SQLInvalidAuthorizationSpecException();
        }
        openConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = conn.prepareStatement(Queries.GET_GOODS_BY_ID);
        preparedStatement.setInt(1, id);
        ResultSet rs = preparedStatement.executeQuery();

        Goods result = new Goods();
        while (rs.next()) {
            result.setId(rs.getInt("ID"));
            result.setName(rs.getString("NAME"));
            result.setDescription(rs.getString("DESCRIPTION"));
            result.setCost(rs.getInt("COST"));
        }

        preparedStatement.close();
        closeConnection();
        return result;
    }

    public List<Goods> getGoods(String token) throws SQLException {
        int userid = getUser(token);
        if (userid == -1){
            throw new SQLInvalidAuthorizationSpecException();
        }

        openConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = conn.prepareStatement(Queries.GET_GOODS);
        ResultSet rs = preparedStatement.executeQuery();

        List<Goods> result = new ArrayList<>();
        while (rs.next()) {
            Goods tmp = new Goods();
            tmp.setId(rs.getInt("ID"));
            tmp.setName(rs.getString("NAME"));
            tmp.setDescription(rs.getString("DESCRIPTION"));
            tmp.setCost(rs.getInt("COST"));
            result.add(tmp);
        }

        preparedStatement.close();
        closeConnection();
        return result;
    }

    public void deleteGoods(String token, int id) throws SQLException {
        int userid = getUser(token);
        if (userid == -1){
            throw new SQLInvalidAuthorizationSpecException();
        }

        openConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = conn.prepareStatement(Queries.DELETE_ALL_ORDERS_OF_GOODS);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        preparedStatement.close();

        preparedStatement = conn.prepareStatement(Queries.DELETE_GOODS);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        closeConnection();
    }

    public void updateGoods(String token, String name, int cost) throws SQLException {
        int userid = getUser(token);
        if (userid == -1){
            throw new SQLInvalidAuthorizationSpecException();
        }

        openConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = conn.prepareStatement(Queries.UPDATE_GOODS);
        preparedStatement.setInt(1, cost);
        preparedStatement.setString(2, name);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        closeConnection();
    }
}

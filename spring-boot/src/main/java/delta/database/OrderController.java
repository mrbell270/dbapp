package delta.database;

import delta.entity.Billing;
import delta.entity.Goods;
import delta.entity.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 26.02.2017.
 */
public class OrderController {
    private Connection conn;

    public OrderController() {
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

    private boolean checkOrder(int userid, int orderid) throws SQLException {
        openConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = conn.prepareStatement(Queries.GET_ORDERS_BY_ID);
        preparedStatement.setInt(1, orderid);
        ResultSet rs = preparedStatement.executeQuery();

        int resuid = -1;
        while(rs.next()){
            resuid = rs.getInt("userid");
        }
        preparedStatement.close();
        closeConnection();
        return resuid==userid;
    }

    private void openConnection() {
        try {
            conn = DriverManager.getConnection(Context.getPropertyByName("DB_URL"),
                    Context.getPropertyByName("DB_USER"), Context.getPropertyByName("DB_PASS"));
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addOrder(String token) throws SQLException {
        int userid = getUser(token);
        if (userid == -1){
            throw new SQLInvalidAuthorizationSpecException();
        }
        openConnection();
        PreparedStatement preparedStatement = null;

        preparedStatement = conn.prepareStatement(Queries.CREATE_ORDER);
        preparedStatement.setInt(1, userid);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        closeConnection();
    }

    public void addGoodsToOrder(String token, int orderid, int goodsid) throws SQLException {
        int userid = getUser(token);
        if (userid == -1){
            throw new SQLInvalidAuthorizationSpecException();
        }

        if (!checkOrder(userid, orderid)){
            throw new SQLClientInfoException();
        }

        openConnection();
        PreparedStatement preparedStatement = null;

        preparedStatement = conn.prepareStatement(Queries.ADD_GOODS_TO_ORDER);
        preparedStatement.setInt(1, orderid);
        preparedStatement.setInt(2, goodsid);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        closeConnection();
    }

    public void addBilling(String token, int orderid) throws SQLException {
        int userid = getUser(token);
        if (userid == -1){
            throw new SQLInvalidAuthorizationSpecException();
        }

        if (!checkOrder(userid, orderid)) {
            throw new SQLClientInfoException();
        }

        openConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = conn.prepareStatement(Queries.GET_GOODS_IN_ORDER);
        preparedStatement.setInt(1, orderid);
        ResultSet rs = preparedStatement.executeQuery();

        int totalCost = 0;
        while (rs.next()) {
            int goodsid = rs.getInt("GOODID");
            PreparedStatement preparedStatement2 = null;
            preparedStatement2 = conn.prepareStatement(Queries.GET_GOODS_COST);
            preparedStatement2.setInt(1, goodsid);
            ResultSet rs2 = preparedStatement2.executeQuery();
            while (rs2.next()) {
                totalCost += rs2.getInt("COST");
            }
            preparedStatement2.close();
        }
        preparedStatement.close();

        preparedStatement = conn.prepareStatement(Queries.CREATE_BILLING);
        preparedStatement.setInt(1, orderid);
        preparedStatement.setInt(2, totalCost);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        closeConnection();
    }

    public List<Order> getOrders(String token) throws SQLException {
        int userid = getUser(token);
        if (userid == -1){
            throw new SQLInvalidAuthorizationSpecException();
        }

        openConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = conn.prepareStatement(Queries.GET_ORDERS);
        preparedStatement.setInt(1, userid);
        ResultSet rs = preparedStatement.executeQuery();

        List<Order> result = new ArrayList<>();
        while (rs.next()) {
            Order tmp = new Order();
            tmp.setId(rs.getInt("ID"));
            tmp.setUserid(rs.getInt("USERID"));
            result.add(tmp);
        }

        preparedStatement.close();
        closeConnection();
        return result;
    }

    public List<Billing> getBillings(String token) throws SQLException {
        int userid = getUser(token);
        if (userid == -1){
            throw new SQLInvalidAuthorizationSpecException();
        }

        openConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = conn.prepareStatement(Queries.GET_BILLINGS);
        preparedStatement.setInt(1, userid);
        ResultSet rs = preparedStatement.executeQuery();

        List<Billing> result = new ArrayList<>();
        while (rs.next()) {
            Billing tmp = new Billing();
            tmp.setUserid(rs.getInt("USERID"));
            tmp.setId(rs.getInt("ID"));
            tmp.setCost(rs.getInt("COST"));
            result.add(tmp);
        }

        preparedStatement.close();
        closeConnection();
        return result;
    }

    public List<Goods> getGoodsInOrder(String token, int orderid) throws SQLException {
        int userid = getUser(token);
        if (userid == -1){
            throw new SQLInvalidAuthorizationSpecException();
        }

        if (!checkOrder(userid, orderid)) {
            throw new SQLClientInfoException();
        }

        openConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = conn.prepareStatement(Queries.GET_GOODS_IN_ORDER);
        preparedStatement.setInt(1, orderid);
        ResultSet rs = preparedStatement.executeQuery();

        List<Goods> result = new ArrayList<>();
        while (rs.next()) {
            int goodsid = rs.getInt("GOODID");
            PreparedStatement preparedStatement2 = null;
            preparedStatement2 = conn.prepareStatement(Queries.GET_GOODS_BY_ID);
            preparedStatement2.setInt(1, goodsid);
            ResultSet rs2 = preparedStatement2.executeQuery();
            while (rs2.next()) {
                Goods tmp = new Goods();
                tmp.setId(rs2.getInt("ID"));
                tmp.setName(rs2.getString("NAME"));
                tmp.setDescription(rs2.getString("DESCRIPTION"));
                tmp.setCost(rs2.getInt("COST"));
                result.add(tmp);
            }
            preparedStatement2.close();
        }

        preparedStatement.close();
        closeConnection();
        return result;
    }

    public void deleteOrder(String token, int orderid) throws SQLException {
        int userid = getUser(token);
        if (userid == -1){
            throw new SQLInvalidAuthorizationSpecException();
        }

        if (!checkOrder(userid, orderid)) {
            throw new SQLClientInfoException();
        }

        openConnection();
        PreparedStatement preparedStatement = null;

        preparedStatement = conn.prepareStatement(Queries.DELETE_BILLING);
        preparedStatement.setInt(1, orderid);
        preparedStatement.executeUpdate();
        preparedStatement.close();

        preparedStatement = conn.prepareStatement(Queries.DELETE_ALL_GOODS_FROM_ORDER);
        preparedStatement.setInt(1, orderid);
        preparedStatement.executeUpdate();
        preparedStatement.close();

        preparedStatement = conn.prepareStatement(Queries.DELETE_ORDER);
        preparedStatement.setInt(1, orderid);
        preparedStatement.execute();
        preparedStatement.close();
        closeConnection();
    }

    public void deleteGoodsFromOrder(String token, int orderid, int goodsid) throws SQLException {
        int userid = getUser(token);
        if (userid == -1){
            throw new SQLInvalidAuthorizationSpecException();
        }

        if (!checkOrder(userid, orderid)){
            throw new SQLClientInfoException();
        }

        openConnection();
        PreparedStatement preparedStatement = null;

        preparedStatement = conn.prepareStatement(Queries.DELETE_GOODS_FROM_ORDER);
        preparedStatement.setInt(1, orderid);
        preparedStatement.setInt(2, goodsid);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        closeConnection();
    }
}

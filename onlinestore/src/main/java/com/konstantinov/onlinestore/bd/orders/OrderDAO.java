package com.konstantinov.onlinestore.bd.orders;

import com.konstantinov.onlinestore.bd.goods.CakeEntity;
import com.konstantinov.onlinestore.bd.goods.CompositionEntity;
import com.konstantinov.onlinestore.bd.users.UserEntity;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderDAO {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";

    private static Connection connection;
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public OrderEntity getById(Long id){
        OrderEntity orderEntity = new OrderEntity();
        try {
            Statement statement = connection.createStatement();
            String SQL = "SELECT oi.id o_id, oi.address o_address,oi.date o_date, oi.delivery o_delivery, oi.payment o_payment,\n" +
                    "       oi.status o_status, ui.id u_id, ui.name u_name, ui.surname u_surname, ui.number u_number,\n" +
                    "oc.cake_id, oc.count\n" +
                    "FROM order_info oi INNER JOIN user_info ui on oi.user_id = ui.id AND oi.id=" + id +"\n" +
                    "INNER JOIN order_cake oc on oi.id = oc.order_id";
            ResultSet resultSet = statement.executeQuery(SQL);
            if(!resultSet.next())
                return null;
            orderEntity.setId(id);
            orderEntity.setAddress(resultSet.getString("o_address"));
            orderEntity.setDate(LocalDate.parse(resultSet.getString("o_date")));
            orderEntity.setPayment(PaymentMethod.values()[resultSet.getInt("o_payment")]);
            orderEntity.setStatus(OrderStatus.values()[resultSet.getInt("o_status")]);
            orderEntity.setDelivery(DeliveryMethod.values()[resultSet.getInt("o_delivery")]);
            UserEntity user = new UserEntity();
            user.setId(resultSet.getLong("u_id"));
            user.setName(resultSet.getString("u_name"));
            user.setNumber(resultSet.getString("u_number"));
            user.setSurname(resultSet.getString("u_surname"));
            orderEntity.setUser(user);
            Map<Long, Integer> cakeCount = new HashMap<>();
            cakeCount.put(resultSet.getLong("cake_id"), resultSet.getInt("count"));
            while(resultSet.next())
                cakeCount.put(resultSet.getLong("cake_id"), resultSet.getInt("count"));
            SQL = "SELECT cake.id id_cake, cake.name name_cake, calories, image,\n" +
                    "       price, weight, description, c.id comp_id, c.name comp_name\n" +
                    "FROM cake\n" +
                    "INNER JOIN cake_composition cc on cake.id = cc.cake_id\n" +
                    "INNER JOIN composition c on c.id = cc.composition_id" +
                    " WHERE cake.id IN (" + cakeCount.keySet().stream().map(Object::toString).collect(Collectors.joining(",")) + ");";
            resultSet = statement.executeQuery(SQL);
            Map<Long, CakeEntity> map = new HashMap<>();
            while (resultSet.next()){
                CakeEntity cakeEntity;
                Long idCake = resultSet.getLong("id_cake");
                if(!map.containsKey(idCake)){
                    cakeEntity = new CakeEntity();
                    mapEntityInfo(cakeEntity, resultSet);
                    map.put(idCake, cakeEntity);
                } else {
                    cakeEntity = map.get(idCake);
                }
                CompositionEntity composition = new CompositionEntity();
                composition.setName(resultSet.getString("comp_name"));
                composition.setId(resultSet.getLong("comp_id"));
                cakeEntity.getComposition().add(composition);
            }
            orderEntity.setCakes(
                    map.values().stream().map(c ->{
                        OrderCakeEntity oc = new OrderCakeEntity();
                        oc.setCake(c);
                        oc.setCount(cakeCount.get(c.getId()));
                        return oc;
                    }).collect(Collectors.toList())
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderEntity;
    }
    private void mapEntityInfo(CakeEntity cakeEntity, ResultSet resultSet) throws SQLException {
        cakeEntity.setName(resultSet.getString("name_cake"));
        cakeEntity.setId(resultSet.getLong("id_cake"));
        cakeEntity.setCalories(resultSet.getBigDecimal("calories"));
        cakeEntity.setImage(resultSet.getString("image"));
        cakeEntity.setWeight(resultSet.getBigDecimal("weight"));
        cakeEntity.setDescription(resultSet.getString("description"));
        cakeEntity.setPrice(resultSet.getBigDecimal("price"));
    }
}

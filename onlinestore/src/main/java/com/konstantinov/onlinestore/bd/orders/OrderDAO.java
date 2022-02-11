package com.konstantinov.onlinestore.bd.orders;

import com.konstantinov.onlinestore.bd.goods.CakeEntity;
import com.konstantinov.onlinestore.bd.goods.CompositionEntity;
import com.konstantinov.onlinestore.bd.users.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class OrderDAO {
    public final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public OrderEntity save(OrderEntity orderEntity) {
        if(orderEntity.getId() == null)
            return create(orderEntity);
        return update(orderEntity);
    }

    private OrderEntity create(OrderEntity orderEntity) {
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        String SQL_final = "INSERT INTO order_info (user_id, delivery, payment, status, date, address) VALUES " +
                "(?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(c -> {
            PreparedStatement statement = c.prepareStatement(SQL_final, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, orderEntity.getUser().getId());
            statement.setInt(2, orderEntity.getDelivery().ordinal());
            statement.setInt(3, orderEntity.getPayment().ordinal());
            statement.setInt(4, orderEntity.getStatus().ordinal());
            statement.setDate(5, java.sql.Date.valueOf(orderEntity.getDate()));
            statement.setString(6, orderEntity.getAddress());
            return statement;
        }, holder);
        String inSql = String.join(",", Collections.nCopies(orderEntity.getCakes().size(), "(?,?,?)"));
        orderEntity.setId((Long) holder.getKeys().get("id"));
        String SQL = String.format("INSERT INTO order_cake (order_id, cake_id, count) VALUES %s", inSql);
        jdbcTemplate.update(SQL,
                orderEntity
                        .getCakes()
                        .stream()
                        .flatMap(orderCakeEntity ->
                                Stream.of(orderEntity.getId(),
                                        orderCakeEntity.getCake().getId(),
                                        orderCakeEntity.getCount()
                                )
                        ).toArray());
        return orderEntity;
    }

    private OrderEntity update(OrderEntity orderEntity) {
        String SQL = "UPDATE order_info SET address=?, date=?, delivery=?, payment=?, status=?, user_id=? WHERE id=?";
        jdbcTemplate.update(SQL, orderEntity.getAddress(), java.sql.Date.valueOf(orderEntity.getDate()),
                orderEntity.getDelivery().ordinal(), orderEntity.getPayment().ordinal(), orderEntity.getStatus().ordinal(),
                orderEntity.getUser().getId(), orderEntity.getId());
        LinkedList<Long> cakesThatAvailable = orderEntity
                .getCakes()
                .stream()
                .map(orderCakeEntity -> orderCakeEntity.getCake().getId())
                .collect(Collectors.toCollection(LinkedList::new));
        String inSql = String.join(",", Collections.nCopies(cakesThatAvailable.size(), "?"));
        SQL = String.format("DELETE FROM order_cake WHERE order_id=? AND cake_id NOT IN (%s)", inSql);
        cakesThatAvailable.addFirst(orderEntity.getId());
        jdbcTemplate.update(SQL, cakesThatAvailable.toArray());
        cakesThatAvailable.removeFirst();
        inSql = String.join(",", Collections.nCopies(cakesThatAvailable.size(), "(?,?,?)"));

        SQL = String.format("INSERT INTO order_cake (count, cake_id, order_id) VALUES %s ON CONFLICT ON CONSTRAINT order_cake_pk DO UPDATE SET count=EXCLUDED.count", inSql);
        jdbcTemplate.update(SQL, orderEntity.getCakes()
                .stream()
                .flatMap(orderCakeEntity ->
                        Stream.of(orderCakeEntity.getCount(),
                                orderCakeEntity.getCake().getId(),
                                orderEntity.getId()
                        )
                ).toArray());
        return orderEntity;
    }

    public OrderEntity getById(Long id){
        String SQL = "SELECT oi.id oi_id, address, date, delivery, payment, " +
                "status, user_id, ui.name, ui.surname, ui.number, oc.id, oc.cake_id, count, " +
                "c.name, c.weight, c.price, c.image, c.description, c.calories, cc.composition_id, c2.name c_name FROM order_info oi " +
                "INNER JOIN order_cake oc on oi.id = oc.order_id AND oi.id=? " +
                "INNER JOIN user_info ui on oi.user_id = ui.id " +
                "INNER JOIN cake c on c.id = oc.cake_id " +
                "INNER JOIN cake_composition cc on c.id = cc.cake_id " +
                "INNER JOIN composition c2 on c2.id = cc.composition_id";
        return jdbcTemplate.query(SQL, rs -> {
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setCakes(new ArrayList<>());
            Map<Long, OrderCakeEntity> cakes = new HashMap<>();
            while (rs.next()){
                if(orderEntity.getId() == null){
                    mapOrderByRow(orderEntity, rs);
                }
                if(!cakes.containsKey(rs.getLong("id"))){
                    OrderCakeEntity orderCakeEntity = new OrderCakeEntity();
                    CakeEntity cake = new CakeEntity();
                    mapCakeByRow(cake, rs);
                    orderCakeEntity.setCake(cake);
                    orderCakeEntity.setOrder(orderEntity);
                    orderCakeEntity.setId(rs.getLong("id"));
                    orderCakeEntity.setCount(rs.getInt("count"));
                    cakes.put(rs.getLong("id"), orderCakeEntity);
                }
                CakeEntity cake = cakes.get(rs.getLong("id")).getCake();
                CompositionEntity composition = new CompositionEntity();
                composition.setId(rs.getLong("composition_id"));
                composition.setName(rs.getString("c_name"));
                cake.getComposition().add(composition);
            }
            cakes.forEach(((aLong, orderCakeEntity) -> orderEntity.getCakes().add(orderCakeEntity)));
            return orderEntity;
        }, id);
    }

    public List<OrderEntity> findByUser_Number(String number){
        String condition = (number != null) ? "\nWHERE ui.number='" + number + "'" : "";
        String SQL = "SELECT oi.id oi_id, address, date, delivery, payment, " +
                "status, user_id, ui.name, ui.surname, ui.number, oc.id, oc.cake_id, count, " +
                "c.name, c.weight, c.price, c.image, c.description, c.calories, cc.composition_id, c2.name c_name FROM order_info oi " +
                "INNER JOIN order_cake oc on oi.id = oc.order_id " +
                "INNER JOIN user_info ui on oi.user_id = ui.id " +
                "INNER JOIN cake c on c.id = oc.cake_id " +
                "INNER JOIN cake_composition cc on c.id = cc.cake_id " +
                "INNER JOIN composition c2 on c2.id = cc.composition_id " + condition;
        return jdbcTemplate.query(SQL, rs -> {
            Map<Long, OrderEntity> orderEntityMap = new HashMap<>();
            Map<Long, OrderCakeEntity> orderCakeEntityMap = new HashMap<>();
            while (rs.next()){
                if (!orderEntityMap.containsKey(rs.getLong("oi_id"))){
                    OrderEntity order = new OrderEntity();
                    mapOrderByRow(order, rs);
                    orderEntityMap.put(rs.getLong("oi_id"), order);
                }
                if(!orderCakeEntityMap.containsKey(rs.getLong("id"))){
                    OrderCakeEntity orderCakeEntity = new OrderCakeEntity();
                    CakeEntity cake = new CakeEntity();
                    mapCakeByRow(cake, rs);
                    orderCakeEntity.setCake(cake);
                    orderCakeEntity.setOrder(orderEntityMap.get(rs.getLong("oi_id")));
                    orderCakeEntity.setId(rs.getLong("id"));
                    orderCakeEntity.setCount(rs.getInt("count"));
                    orderCakeEntityMap.put(rs.getLong("id"), orderCakeEntity);
                    orderEntityMap.get(rs.getLong("oi_id")).getCakes().add(orderCakeEntity);
                }
                CakeEntity cake = orderCakeEntityMap.get(rs.getLong("id")).getCake();
                CompositionEntity composition = new CompositionEntity();
                composition.setId(rs.getLong("composition_id"));
                composition.setName(rs.getString("c_name"));
                cake.getComposition().add(composition);
            }
            return new ArrayList<>(orderEntityMap.values());
        });
    }

    public List<OrderEntity> findAll(){
        return findByUser_Number(null);
    }

    private void mapCakeByRow(CakeEntity cake, ResultSet rs) throws SQLException {
        cake.setId(rs.getLong("cake_id"));
        cake.setWeight(rs.getBigDecimal("weight"));
        cake.setImage(rs.getString("image"));
        cake.setName(rs.getString("name"));
        cake.setCalories(rs.getBigDecimal("calories"));
        cake.setDescription(rs.getString("description"));
        cake.setPrice(rs.getBigDecimal("price"));
        CompositionEntity composition = new CompositionEntity();
        composition.setId(rs.getLong("composition_id"));
    }

    private void mapOrderByRow(OrderEntity orderEntity, ResultSet rs) throws SQLException {
        orderEntity.setId(rs.getLong("oi_id"));
        orderEntity.setStatus(OrderStatus.values()[rs.getInt("status")]);
        orderEntity.setDelivery(DeliveryMethod.values()[rs.getInt("delivery")]);
        orderEntity.setPayment(PaymentMethod.values()[rs.getInt("payment")]);
        orderEntity.setDate(LocalDate.parse(rs.getString("date")));
        orderEntity.setAddress(rs.getString("address"));
        UserEntity user = new UserEntity();
        user.setId(rs.getLong("user_id"));
        user.setName(rs.getString("name"));
        user.setSurname(rs.getString("surname"));
        user.setNumber(rs.getString("number"));
        orderEntity.setUser(user);
    }
}

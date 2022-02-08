package com.konstantinov.onlinestore.bd.goods;

import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CakeDAO {

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

    public CakeEntity getById(Long id){
        List<CakeEntity> cakes = new ArrayList<CakeEntity>();
        CakeEntity cakeEntity = new CakeEntity();
        Set<CompositionEntity> compositions = new HashSet<CompositionEntity>();
        try {
            Statement statement = connection.createStatement();
            String SQL = "SELECT cake.ID id_cake, cake.NAME name_cake, CALORIES, IMAGE, PRICE, WEIGHT, DESCRIPTION, c.id comp_id, c.name comp_name FROM cake\n" +
                    "INNER JOIN cake_composition cc on cake.id = cc.cake_id AND cake.id = " + id + "\n" +
                    "INNER JOIN composition c on c.id = cc.composition_id;";
            ResultSet resultSet = statement.executeQuery(SQL);
            boolean cakeInfoIsMapped = false;
            while (resultSet.next())
            {
                if(!cakeInfoIsMapped){
                    mapEntityInfo(cakeEntity, resultSet);
                    cakeInfoIsMapped = true;
                }
                CompositionEntity composition = new CompositionEntity();
                composition.setName(resultSet.getString("comp_name"));
                composition.setId(resultSet.getLong("comp_id"));
                compositions.add(composition);
            }
            cakeEntity.setComposition(compositions);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cakeEntity;
    }

    public List<CakeEntity> findAll(Integer page, Integer size){
        Map<Long, CakeEntity> map = new HashMap<Long, CakeEntity>();
        try {
            Statement statement = connection.createStatement();
            String SQL = "SELECT cake.ID id_cake, cake.NAME name_cake, CALORIES, IMAGE, PRICE, WEIGHT, DESCRIPTION, c.id comp_id, c.name comp_name FROM cake\n" +
                    "INNER JOIN cake_composition cc on cake.id = cc.cake_id\n" +
                    "INNER JOIN composition c on c.id = cc.composition_id;";
            ResultSet resultSet = statement.executeQuery(SQL);
            while (resultSet.next())
            {
                CakeEntity cakeEntity;
                Long id = resultSet.getLong("id_cake");
                if(!map.containsKey(id)){
                    cakeEntity = new CakeEntity();
                    mapEntityInfo(cakeEntity, resultSet);
                    map.put(id, cakeEntity);
                } else {
                    cakeEntity = map.get(id);
                }
                CompositionEntity composition = new CompositionEntity();
                composition.setName(resultSet.getString("comp_name"));
                composition.setId(resultSet.getLong("comp_id"));
                cakeEntity.getComposition().add(composition);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<CakeEntity>(map.values());
    }

    public void save(CakeEntity cakeEntity){
        try {
            if(cakeEntity.getId() == null)
                create(cakeEntity);
            else
                update(cakeEntity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void create(CakeEntity cakeEntity) throws SQLException {
        Statement statement = connection.createStatement();
        //List<Long> compId = addComposition(cakeEntity.getComposition());
        String SQL = "SELECT * FROM cake\n" +
                "ORDER BY id DESC\n" +
                "LIMIT 1";
        ResultSet resultSet = statement.executeQuery(SQL);
        resultSet.next();
        Long id = resultSet.getLong("id") + 1;
        List<Long> compId = getNotAddedCompositions(cakeEntity
                        .getComposition()
                        .stream()
                        .map(CompositionEntity::getId)
                        .collect(Collectors.toList()), id
                );
        SQL = "INSERT INTO cake (id, name, calories, image, price, weight, description) " +
                "VALUE (" + id + ",'" + cakeEntity.getName() + "'," + cakeEntity.getCalories() + ",'" +
                cakeEntity.getImage() + "'," + cakeEntity.getPrice() + "," + cakeEntity.getWeight() + ",'" +
                cakeEntity.getDescription() + "')";
        resultSet = statement.executeQuery(SQL);
        SQL = "INSERT INTO cake_composition (cake_id, composition_id) VALUES " + compId.stream().map(c -> "(" + id + "," + c).collect(Collectors.joining(","));
        resultSet = statement.executeQuery(SQL);
    }

    private void update(CakeEntity cakeEntity) throws SQLException {
        Statement statement = connection.createStatement();
        //List<Long> compId = addComposition(cakeEntity.getComposition());
        List<Long> compId = getNotAddedCompositions(cakeEntity
                .getComposition()
                .stream()
                .map(CompositionEntity::getId)
                .collect(Collectors.toList()), cakeEntity.getId()
        );

        String SQL = "UPDATE cake \n " +
                "SET name='" + cakeEntity.getName() + "',calories=" + cakeEntity.getCalories() + ",image='" + cakeEntity.getImage() +
                "',price=" + cakeEntity.getPrice() + ",weight=" + cakeEntity.getWeight() + ",description='" + cakeEntity.getDescription() + "'" +
                "\nWHERE id=" + cakeEntity.getId();
        statement.executeUpdate(SQL);
        SQL = "INSERT INTO cake_composition (cake_id, composition_id) VALUES " + compId.stream().map(c -> "(" + cakeEntity.getId() + "," + c + ")").collect(Collectors.joining(","));
        statement.executeUpdate(SQL);
    }

    private List<Long> getNotAddedCompositions(List<Long> comp, Long id) throws SQLException {
        Statement statement = connection.createStatement();
        String SQL = "SELECT * FROM cake_composition\n" +
                    "WHERE cake_id =" + id + ";";
        ResultSet resultSet = statement.executeQuery(SQL);
        List<Long> composition = new ArrayList<>();
        while (resultSet.next())
        {
            composition.add(resultSet.getLong("composition_id"));
        }
        comp.removeAll(composition);
        return comp;
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

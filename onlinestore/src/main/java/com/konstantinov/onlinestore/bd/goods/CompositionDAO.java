package com.konstantinov.onlinestore.bd.goods;

import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class CompositionDAO{
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

    public CompositionEntity findByName(String name){
        CompositionEntity composition = new CompositionEntity();
        try {
            Statement statement = connection.createStatement();
            String SQL = "SELECT id, name FROM composition\n" +
                    "WHERE name = '" + name + "';";
            ResultSet resultSet = statement.executeQuery(SQL);
            if(!resultSet.next()){
                return null;
            }
            composition.setName(resultSet.getString("name"));
            composition.setId(resultSet.getLong("id"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return composition;
    }

    public CompositionEntity save(CompositionEntity composition){
        try {
            Statement statement = connection.createStatement();
            String SQL = "SELECT * FROM composition\n" +
                    "ORDER BY id DESC\n" +
                    "LIMIT 1";
            ResultSet resultSet = statement.executeQuery(SQL);
            resultSet.next();
            Long id = resultSet.getLong("id") + 1;
            composition.setId(id);
            SQL = "INSERT INTO composition (id, name)\n " +
                    "VALUES (" + id + ",'" + composition.getName() + "');";
            statement.executeQuery(SQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return composition;
    }
}

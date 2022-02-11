package com.konstantinov.onlinestore.bd.users;

import com.konstantinov.onlinestore.rest.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Component
public class UserDAO {
    public final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public UserEntity getById(Long id){
        String SQL = "SELECT * FROM user_info WHERE id=?";
        return jdbcTemplate.query(SQL, new BeanPropertyRowMapper<>(UserEntity.class), id).stream().findAny().orElse(null);
    }

    public UserEntity findByNumber(String number){
        String SQL = "SELECT * FROM user_info WHERE number=?";
        return jdbcTemplate.query(SQL, new BeanPropertyRowMapper<>(UserEntity.class), number).stream().findAny().orElse(null);
    }

    public List<UserEntity> findAll(){
        String SQL = "SELECT * FROM user_info";
        return jdbcTemplate.query(SQL, new BeanPropertyRowMapper<>(UserEntity.class));
    }

    public UserEntity save(UserEntity user){
        if(user.getId() == null)
            return create(user);
        return update(user);
    }

    private UserEntity create(UserEntity user){
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        String SQL = "INSERT INTO user_info(name, number, surname) VALUES (?,?,?)";
        jdbcTemplate.update(c -> {
            PreparedStatement statement = c.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getName());
            statement.setString(2, user.getNumber());
            statement.setString(3, user.getSurname());
            return statement;
        }, holder);
        user.setId((Long) holder.getKeys().get("id"));
        return user;
    }

    private UserEntity update(UserEntity user){
        String SQL = "UPDATE user_info SET name=?, number=?, surname=? WHERE id=?";
        jdbcTemplate.update(SQL, user.getName(), user.getNumber(), user.getSurname(), user.getId());
        return user;
    }
}

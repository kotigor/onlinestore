package com.konstantinov.onlinestore.bd.goods;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CakeDAO {
    public final JdbcTemplate jdbcTemplate;

    @Autowired
    public CakeDAO(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CakeEntity> findAll(){
        String SQL = "SELECT cake.id id_cake, cake.name name_cake,\n" +
                "       calories, description, image, price,\n" +
                "       weight, c.name, c.id\n" +
                "       FROM cake\n" +
                "INNER JOIN cake_composition cc on cake.id = cc.cake_id\n" +
                "INNER JOIN composition c on c.id = cc.composition_id;";
        List<CakeEntity> cakeEntities = jdbcTemplate.query(SQL, new ResultSetExtractor<List<CakeEntity>>() {
            @Override
            public List<CakeEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
                Map<Long, CakeEntity> map = new HashMap<>();
                while (rs.next()){
                    Long cakeId = rs.getLong("id_cake");
                    if(!map.containsKey(cakeId)){
                        CakeEntity cakeEntity = mapCakeInfo(rs);
                        map.put(cakeId, cakeEntity);
                    }
                    map.get(cakeId).getComposition().add(mapCompositionInfo(rs));
                }
                return map.values().stream().collect(Collectors.toList());
            }
        });
        return cakeEntities;
    }

    public CakeEntity getById(Long id){
        String SQL = "SELECT cake.id id_cake, cake.name name_cake,\n" +
                "       calories, description, image, price,\n" +
                "       weight, c.name, c.id\n" +
                "       FROM cake\n" +
                "INNER JOIN cake_composition cc on cake.id = cc.cake_id AND cake.id=? \n" +
                "INNER JOIN composition c on c.id = cc.composition_id;";
        CakeEntity cakeEntity = jdbcTemplate.query(SQL, new ResultSetExtractor<CakeEntity>() {
            @Override
            public CakeEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
                rs.next();
                CakeEntity cake = mapCakeInfo(rs);
                do{
                    cake.getComposition().add(mapCompositionInfo(rs));
                }
                while (rs.next());
                return cake;
            }
        }, id);
        return cakeEntity;
    }

    public CakeEntity save(CakeEntity cakeEntity){
        if(cakeEntity.getId() == null)
            return create(cakeEntity);
        return update(cakeEntity);
    }

    private CakeEntity create(CakeEntity cakeEntity){
        Long id = jdbcTemplate.query("SELECT id FROM cake ORDER BY id DESC LIMIT 1", new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                rs.next();
                return rs.getLong("id");
            }
        });
        id++;
        jdbcTemplate.update("INSERT INTO cake (id, name, calories, image, price, weight, description) VALUES (?,?,?,?,?,?,?)",
                id, cakeEntity.getName(), cakeEntity.getCalories(), cakeEntity.getImage(),
                cakeEntity.getPrice(), cakeEntity.getWeight(), cakeEntity.getDescription());
        String inSql = String.join(",", Collections.nCopies(cakeEntity.getComposition().size(), "(" + id +",?)"));
        jdbcTemplate.update(String.format("INSERT INTO cake_composition VALUES %s", inSql), cakeEntity.getComposition().toArray());
        cakeEntity.setId(id);
        return cakeEntity;
    }

    private CakeEntity update(CakeEntity cakeEntity){
        List<Long> oldComp = jdbcTemplate.query("SELECT * FROM cake_composition WHERE cake_id=?", new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong("composition_id");
            }
        }, cakeEntity.getId());
        List<Long> toDelete = new ArrayList<>(oldComp);
        toDelete.removeAll(cakeEntity.getComposition().stream().map(CompositionEntity::getId).collect(Collectors.toList()));
        List<Long> toAdd = cakeEntity.getComposition().stream().map(CompositionEntity::getId).collect(Collectors.toList());
        toAdd.removeAll(oldComp);
        jdbcTemplate.update("UPDATE cake SET name=?, calories=?, description=?, image=?, price=?, weight=? WHERE id=?",
                cakeEntity.getName(), cakeEntity.getCalories(), cakeEntity.getDescription(), cakeEntity.getImage(),
                cakeEntity.getPrice(), cakeEntity.getWeight(), cakeEntity.getId());
        String inSql = String.join(",", Collections.nCopies(toDelete.size(), "?"));
        jdbcTemplate.update(String.format("DELETE FROM cake_composition WHERE composition_id IN (%s)",inSql),
                toDelete.toArray());
        inSql = String.join(",", Collections.nCopies(toAdd.size(), "(?)"));
        jdbcTemplate.update(String.format("INSERT INTO cake_composition VALUES %s", inSql), toAdd.toArray());
        return cakeEntity;
    }

    private CakeEntity mapCakeInfo(ResultSet rs) throws SQLException {
        CakeEntity cakeEntity = new CakeEntity();
        cakeEntity.setCalories(rs.getBigDecimal("calories"));
        cakeEntity.setPrice(rs.getBigDecimal("price"));
        cakeEntity.setName(rs.getString("name_cake"));
        cakeEntity.setDescription(rs.getString("description"));
        cakeEntity.setWeight(rs.getBigDecimal("weight"));
        cakeEntity.setImage(rs.getString("image"));
        cakeEntity.setId(rs.getLong("id_cake"));
        return cakeEntity;
    }

    private CompositionEntity mapCompositionInfo(ResultSet rs) throws SQLException {
        CompositionEntity composition = new CompositionEntity();
        composition.setId(rs.getLong("id"));
        composition.setName(rs.getString("name"));
        return composition;
    }

}

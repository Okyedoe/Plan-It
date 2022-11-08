package com.example.demo.src.keyword;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class KeyWordDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public List<String> getKeyWordList(int journey_id) {
        List<String> tmp = new ArrayList<>();
        try {
            String sql = "select name from keywords where status = 1 and journey_id = ? ";
            tmp = this.jdbcTemplate.queryForList(sql, String.class, journey_id);
        }catch (NullPointerException exception){
            tmp.add("");
        }
        return tmp;
    }
}

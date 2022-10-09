package com.example.demo.src.planet;

import com.example.demo.src.planet.model.GetPlanetsRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PlanetDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }




    //journey_id를 이용하여 해당 유저아이디 가져오기
    public int getUser_id(int journey_id)
    {
        String getUseridQuery = "select user_id from journey where journey_id = ?";
        int user_id = this.jdbcTemplate.queryForObject(getUseridQuery,int.class,journey_id);
        return user_id;
    }

    //행성목록 가져오기
    public List<GetPlanetsRes> getPlanets (int journey_id)
    {
        //status가 0인 -> 삭제된 행성은 가져오지않는다.
        String getPlanetsQuery = "select * from planet where journey_id = ?";

        return this.jdbcTemplate.query(getPlanetsQuery,(rs, rowNum) -> new GetPlanetsRes(
                        rs.getString("planet_name"),
                        rs.getString("planet_intro"),
                        rs.getInt("planet_exp"),
                        rs.getInt("planet_level"),
                        rs.getString("planet_image")
                ),journey_id);

    }





}

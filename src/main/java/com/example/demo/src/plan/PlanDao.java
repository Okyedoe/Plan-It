package com.example.demo.src.plan;
import com.example.demo.src.journey.model.PostJourneyReq;
import com.example.demo.src.journey.model.PostJourneyRes;
import com.example.demo.src.plan.model.PostPlanReq;
import com.example.demo.src.plan.model.PostPlanRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;



@Repository
public class PlanDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    //행성아이디를 이용하여 행성의 주인 == 유저아이디를 받아오는 명령어
    public int getUser_id_from_planet_id (int planet_id)
    {
        String getQuery = "select user_id from planet\n" +
                "left join journey j on planet.journey_id = j.journey_id\n" +
                "where planet_id = ?";
        int user_id = this.jdbcTemplate.queryForObject(getQuery,int.class,planet_id);
        return user_id;
    }


    //행성 세부계획 추가
    @Transactional
    public PostPlanRes createPlan (PostPlanReq postPlanReq ,int planet_id )
    {
        String createQuery = "insert into detailed_plan(planet_id,plan_content,type) VALUES(?,?,?)";
        Object[] createParams = new Object[]{planet_id,postPlanReq.getPlan_content(),postPlanReq.getType()};
        this.jdbcTemplate.update(createQuery,createParams);
        String peekQuery = "select last_insert_id()";
        int last_in_detailed_plan_id = this.jdbcTemplate.queryForObject(peekQuery,int.class);

        String getInfo = "select * from detailed_plan where detailed_plan_id = ?";
        return this.jdbcTemplate.queryForObject(getInfo,(rs, rowNum) -> new PostPlanRes(
                rs.getInt("detailed_plan_id"),
                rs.getInt("planet_id"),
                rs.getString("plan_content"),
                rs.getString("type")

        ),last_in_detailed_plan_id );
    }



}

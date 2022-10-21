package com.example.demo.src.planet;

import com.example.demo.src.planet.model.GetDetailedInfoRes;
import com.example.demo.src.planet.model.GetPlanetsRes;
import com.example.demo.src.planet.model.PostNewPlanetReq;
import com.example.demo.src.planet.model.PostNewPlanetRes;
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
        String getPlanetsQuery = "select * from planet where journey_id = ? and status = 1";

        return this.jdbcTemplate.query(getPlanetsQuery,(rs, rowNum) -> new GetPlanetsRes(
                        rs.getInt("planet_id"),
                        rs.getString("planet_name"),
                        rs.getString("planet_intro"),
                        rs.getInt("planet_exp"),
                        rs.getInt("planet_level"),
                        rs.getString("planet_image")
                ),journey_id);

    }

    //행성 세부계획 가져오기
    public GetDetailedInfoRes getDetailedInfo (int planet_id)
    {
        String getDetailedQuery = "select * from planet where planet_id = ?";
        GetDetailedInfoRes getDetailedInfoRes =this.jdbcTemplate.queryForObject(getDetailedQuery,(rs, rowNum) -> new GetDetailedInfoRes(
                        rs.getInt("planet_id"),
                        rs.getString("planet_name"),
                        rs.getString("planet_intro"),
                        rs.getInt("planet_exp"),
                        rs.getInt("planet_level"),
                        rs.getString("planet_image")
                ),planet_id
                );

        String getPlansQuery = "select plan_content,type,status from detailed_plan where planet_id = ?";
        List<GetDetailedInfoRes.Plans> plans = this.jdbcTemplate.query(getPlansQuery,(rs, rowNum) ->new GetDetailedInfoRes.Plans(
                rs.getString("plan_content"),
                rs.getString("type"),
                rs.getBoolean("status")
        ),planet_id);

        getDetailedInfoRes.setPlans(plans);
        return getDetailedInfoRes;
    }


    //새 행성 만들기
    @Transactional
    public PostNewPlanetRes createNewPlanet (PostNewPlanetReq postNewPlanetReq,int journey_id)
    {
        //행성추가 및 행성 세부계획 추가
        String addPlanetQuery = "insert into planet(journey_id,planet_name) VALUES(?,?)";
        String addPlanQuery = "insert into detailed_plan(planet_id,plan_content) VALUES(?,?)";

        //행성추가해주고, 행성 아이디를 받아온다.
        Object[] addPlanetParams = new Object[]{journey_id,postNewPlanetReq.getPlanet_name()};
        this.jdbcTemplate.update(addPlanetQuery,addPlanetParams); // 행성추가
        String peekQuery = "select last_insert_id()";
        int planet_id = this.jdbcTemplate.queryForObject(peekQuery,int.class); //방금 추가된 행성아이디

        //해당 행성 세부계획 추가
        List<String> detailed_plans = postNewPlanetReq.getDetailed_plans();
        for(int i=0;i<detailed_plans.size();i++)
        {
            Object[] params = new Object[]{planet_id,detailed_plans.get(i)};
            this.jdbcTemplate.update(addPlanQuery,params);
        }

        //쿼리로 받아와야하지만 일단 야매로.
        PostNewPlanetRes postNewPlanetRes = new PostNewPlanetRes();
        postNewPlanetRes.setPlanet_name(postNewPlanetReq.getPlanet_name());
        postNewPlanetRes.setPlanet_id(planet_id);
        postNewPlanetRes.setDetailed_plans(postNewPlanetReq.getDetailed_plans());

        return postNewPlanetRes;



    }


    //행성 삭제 status -> 0으로 바꿔줌
    @Transactional
    public String deletePlanet(int planet_id)
    {
        String deletQuery = "update planet set status = 0 where planet_id = ?";
        this.jdbcTemplate.update(deletQuery,planet_id);
        String checkstatus = "select status from planet where planet_id =?";
        //수정한 결과를 다시 sql로 받아본다.
        int planet_status = this.jdbcTemplate.queryForObject(checkstatus,int.class,planet_id);
        String result = "행성아이디 : "+planet_id + " status 상태 : " + planet_status;
        return result;
    }

    //해당 행성이 삭제된행성인지 체크
    public int checkPlanet (int planet_id)
    {
        String checkQuery = "select status from planet where planet_id = ?";
        return this.jdbcTemplate.queryForObject(checkQuery,int.class,planet_id);
    }

    //해당 여정이 끝났는지 체크
    public int checkJourney (int journey_id)
    {
        String checkQuery = "select status from joruney where joruney_id = ?";
        return this.jdbcTemplate.queryForObject(checkQuery,int.class,journey_id);
    }

    //행성이름중복 체크
    public int checkPlanetExist(String planet_name)
    {
        String a = "select count(*) from planet where planet_name =?";
        return this.jdbcTemplate.queryForObject(a,int.class,planet_name);

    }







}

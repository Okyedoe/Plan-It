package com.example.demo.src.planet;

import static com.example.demo.config.BaseResponseStatus.*;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.planet.model.GetDetailedInfoRes;
import com.example.demo.src.planet.model.GetPlanetsRes;
import com.example.demo.src.planet.model.PatchRevisePlanetInforReq;
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
        String getPlanetsQuery =
            "select planet_id,planet_name,planet_intro,planet_exp,planet_level,pc.color from planet\n"
                + "join planet_color pc on planet.status = pc.status\n"
                + "where journey_id = ? and planet.status = 1 and planet_name != '해당없음'\n";

        return this.jdbcTemplate.query(getPlanetsQuery, (rs, rowNum) -> new GetPlanetsRes(
            rs.getInt("planet_id"),
            rs.getString("planet_name"),
            rs.getString("planet_intro"),
            rs.getInt("planet_exp"),
            rs.getInt("planet_level"),
            rs.getString("color")
        ), journey_id);

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

        String getPlansQuery = "select plan_content,type,status,is_completed from detailed_plan where planet_id = ?";
        List<GetDetailedInfoRes.Plans> plans = this.jdbcTemplate.query(getPlansQuery,(rs, rowNum) ->new GetDetailedInfoRes.Plans(
                rs.getString("plan_content"),
                rs.getString("type"),
                rs.getInt("status"),
            rs.getInt("is_completed")
        ),planet_id);

        getDetailedInfoRes.setPlans(plans);
        return getDetailedInfoRes;
    }


    //새 행성 만들기
    @Transactional
    public PostNewPlanetRes createNewPlanet (PostNewPlanetReq postNewPlanetReq,int journey_id)
    {
        //행성컬러를 이용하여 컬러 번호를 받아온다
        String getColorId = "select planet_color_id\n"
            + "from planet_color\n"
            + "where color = ?;";
        int color_id = this.jdbcTemplate.queryForObject(getColorId, int.class,
            postNewPlanetReq.getColor());

        String planet_intro = "";

        if (postNewPlanetReq.getPlanet_intro() != null) {
            if (postNewPlanetReq.getPlanet_intro().length() != 0) {
                planet_intro = postNewPlanetReq.getPlanet_intro();
            }
        }

        //행성추가 및 행성 세부계획 추가
        String addPlanetQuery = "insert into planet(journey_id,planet_name,planet_intro,color_id) VALUES(?,?,?,?)";
        String addPlanQuery = "insert into detailed_plan(planet_id,plan_content) VALUES(?,?)";

        //행성추가해주고, 행성 아이디를 받아온다.
        Object[] addPlanetParams = new Object[]{journey_id,postNewPlanetReq.getPlanet_name(),planet_intro,color_id};
        this.jdbcTemplate.update(addPlanetQuery,addPlanetParams); // 행성추가
        String peekQuery = "select last_insert_id()";
        int planet_id = this.jdbcTemplate.queryForObject(peekQuery,int.class); //방금 추가된 행성아이디

//        //해당 행성 세부계획 추가
//        List<String> detailed_plans = postNewPlanetReq.getDetailed_plans();
//        for(int i=0;i<detailed_plans.size();i++)
//        {
//            Object[] params = new Object[]{planet_id,detailed_plans.get(i)};
//            this.jdbcTemplate.update(addPlanQuery,params);
//        }

        //쿼리로 받아와야하지만 일단 야매로.
        PostNewPlanetRes postNewPlanetRes = new PostNewPlanetRes();
        postNewPlanetRes.setPlanet_name(postNewPlanetReq.getPlanet_name());
        postNewPlanetRes.setPlanet_id(planet_id);
        postNewPlanetRes.setColor(postNewPlanetReq.getColor());
        postNewPlanetRes.setPlanet_intro(planet_intro);
//        postNewPlanetRes.setDetailed_plans(postNewPlanetReq.getDetailed_plans());

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

    public List<String> getPlanetName(int journey_id){
        String sql = "select planet_name from planet where journey_id = ? and status = 1";
        int param = journey_id;
        return this.jdbcTemplate.queryForList(sql,String.class,param);
    }


    @Transactional
    public String revisePlanetInfo(int planet_id,
        PatchRevisePlanetInforReq patchRevisePlanetInforReq) throws BaseException {
        String reviseQuery = "update planet set planet_intro = ? where planet_id = ?";
        Object[] reviseQueryParams = new Object[]{patchRevisePlanetInforReq.getPlanet_intro(),
            planet_id};

        int result = this.jdbcTemplate.update(reviseQuery, reviseQueryParams);
        if (!(result == 1)) {
            throw new BaseException(UPDATE_FAILED);
        }

        return "성공";

    }


    //해당없음 행성의 아이디를 찾는 함수
    public int getNotBelongPlanetId(int journey_id) {
        String query = "select planet_id from planet where journey_id = ? and planet_name = '해당없음'";
        return this.jdbcTemplate.queryForObject(query, int.class, journey_id);

    }


    public int checkColorExist(String color) {
        String checkQuery = "select EXISTS(select planet_color_id from planet_color where color = ?);";
        int result = this.jdbcTemplate.queryForObject(checkQuery, int.class, color);
        return result;

    }








}

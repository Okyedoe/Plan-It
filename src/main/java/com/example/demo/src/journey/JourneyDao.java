package com.example.demo.src.journey;
import com.example.demo.src.journey.model.PostJourneyReq;
import com.example.demo.src.journey.model.PostJourneyRes;
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
public class JourneyDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    //여정 생성 api
    @Transactional
    public PostJourneyRes createJourney (PostJourneyReq postJourneyReq)
    {
        int period = postJourneyReq.getPeriod(); //기간
        String[] keywords = postJourneyReq.getKeywords(); //키워드
        List<PostJourneyReq.Planetinfo> planets = postJourneyReq.getPlanets(); // 행성 + 행성 세부계획

//        들어오는 내역 print 해보는 주석.
//        for(int a=0;a<planets.size();a++)
//        {
//            System.out.println(planets.get(a).getPlanet_name());
//
//            for(int b=0;b<planets.get(a).getDetailed_plans().size();b++)
//            {
//                System.out.println(planets.get(a).getDetailed_plans().get(b));
//            }
//
//        }
//        System.out.println("planets = " + planets.toString());
//        System.out.println("keywords = " + Arrays.toString(keywords));
//        System.out.println("period = " + period);


        //여정 데이터 추가
        String addJouneryQuery = "insert into journey(user_id,period) VALUES(?,?)";
        Object[] addJourneyParams = new Object[]{postJourneyReq.getUser_id(),postJourneyReq.getPeriod()};
        this.jdbcTemplate.update(addJouneryQuery,addJourneyParams);

        //여정아이디 가져오기
        String idpeekQuery = "select last_insert_id()";
        int journey_id = this.jdbcTemplate.queryForObject(idpeekQuery,int.class);

        //키워드 데이터 추가
        String keywordsAddQuery = "insert into keywords(journey_id,name) VALUES(?,?)";
        for(int i=0;i< keywords.length;i++) //들어온 키워드 수만큼 반복해서 키워드 데이터 추가.
        {
            String name = keywords[i];
            Object[] keywordsAddParams = new Object[]{journey_id,name};
            this.jdbcTemplate.update(keywordsAddQuery,keywordsAddParams);
        }

        //행성추가 및 행성 세부계획 추가
        String addPlanetQuery = "insert into planet(journey_id,planet_name) VALUES(?,?)";
        String addPlanQuery = "insert into detailed_plan(planet_id,plan_content) VALUES(?,?)";
        for(int j=0;j<planets.size();j++)
        {
            PostJourneyReq.Planetinfo planetinfo = planets.get(j); // planetinfo 리스트의 요소중 하나를 가져온다.
            String planet_name = planetinfo.getPlanet_name(); // 행성이름을 받아온다.
            List<String> planet_plans = planetinfo.getDetailed_plans(); // 행성 계획리스트를 받아온다.

            Object[] addPlanetParams = new Object[]{journey_id,planet_name}; // 행성추가 파람
            this.jdbcTemplate.update(addPlanetQuery,addPlanetParams); // 행성 데이터 추가
            int planet_id = this.jdbcTemplate.queryForObject(idpeekQuery,int.class); // 행성아이디를 가져온다.

            for(int k=0;k<planet_plans.size();k++) // 행성 세부계획의 수만큼 세부계획 데이터를 추가한다.
            {
                String plan_content = planet_plans.get(k); //세부계획리스트에서 세부계획내용을 가져온다.
                Object[] addPlanParams = new Object[]{planet_id,plan_content};
                this.jdbcTemplate.update(addPlanQuery,addPlanParams); // 행성아이디,세부계획내용을 이용하여 세부계획데이터생성
            }

        }

        PostJourneyRes postJourneyRes = new PostJourneyRes(); // 리턴값만들기
        postJourneyRes.setJourney_id(journey_id);
        postJourneyRes.setKeywords(keywords);
        postJourneyRes.setPlanets(planets);
        postJourneyRes.setPeriod(period);
        postJourneyRes.setUser_id(postJourneyReq.getUser_id());

        return postJourneyRes;
    }

}

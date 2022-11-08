package com.example.demo.src.journey;
import com.example.demo.config.BaseException;
import com.example.demo.src.diary.DiaryDao;
import com.example.demo.src.journey.model.GetAllJourneyRes;
import com.example.demo.src.journey.model.GetJourneyRes;
import com.example.demo.src.journey.model.PostJourneyReq;
import com.example.demo.src.journey.model.PostJourneyRes;
import com.example.demo.src.planet.PlanetDao;
import com.example.demo.utils.image.model.GetImageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.*;

import static com.example.demo.config.BaseResponseStatus.*;


@Repository
public class JourneyDao {

    private JdbcTemplate jdbcTemplate;
    private DiaryDao diaryDao;
    private PlanetDao planetDao;
    @Autowired
    public void setDataSource(DataSource dataSource ,DiaryDao diaryDao, PlanetDao planetDao){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.diaryDao=diaryDao;
        this.planetDao=planetDao;
    }


    //여정 생성 api
    @Transactional
    public PostJourneyRes createJourney (PostJourneyReq postJourneyReq, int user_id) throws BaseException
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


        //여정,닉네임 데이터를 가지고 여정데이터를 생성해준다.
        String addJouneryQuery = "insert into journey(user_id,period,nickname) VALUES(?,?,?)";
        Object[] addJourneyParams = new Object[]{user_id,postJourneyReq.getPeriod(),postJourneyReq.getNickname()};
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

            //행성이름 빈값 체크 validation
            if(planetinfo.getPlanet_name() == null)
            {
                throw new BaseException(EMPTY_PLANET_NAME);
            }
            String planet_name = planetinfo.getPlanet_name(); // 행성이름을 받아온다.

            //행성이름 중복 체크 validation , 입력된 행성이름 각각 양쪽 공백 지우고,List에 담고 , List에 담은거 Set에 담아서 길이비교
            List<String> planet_names = new ArrayList<>();
            for(int a=0;a<planets.size();a++)
            {
                String current_planet_name = planets.get(a).getPlanet_name().trim();
                planet_names.add(current_planet_name);
            }
            Set<String> s = new HashSet<>(planet_names);
            if(planet_names.size() != s.size())
            {
                //길이가 다르다 == 중복값이 있었다..
                throw new BaseException(DUPLICATED_PLANET_NAME);
            }

            List<String> planet_plans = planetinfo.getDetailed_plans(); // 행성 계획리스트를 받아온다.

            if(planet_plans.size() ==0)
            {
                //세부계획이 빈값이다?
                throw new BaseException(EMPTY_DETAILED_PLAN);
            }

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

        String notbelongPlanetQuery = "insert into planet(planet_name,journey_id) VALUES('해당없음',?)";

        this.jdbcTemplate.update(notbelongPlanetQuery, journey_id);

        PostJourneyRes postJourneyRes = new PostJourneyRes(); // 리턴값만들기
        postJourneyRes.setJourney_id(journey_id);
        postJourneyRes.setKeywords(keywords);
        postJourneyRes.setPlanets(planets);
        postJourneyRes.setPeriod(period);
        postJourneyRes.setUser_id(user_id);
        postJourneyRes.setNickname(postJourneyReq.getNickname());

        return postJourneyRes;
    }

    //활성화되어있는 유저인지 체크 (탈퇴한 유저가 아닌지 체크)
    public int checkUser (int user_id)
    {
        String checkQuery = "select status from user where user_id =?";
        return this.jdbcTemplate.queryForObject(checkQuery,int.class,user_id);
    }

    public int getUserIdByJourneyId(int journey_id)
    {
        String getQuery = "select user_id from journey where journey_id = ?";
        return this.jdbcTemplate.queryForObject(getQuery,int.class,journey_id);
    }


    public GetAllJourneyRes getJourney(int user_id) {
        int sum_period= 0;
        int sum_planet = 0;

        String sql = "select journey_id from journey where user_id = ? and status = 1";
        int param = user_id;
        List<Integer> journey_id = this.jdbcTemplate.queryForList(sql,int.class,param);

        List<GetJourneyRes> getJourneyRes = new ArrayList<>();

        for(int journeyIdx : journey_id){
        String periodSql = "select period from journey where journey_id = ? and status =1";

        int period = this.jdbcTemplate.queryForObject(periodSql,int.class,journeyIdx);
        sum_period += period;
        String start_dateSql = "select date_format(created_at, '%y-%m-%d') from journey where journey_id = ? and status =1 ";
        String start_date = this.jdbcTemplate.queryForObject(start_dateSql,String.class,journeyIdx);

        String end_dateSql = "select date_format(date_add(created_at , interval 7*? day), '%y-%m-%d') from journey where journey_id = ? and status =1";
        Object[] end_dateParam = new Object[]{period,journeyIdx};
        String end_date = this.jdbcTemplate.queryForObject(end_dateSql,String.class,end_dateParam);

        List<String> tmp_planet = planetDao.getPlanetName(journeyIdx);
        sum_planet += tmp_planet.size();
        GetImageList tmp_img = diaryDao.getFourImages(journeyIdx);
        GetJourneyRes tmp = new GetJourneyRes(period,tmp_planet,tmp_img,start_date,end_date);
        getJourneyRes.add(tmp);
        }
        int sum_journey=getJourneyRes.size();

        GetAllJourneyRes getAllJourneyRes = new GetAllJourneyRes(sum_journey,sum_planet,sum_period,getJourneyRes);

        return getAllJourneyRes;
    }

    // getPlanetName <- 여정 아이디에 맞는 행성이름 리스트 가져오는
    // getFourImages <- 여정아이디에 맞는 최근 4개의 다이어리 이미지 가져오는.

    public String getNameByJourneyId(int journey_id){
        String sql = "select nickname from journey where journey_id = ? and status =1";
        return this.jdbcTemplate.queryForObject(sql,String.class,journey_id);
    }





    public int checkDuplicateNickname(String nickname) {
        String checkQuery = "select EXISTS(select nickname from journey where nickname=?)";
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, nickname);
    }


//    //행성 목록에서 행성이름빈값,중복  세부계획 빈값, 중복 체크  [결과는 int값에따라다름]
//    public int checkPlanetList (List<PostJourneyReq.Planetinfo> planetinfo)
//    {
//        for(int i=0;i<planetinfo.size();i++)
//        {
//            PostJourneyReq.Planetinfo current = planetinfo.get(i);
//            String planet_name = current.getPlanet_name();
//            if(planet_name == null)
//            {
//                return 1;
//            }
//
//
//        }
//    }



}

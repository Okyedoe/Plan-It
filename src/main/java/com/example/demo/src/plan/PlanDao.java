package com.example.demo.src.plan;
import com.example.demo.config.BaseException;
import com.example.demo.src.journey.model.PostJourneyReq;
import com.example.demo.src.journey.model.PostJourneyRes;
import com.example.demo.src.plan.model.GetTodayPlanRes;
import com.example.demo.src.plan.model.PostPlanReq;
import com.example.demo.src.plan.model.PostPlanRes;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.*;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.*;


@Repository
public class PlanDao {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
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

    //세부계획 아이디를 이용하여 해당 계획 유저아이디를 받아오는 명령어
    public int getUser_id_from_detailed_plan_id (int detailed_plan_id)
    {
        String getQuery = "select user_id from detailed_plan\n" +
                "join planet p on p.planet_id = detailed_plan.planet_id\n" +
                "join journey j on j.journey_id = p.journey_id where detailed_plan_id = ?";
        int user_id = this.jdbcTemplate.queryForObject(getQuery,int.class,detailed_plan_id);
        return user_id;
    }


    //행성 세부계획 추가
    @Transactional
    public PostPlanRes createPlan (PostPlanReq postPlanReq ,int planet_id ) throws BaseException
    {
        //타입검사 + 정리 (들어온 갯수와 정렬된 갯수를 비교, 다르면 입력값이 올바르지않아서 ->요일이 중복됬는지 ,입력형태가 잘못됬는지 체크함,요일을 순서대로 넣지않아도됨.)
        String type = postPlanReq.getType();
        if(type.length() > 100) //타입에 많은길이의 스트링을 보낼것을 대비
        {
           throw new BaseException(WRONG_TYPE_DAY);
        }
        StringTokenizer st = new StringTokenizer(type,",");
        int length =  st.countTokens();
        HashMap<Integer,String> result = new HashMap<>();
        while(st.hasMoreTokens())
        {
            String temp = st.nextToken();
            switch (temp) {
                case "월":
                    result.put(1,"월");
                    break;
                case "화":
                    result.put(2,"화");
                    break;
                case "수":
                    result.put(3,"수");
                    break;
                case "목":
                    result.put(4,"목");
                    break;
                case "금":
                    result.put(5,"금");
                    break;
                case "토":
                    result.put(6,"토");
                    break;
                case "일":
                    result.put(7,"일");
                    break;
            }

        }
        if(result.size() != length)
        {
            throw new BaseException(WRONG_TYPE_DAY);
        }
        String sorted_type = "";
        for(int i=1;i<=7;i++)
        {
            if(result.containsKey(i))
            {
                sorted_type += result.get(i)+",";
            }
        }


        String createQuery = "insert into detailed_plan(planet_id,plan_content,type) VALUES(?,?,?)";
        Object[] createParams = new Object[]{planet_id,postPlanReq.getPlan_content(),sorted_type};
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

    //오늘 세부계획 출력
    public List<GetTodayPlanRes> getTodayPlans (int journey_id)
    {
        //journey_id를 이용하여 해당 행성값들 가져온다.
        String getPlanets = "select planet_id from planet where journey_id =?";
        List<Integer> planet_ids ;
        planet_ids = this.jdbcTemplate.query(getPlanets,(rs, rowNum) ->new Integer(rs.getInt("planet_id")) ,journey_id  );

        System.out.println(planet_ids.size() +":"+ planet_ids.get(0)); // 잘저장된걸 확인가능.


        //행성값들을 이용하여 마음가짐,일회성, 오늘 요일과 맞는 세부계획들을 가져온다.
        List<GetTodayPlanRes> result = new ArrayList<>();

        for(int i=0;i<planet_ids.size();i++)
        {
            String getToday ="select detailed_plan.planet_id,plan_content,type,planet_image from detailed_plan\n" +
                    "left join planet p on p.planet_id = detailed_plan.planet_id where detailed_plan.planet_id = ?";
            int current_id = planet_ids.get(i);
            List<GetTodayPlanRes> temp = this.jdbcTemplate.query(getToday,(rs, rowNum) ->new GetTodayPlanRes(
                    rs.getInt("planet_id"),
                    rs.getString("planet_image"),
                    rs.getString("plan_content"),
                    rs.getString("type")
            ) ,current_id);
            result.addAll(temp);
        }
        //마음가짐,1회성,매일루틴  ++ 해당요일에 맞는 애들만 가져오기
        LocalDate now = LocalDate.now();
//        String day = now.getDayOfWeek().toString();
        int day_val = now.getDayOfWeek().getValue(); // 1 = 월 , 2 = 화 , 3= 수 ~ 7 = 일
//        System.out.println("day_val = " + day_val);
//        System.out.println("result 사이즈 :"+result.size());

        List<GetTodayPlanRes> real_result = new ArrayList<>();

        for(int j=0;j<result.size();j++)
        {
            String current_type = result.get(j).getType();
            System.out.println("현재 current_type값 : "+current_type);
            if(current_type.contains(","))
            {
                switch (day_val)
                {
                    case 1 :
                        if(current_type.contains("월"))
                        {
                            real_result.add(result.get(j));
                        }
                        break;
                    case 2 :
                        System.out.println("들어온값 : "+current_type);
                        if(current_type.contains("화"))
                        {
                            real_result.add(result.get(j));
                        }
                        break;
                    case 3 :
                        if(current_type.contains("수"))
                        {
                            real_result.add(result.get(j));
                        }
                        break;
                    case 4 :
                        if(current_type.contains("목"))
                        {
                            real_result.add(result.get(j));
                        }
                        break;
                    case 5 :
                        if(current_type.contains("금"))
                        {
                            real_result.add(result.get(j));
                        }
                        break;
                    case 6 :
                        if(current_type.contains("토"))
                        {
                            real_result.add(result.get(j));
                        }
                        break;
                    case 7 :
                        if(current_type.contains("일"))
                        {
                            real_result.add(result.get(j));
                        }
                        break;


                }
            }
            else{
                real_result.add(result.get(j));
            }

        }


        return real_result;




    }

    //세부계획 완료처리 , 이미 완료 처리되어있다면 미완료로 바꿈.
    @Transactional
    public String completePlan (int detailed_plan_id) throws BaseException
    {
        String checkquery = "select is_completed from detailed_plan where detailed_plan_id =?";
        int current_is_completed = this.jdbcTemplate.queryForObject(checkquery,int.class,detailed_plan_id);

        checkDeleted(detailed_plan_id); //받은 세부계획이 삭제된건지 체크.

        if(current_is_completed == 1)
        {
            String query = "update detailed_plan set is_completed=0 where detailed_plan_id =?";
            this.jdbcTemplate.update(query,detailed_plan_id);
            return detailed_plan_id+": 완료 -> 미완료 처리";
        }
        else{
            String query = "update detailed_plan set is_completed=1 where detailed_plan_id =?";
            this.jdbcTemplate.update(query,detailed_plan_id);
            return detailed_plan_id+": 미완료 -> 완료 처리";
        }


    }

    //세부계획 삭제처리 , 이미 완료 처리되어있다면 미완료로 바꿈.
    @Transactional
    public String deletePlan (int detailed_plan_id) throws BaseException
    {
        checkDeleted(detailed_plan_id); //받은 세부계획이 삭제된건지 체크.

        String query = "update detailed_plan set status=0 where detailed_plan_id =?";
        this.jdbcTemplate.update(query,detailed_plan_id);
        return detailed_plan_id+": 삭제처리";


    }
    public void checkDeleted (int detailed_plan_id) throws BaseException
    {
        String deletecheckquery = "select status from detailed_plan where detailed_plan_id =?";
        int current_status = this.jdbcTemplate.queryForObject(deletecheckquery,int.class,detailed_plan_id);
        if(current_status==0)
        {
            throw new BaseException(ALREADY_DELETED);
        }
    }





}

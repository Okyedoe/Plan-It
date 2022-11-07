
package com.example.demo.src.plan;
import com.example.demo.config.BaseException;
import com.example.demo.src.journey.model.PatchPlanRes;
import com.example.demo.src.journey.model.PostJourneyReq;
import com.example.demo.src.journey.model.PostJourneyRes;
import com.example.demo.src.plan.model.GetTodayPlanRes;
import com.example.demo.src.plan.model.PostPlanReq;
import com.example.demo.src.plan.model.PostPlanRes;
import io.swagger.models.auth.In;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
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
        String sorted_type = "";
        boolean checkRoutine = false;
        if(!(type.equals("마음가짐")) && !(type.equals("1회성"))&& !(type.equals("매일루틴")) &&!(type.equals("비정기적"))) //
        {
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
            checkRoutine = true;

            for(int i=1;i<=7;i++)
            {
                if(result.containsKey(i))
                {
                    sorted_type += result.get(i)+",";
                }
            }
        }



        String createQuery = "insert into detailed_plan(planet_id,plan_content,type) VALUES(?,?,?)";
        int last_in_detailed_plan_id;
        //타입이 루틴이라면 -> 요일 저장을 따로 한다.
        if (checkRoutine) {
            //계획 저장하는 테이블에는 루틴으로 저장해주고
            Object[] createParams = new Object[]{planet_id,postPlanReq.getPlan_content(),"루틴"};  //타입은 루틴으로 넣어줘야함.
            this.jdbcTemplate.update(createQuery,createParams);

            String peekQuery = "select last_insert_id()";
            last_in_detailed_plan_id = this.jdbcTemplate.queryForObject(peekQuery,int.class);

            //요일 따로 담는 테이블에  요일 별 데이터 생성해주는 부분
            StringTokenizer st = new StringTokenizer(sorted_type, ",");
            while (st.hasMoreTokens()) {
                String day = st.nextToken();
                String daysQuery  = "insert into plan_day_of_week(detailed_plan_id,day_of_week) VALUES(?,?)";
                Object[] dayParams = new Object[]{last_in_detailed_plan_id, day};
                this.jdbcTemplate.update(daysQuery, dayParams);

            }
            //요일을 저장한 값에서 오늘 요일이 존재한다면 오늘 할일로 되니까 total plans를 +1를 해줘야한다.
            LocalDate day = LocalDate.now();

            System.out.println(day);
            DayOfWeek dayOfWeek = day.getDayOfWeek();
            System.out.println("dayOfWeek = " + dayOfWeek);
            String today = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN);
            System.out.println(today);

            if (sorted_type.contains(today)) {
                //루틴의 요일값중 오늘이 포함되는것이므로 해당유저의 총 할일의 갯수를 +1 해준다
                int user_id = getUser_id_from_planet_id(planet_id);

                //00 시 00분 00초 오늘,해당유저의 값이 잘 저장되서 today_totalplan_completedplan에 데이터가 있는지 체크, 있어야 +1 해줄꺼니까
                String existQuery = "select EXISTS(select * from today_totalplan_completedplan t where date_format(created_at,'%Y-%m-%d') = ? and user_id =?) ";
                int existResult = this.jdbcTemplate.queryForObject(existQuery, int.class, day,
                    user_id);
                if (existResult == 0) {
                    //값이 1이라면 == true , 0이라면 == false
                    throw new BaseException(SHEDULE_ERROR);
                }

                String plusQuery = "update today_totalplan_completedplan set total_plans = total_plans+1 where user_id = ?";
                this.jdbcTemplate.update(plusQuery, user_id);

            }



        }
        //타입이 루틴이 아닌 그 나머지라면
        else{
            //그 타입에 맞게 계획테이블에 데이터 추가해주고
            Object[] createParams = new Object[]{planet_id,postPlanReq.getPlan_content(),type};
            this.jdbcTemplate.update(createQuery,createParams);

            String peekQuery = "select last_insert_id()";
            last_in_detailed_plan_id = this.jdbcTemplate.queryForObject(peekQuery,int.class);
        }

        if (checkRoutine) {
            //루틴 타입이라면 루틴 + 요일값으로 타입을 표현해서 리턴해준다.
            String getInfo = "select * from detailed_plan where detailed_plan_id = ?";
            PostPlanRes postPlanRes =this.jdbcTemplate.queryForObject(getInfo,(rs, rowNum) -> new PostPlanRes(
                rs.getInt("detailed_plan_id"),
                rs.getInt("planet_id"),
                rs.getString("plan_content"),
                rs.getString("type")

            ),last_in_detailed_plan_id );
            String temp = "루틴 : " + sorted_type;
            if (postPlanRes != null) {
                postPlanRes.setType(temp);
            }
            return postPlanRes;
        }
        //루틴이 아닌 다른 타입들은 그냥 타입값 포함해서 리턴.
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

        //해당없음 행성의 1회성애들도 가져와야한다 -> 딱히 수정할 부분은 없음.
        //행성값들을 이용하여 마음가짐,1회성, 오늘 요일과 맞는 세부계획들을 가져온다.  ,, status가 0이 아닌애들을 가져와야한다. + is_completed를 이용하여 완료된건지 아닌지도 리턴에추가
        List<GetTodayPlanRes> result = new ArrayList<>();

        for(int i=0;i<planet_ids.size();i++)
        {
            //status를 이용하여 삭제되지않은걸 가져와야함.
            String getToday ="select detailed_plan.planet_id,plan_content,type,planet_image,is_completed,detailed_plan_id from detailed_plan\n" +
                    "left join planet p on p.planet_id = detailed_plan.planet_id where detailed_plan.planet_id = ? and detailed_plan.status =1";
            int current_id = planet_ids.get(i);
            List<GetTodayPlanRes> temp = this.jdbcTemplate.query(getToday,
                (rs, rowNum) -> new GetTodayPlanRes(
                    rs.getInt("planet_id"),
                    rs.getString("planet_image"),
                    rs.getString("plan_content"),
                    rs.getString("type"),
                    rs.getInt("is_completed"),
                    rs.getInt("detailed_plan_id")
                ), current_id);
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

            if (current_type.equals("루틴")) {
                //해당 세부계획아이디를 이용해서 모든 요일을 가져온다.
                String dayQuery = "select day_of_week\n"
                    + "from plan_day_of_week\n"
                    + "where detailed_plan_id = ? and status = 1;";

                List<String> dayList = this.jdbcTemplate.query(dayQuery,(rs, rowNum) -> new String(rs.getString("day_of_week")),result.get(j).getDetailed_plan_id()); //요일들 가져옴.
                String tt = "";
                for (String day : dayList) {
                    tt += day+",";
                }
                switch (day_val)
                {
                    case 1 :
                        if(tt.contains("월"))
                        {
                            result.get(j).setType("루틴 :"+tt);
                            real_result.add(result.get(j));
                        }
                        break;
                    case 2 :
                        System.out.println("들어온값 : "+tt);
                        if(tt.contains("화"))
                        {
                            result.get(j).setType("루틴 :"+tt);
                            real_result.add(result.get(j));
                        }
                        break;
                    case 3 :
                        if(tt.contains("수"))
                        {
                            result.get(j).setType("루틴 :"+tt);
                            real_result.add(result.get(j));
                        }
                        break;
                    case 4 :
                        if(tt.contains("목"))
                        {
                            result.get(j).setType("루틴 :"+tt);
                            real_result.add(result.get(j));
                        }
                        break;
                    case 5 :
                        if(tt.contains("금"))
                        {
                            result.get(j).setType("루틴 :"+tt);
                            real_result.add(result.get(j));
                        }
                        break;
                    case 6 :
                        if(tt.contains("토"))
                        {
                            result.get(j).setType("루틴 :"+tt);
                            real_result.add(result.get(j));
                        }
                        break;
                    case 7 :
                        if(tt.contains("일"))
                        {
                            result.get(j).setType("루틴 :"+tt);
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

    //세부계획 완료처리 , 이미 완료 처리되어있다면 미완료로 바꿈 , 삭제된건지 체크
    @Transactional
    public PatchPlanRes completePlan (int detailed_plan_id) throws BaseException
    {
        //현재 타입이 마음가짐인지 체크해서 마음가짐이라면 에러 발생
        String typecheck = "select type from detailed_plan where detailed_plan_id =?";
        String current_type = this.jdbcTemplate.queryForObject(typecheck, String.class,
            detailed_plan_id);
        if (current_type != null && current_type.equals("마음가짐")) {
            throw new BaseException(CANNOT_COMPLETED);
        }
        boolean checkcheck = false;
        if(current_type.equals("비정기적"))
        {
            checkcheck= true;
        }

        //이미 실행됬는지 is_completed 값을 가져오는 부분이다.
        String checkquery = "select is_completed from detailed_plan where detailed_plan_id =?";
        int current_is_completed = this.jdbcTemplate.queryForObject(checkquery,int.class,detailed_plan_id);

        checkDeleted(detailed_plan_id); //받은 세부계획이 삭제된건지 체크.

        //세부계획의 행성아이디를 가져오는 쿼리
        String current_planet_query = "select planet_id from detailed_plan where detailed_plan_id =? ";
        int current_planet_id = this.jdbcTemplate.queryForObject(current_planet_query,int.class,detailed_plan_id);


        LocalDate day = LocalDate.now();

        System.out.println(day);
        DayOfWeek dayOfWeek = day.getDayOfWeek();
        System.out.println("dayOfWeek = " + dayOfWeek);
        String today = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN);
        System.out.println(today);

        int user_id = getUser_id_from_detailed_plan_id(detailed_plan_id);





        if(current_is_completed == 1) //이미 실행된 세부계획이라면 완료 세부계획의 is_completed를 0으로 바꾸고,
        {
            //is_completed를 0으로 바꾸고
            String query = "update detailed_plan set is_completed=0 where detailed_plan_id =?";
            this.jdbcTemplate.update(query,detailed_plan_id);

            //경험치 감소시키고(1개 미완료시 경험치 1 감소) , 누적값이 33으로 나눠지면 33으로 나눈값으로 레벨 설정 (레벨업)
            String expUp = "update planet set planet_exp = planet_exp-1  where planet_id = ?";
            String levelChange = "update planet set planet_level = FLOOR(planet_exp/33)+1 where planet_id = ?";
            this.jdbcTemplate.update(expUp, current_planet_id);
            this.jdbcTemplate.update(levelChange, current_planet_id);

            //그다음 today_~ 테이블들과 관련된 내용

            // today_totalplan_completedplan 테이블에  00시에 데이터가 생성되어서 존재하는지 체크
            //미완료처리시  completed_plans 감소시켜줘야하므로
            //00 시 00분 00초 오늘,해당유저의 값이 잘 저장되서 today_totalplan_completedplan에 데이터가 있는지 체크, 있어야 completed_plans를 -1 해줄꺼니까
            //있다면 해당 아이디값을 없다면 0을 리턴해준다.
            String existQuery = "select EXISTS(select today_totalplan_completedplan_id from today_totalplan_completedplan where date_format(created_at,'%Y-%m-%d') = ? and user_id =?) ";
            int existResult = this.jdbcTemplate.queryForObject(existQuery, int.class, day,
                user_id);
            if (existResult == 0 ) {
                //0이라면 없는것
                throw new BaseException(SHEDULE_ERROR);
            }


            //있다면 completed_plans 감소시킨다.!
            String getId = "select today_totalplan_completedplan_id from today_totalplan_completedplan where date_format(created_at,'%Y-%m-%d') = ? and user_id =?";
            int getIdResult = this.jdbcTemplate.queryForObject(getId, int.class, day, user_id);
            String plusQuery ="";
            //비정기적이라면 최대 갯수값도 줄여준다! (비정기적은 원래해야하는 갯수에 포함안시켜놨어서)
            if (checkcheck) {
                plusQuery = "update today_totalplan_completedplan set  completed_plans = completed_plans-1 ,total_plans = total_plans-1 where today_totalplan_completedplan_id = ?";
            }
            else{// 비정기적이 아니라면
                plusQuery = "update today_totalplan_completedplan set  completed_plans = completed_plans-1 where today_totalplan_completedplan_id = ?";
            }
            this.jdbcTemplate.update(plusQuery, getIdResult );

            //today_completed_plans에 있는 세부계획의 status를 0로바꿔줘야한다.

            //이미 존재한다면 1 , 없다면 0을 가져오는 쿼리
            String checkAlreadyExist = "select EXISTS(select today_completed_plans_id\n"
                + "from today_completed_plans\n"
                + "where detailed_plan_id = ?\n"
                + "  and date_format(created_at, '%Y-%m-%d') = ?)";
            //그 결과
            int checkResult = this.jdbcTemplate.queryForObject(checkAlreadyExist, int.class,
                detailed_plan_id, day);

            if (checkResult == 1) {
                //존재하므로 그 아이디값을 이용해서 status를 0으로 바꿔준다.
                String getId2 = "select today_completed_plans_id\n"
                    + "                from today_completed_plans\n"
                    + "                where detailed_plan_id = ?\n"
                    + "                  and date_format(created_at, '%Y-%m-%d') = ?";
                int id2 = this.jdbcTemplate.queryForObject(getId2,int.class,detailed_plan_id, day);

                String setStatusOne = "update today_completed_plans\n"
                    + "set status =0\n"
                    + "where today_completed_plans_id = ?;";
                this.jdbcTemplate.update(setStatusOne, id2);
            }
            else{
                //없다는건 말이안되므로 오류발생
                throw new BaseException(NOT_EXISTS_COMPLETED_PLAN);

            }




            //증가시킨 세부계획의 내용과 그 세부계획을 가지고있는 행성의 정보
            String resultReturnQuery =
                "select detailed_plan_id,p.planet_id,p.planet_exp,p.planet_level,plan_content,type,dp.status,is_completed from detailed_plan dp\n"
                    + "join planet p on p.planet_id = dp.planet_id where dp.detailed_plan_id = ?";
            PatchPlanRes patchPlanRes = this.jdbcTemplate.queryForObject(resultReturnQuery,
                (rs, rowNum) -> new PatchPlanRes(
                    rs.getInt("detailed_plan_id"),
                    rs.getInt("planet_id"),
                    rs.getInt("planet_exp"),
                    rs.getInt("planet_level"),
                    rs.getString("plan_content"),
                    rs.getString("type"),
                    rs.getInt("status"),
                    rs.getInt("is_completed")
                ), detailed_plan_id

            );



            return patchPlanRes;
        }
        else{
            //아니라면 is_completed를 1로 바꿔주고
            String query = "update detailed_plan set is_completed=1 where detailed_plan_id =?";
            this.jdbcTemplate.update(query,detailed_plan_id);

            //경험치 증가시키고(1개 완료시 경험치 1 증가) , 누적값이 33으로 나눠지면 33으로 나눈값으로 레벨 설정 (레벨업)
            String expUp = "update planet set planet_exp = planet_exp+1  where planet_id = ?";
            String levelChange = "update planet set planet_level = FLOOR(planet_exp/33)+1 where planet_id = ?";
            this.jdbcTemplate.update(expUp, current_planet_id);
            this.jdbcTemplate.update(levelChange, current_planet_id);

            //그다음 today_~ 테이블들과 관련된 내용

            // today_totalplan_completedplan 테이블에  00시에 데이터가 생성되어서 존재하는지 체크
            //완료시 completed_plans 증가시켜줘야하므로
            //00 시 00분 00초 오늘,해당유저의 값이 잘 저장되서 today_totalplan_completedplan에 데이터가 있는지 체크, 있어야 completed_plans를 +1 해줄꺼니까
            //있다면 해당 아이디값을 없다면 0을 리턴해준다.
            System.out.println("day = " + day);
            System.out.println("user_id = " + user_id);
            String existQuery = "select EXISTS(select today_totalplan_completedplan_id from today_totalplan_completedplan where date_format(created_at,'%Y-%m-%d') = ? and user_id =?) ";
            int existResult = this.jdbcTemplate.queryForObject(existQuery, int.class, day,
                user_id);
            if (existResult == 0 ) {
                //0이라면 없는것
                throw new BaseException(SHEDULE_ERROR);
            }
            //있다면 completed_plans 증가!
            String getId = "select today_totalplan_completedplan_id from today_totalplan_completedplan where date_format(created_at,'%Y-%m-%d') = ? and user_id =?";
            int getIdResult = this.jdbcTemplate.queryForObject(getId, int.class, day, user_id);

            //비정기적이라면 total_plans값도 1증가 시켜줘야한다.
            String plusQuery = "";
            if (checkcheck) {
                plusQuery = "update today_totalplan_completedplan set  completed_plans = completed_plans+1,total_plans =total_plans+1 where today_totalplan_completedplan_id = ?";

            }
            else{
                plusQuery = "update today_totalplan_completedplan set  completed_plans = completed_plans+1 where today_totalplan_completedplan_id = ?";
            }
            this.jdbcTemplate.update(plusQuery, getIdResult );

            //거기다가 today_completed_plans에다가 어떤 세부계획을 완성했는지 데이터를 생성해줘야한다.
            //데이터를 생성해주기전에 이미 있는지부터 체크하고 있다면 status를 1로바꿔줘야한다.

            //이미 존재한다면 1 , 없다면 0을 가져오는 쿼리
            String checkAlreadyExist = "select EXISTS(select today_completed_plans_id\n"
                + "from today_completed_plans\n"
                + "where detailed_plan_id = ?\n"
                + "  and date_format(created_at, '%Y-%m-%d') = ?)";
            //그 결과
            int checkResult = this.jdbcTemplate.queryForObject(checkAlreadyExist, int.class,
                detailed_plan_id, day);

            if (checkResult == 1) {
                //널이 아니므로 그 아이디값을 이용해서 status를 1로 바꿔준다.
                String getId2 = "select today_completed_plans_id\n"
                    + "                from today_completed_plans\n"
                    + "                where detailed_plan_id = ?\n"
                    + "               and date_format(created_at, '%Y-%m-%d') = ?";
                int id2 = this.jdbcTemplate.queryForObject(getId2,int.class,detailed_plan_id, day);

                String setStatusOne = "update today_completed_plans\n"
                    + "set status =1\n"
                    + "where today_completed_plans_id = ?;";
                this.jdbcTemplate.update(setStatusOne, id2);
            }
            else{
                //아니라면 새로 데이터를 생성해주자.
                String addTodayCompletedPlan =
                    "insert into today_completed_plans(today_totalplan_completedplan_id, detailed_plan_id)\n"
                        + "VALUES (?, ?);";
                Object[] addTodayCompletedPlanParams = new Object[]{getIdResult, detailed_plan_id};
                this.jdbcTemplate.update(addTodayCompletedPlan, addTodayCompletedPlanParams);
            }




            //증가시킨 세부계획의 내용과 그 세부계획을 가지고있는 행성의 정보
            String resultReturnQuery =
                "select detailed_plan_id,p.planet_id,p.planet_exp,p.planet_level,plan_content,type,dp.status,is_completed from detailed_plan dp\n"
                    + "join planet p on p.planet_id = dp.planet_id where dp.detailed_plan_id = ?";
            PatchPlanRes patchPlanRes = this.jdbcTemplate.queryForObject(resultReturnQuery,
                (rs, rowNum) -> new PatchPlanRes(
                    rs.getInt("detailed_plan_id"),
                    rs.getInt("planet_id"),
                    rs.getInt("planet_exp"),
                    rs.getInt("planet_level"),
                    rs.getString("plan_content"),
                    rs.getString("type"),
                    rs.getInt("status"),
                    rs.getInt("is_completed")
                ), detailed_plan_id

            );

            return patchPlanRes;
        }


    }

    //세부계획 삭제처리 , 삭제했으므로 status는 0으로 바꾸고, 오늘 총 계획수 1줄이고 ,완료된거였으면 완료 수도 1 줄인다.
    //그리고 루틴이라면 해당 요일값들도 status를 0으로 처리해준다.
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


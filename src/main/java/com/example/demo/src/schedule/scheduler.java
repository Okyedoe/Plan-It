package com.example.demo.src.schedule;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;
import org.jetbrains.annotations.Async.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableAsync
public class scheduler {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Async
    @Scheduled(cron = "15 0 0 1/1 * ?") // 매일 오전 00시 00분 00초에  , 초 분 시 일 월 요일
    public void setIsCompletedZero() {
        String setIsCompletedZeroQuery = "update detailed_plan\n"
            + "set is_completed = 0\n"
            + "where type = '매일루틴'\n"
            + "  and type = '루틴';";
        //매일루틴, 루틴
        this.jdbcTemplate.update(setIsCompletedZeroQuery);

        // 현재 날짜/시간
        LocalDateTime now = LocalDateTime.now();

        // 현재 날짜/시간 출력
        System.out.println(now); // 2021-06-17T06:43:21.419878100

        // 포맷팅
        String formatedNow = now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초"));
        System.out.println("스케줄러에서 is_completed 초기화가 실행되었습니다. : " + formatedNow);

    }



    @Async
    @Scheduled(cron = "15 0 0 1/1 * ?")
    public void insertTodayTotalcounts() {
        String excludeRoutineCount = "select s.user_id, sum(s.총갯수) as '총갯수'\n"
            + "from (select u.user_id, sum(dp.status) as '총갯수'\n"
            + "      from detailed_plan dp\n"
            + "               join planet p on p.planet_id = dp.planet_id\n"
            + "               join journey j on j.journey_id = p.journey_id\n"
            + "               join user u on u.user_id = j.user_id\n"
            + "      where dp.type != '마음가짐'\n"
            + "        and type != '비정기적'\n"
            + "        and type != '루틴'\n"
            + "        and dp.status != 0\n"
            + "      group by j.user_id\n"
            + "\n"
            + "      union all\n"
            + "\n"
            + "      select j.user_id,\n"
            + "             char_length(replace(group_concat(day_of_week), ',', '')) -\n"
            + "             char_length(replace(replace(group_concat(day_of_week), ',', ''),\n"
            + "                                 SUBSTR('일월화수목금토', DAYOFWEEK(NOW()), 1), '')) as 총갯수\n"
            + "      from plan_day_of_week pw\n"
            + "               join detailed_plan dp on dp.detailed_plan_id = pw.detailed_plan_id\n"
            + "               join planet p on p.planet_id = dp.planet_id\n"
            + "               join journey j on p.journey_id = j.journey_id\n"
            + "               join user u on j.user_id = u.user_id\n"
            + "      where dp.status != 0\n"
            + "      group by j.user_id) s\n"
            + "group by s.user_id\n"
            + "order by s.user_id asc;";

        List<userTotalPlans> userList= this.jdbcTemplate.query(excludeRoutineCount, (rs, rowNum) -> new userTotalPlans(
            rs.getInt("user_id"),
            rs.getInt("총갯수")
        ));


        // 현재 날짜/시간
        LocalDateTime now = LocalDateTime.now();

        // 현재 날짜/시간 출력
        System.out.println(now); // 2021-06-17T06:43:21.419878100

        // 포맷팅
        String formatedNow = now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초"));

//        리스트에 저장된값 출력해봄
        for(userTotalPlans userTotalPlans : userList){
            System.out.println(
                String.format(formatedNow+" user id : %d , count : %d", userTotalPlans.getUser_id(),
                    userTotalPlans.getCount()));
        }



        String insertQuery = "insert into today_totalplan_completedplan(user_id,total_plans) VALUES(?,?)";
        Object[] insertParams = new Object[2];
        for (userTotalPlans userTotalPlans : userList) {
            int current_user_id = userTotalPlans.getUser_id();
            int current_total_plans = userTotalPlans.getCount();
            insertParams[0] = current_user_id;
            insertParams[1] = current_total_plans;

            this.jdbcTemplate.update(insertQuery, insertParams);
        }



    }

    static class userTotalPlans {

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        int user_id;
        int count;

        public userTotalPlans(int user_id, int count) {
            this.user_id = user_id;
            this.count = count;
        }

    }

}

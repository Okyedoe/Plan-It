package com.example.demo.src.report;

import com.example.demo.src.diary.DiaryDao;
import com.example.demo.src.planet.PlanetDao;
import com.example.demo.src.report.model.ReportRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class ReportDao {

    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public ReportRes getTotalReport(int user_id) {
        int[] total = new int[5];
        int[] completed = new int[5];
        for(int i = 4;i>=0;i--){
            String Totalsql = "select exists (select total_plans from today_totalplan_completedplan where user_id = ? and weekofyear(created_at) = weekofyear(now()) - ?)";
            Object[] TotalParams = new Object[]{user_id,i+1};
            int is_null =this.jdbcTemplate.queryForObject(Totalsql,int.class,TotalParams);
            if(is_null==0){
                total[i]=0;
            }else{
                String tmp = "select sum(total_plans) from today_totalplan_completedplan where user_id = ? and weekofyear(created_at) = weekofyear(now()) - ?";
                int value = this.jdbcTemplate.queryForObject(tmp,int.class,TotalParams);
                total[i]=value;
            }

            String Comsql = "select exists (select completed_plans from today_totalplan_completedplan where user_id = ? and weekofyear(created_at) = weekofyear(now()) - ?)";
            int is_null1 =this.jdbcTemplate.queryForObject(Comsql,int.class,TotalParams);
            if(is_null1==0){
                completed[i]=0;
            }else{
                String tmp1 = "select sum(completed_plans) from today_totalplan_completedplan where user_id = ? and weekofyear(created_at) = weekofyear(now()) - ?";
                int value = this.jdbcTemplate.queryForObject(tmp1,int.class,TotalParams);
                completed[i]=value;
            }
        }

        ReportRes reportRes = new ReportRes(total[0],total[1],total[2],total[3],total[4],completed[0],completed[1],completed[2],completed[3],completed[4]);
        return reportRes;
    }
}

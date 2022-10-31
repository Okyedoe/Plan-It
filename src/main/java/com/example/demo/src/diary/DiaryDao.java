package com.example.demo.src.diary;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.diary.model.*;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DiaryDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Transactional
    public PostDiaryRes createDiary(PostDiary postDiaryReq) {
        if(postDiaryReq.getImage_url().size() == 0){
            String addDiaryQuery = "insert into diary(user_id,emotion,evaluation,content) VALUES(?,?,?,?)";
            Object[] addDiaryParams = new Object[]{postDiaryReq.getUser_id(),postDiaryReq.getEmotion(),postDiaryReq.getEvaluation(),postDiaryReq.getContent()};
             this.jdbcTemplate.update(addDiaryQuery,addDiaryParams);

        }
        else{
            String addDiaryQuery = "insert into diary(user_id,emotion,evaluation,content) VALUES(?,?,?,?)";
            Object[] addDiaryParams = new Object[]{postDiaryReq.getUser_id(),postDiaryReq.getEmotion(),postDiaryReq.getEvaluation(),postDiaryReq.getContent()};
            this.jdbcTemplate.update(addDiaryQuery,addDiaryParams);
            String idpeekQuery = "select last_insert_id()";
            int diary_id = this.jdbcTemplate.queryForObject(idpeekQuery,int.class); // 다이어리 아이디 가져오기.
            String addImageQuery = "insert into diary_image(diary_id,diary_image_url) VALUES(?,?)";
            List<String> temp = postDiaryReq.getImage_url();
            for(int i = 0 ; i<postDiaryReq.getImage_url().size();i++){
                Object[] addImageParams = new Object[]{diary_id,temp.get(i)};
                this.jdbcTemplate.update(addImageQuery,addImageParams);
            }
        }
        PostDiaryRes postdiaryRes = new PostDiaryRes(); // 리턴값만들기
        postdiaryRes.setUser_id(postDiaryReq.getUser_id());
        postdiaryRes.setEmotion(postDiaryReq.getEmotion());
        postdiaryRes.setEvaluation(postDiaryReq.getEvaluation());
        postdiaryRes.setContent(postDiaryReq.getContent());
        return postdiaryRes;
    }
    @Transactional
    public List<String> getAllImages(int user_id,int diary_id){
        String getAllImagesQuery = "select diary_image_url from diary as a join diary_image as b on a.diary_id=b.diary_id where user_id= ? and a.diary_id = ? and b.status=1 order by created_at desc";
        Object[] getAllImagesParams =new Object[]{user_id,diary_id};
        return this.jdbcTemplate.query(getAllImagesQuery,
                (rs, rowNum) -> new String(
                        rs.getString("diary_image_url"))
                ,getAllImagesParams);
    }
    @Transactional
    public GetDiary getDiaryResObject(int diary_id){
        String sql = "select diary_id,emotion, evaluation,content,date_format(created_at, '%y-%m-%d') as created_at from diary where diary_id= ? ";
        return this.jdbcTemplate.queryForObject(sql,(rs,rowNum) -> new GetDiary(
                rs.getInt("diary_id"),
                rs.getString("emotion"),
                rs.getInt("evaluation"),
                rs.getString("content"),
                rs.getString("created_at")
                ),
                diary_id
                );
    }

    public int deleteDiary(int diary_id) {
        String deleteUserQuery = "update diary set status = 0 where diary_id =? and status = 1";
        int deleteUserParams = diary_id;
        return this.jdbcTemplate.update(deleteUserQuery,deleteUserParams);
    }



    //날짜 필터링 없이 모든 다이어리 가져오기.
    @Transactional
    public List<GetDiaryRes> getAllDiary(int user_id)  {

        String getDiaryIdSql = "select diary_id from diary where user_id =? and status= 1 order by created_at desc";
        int getDiaryIdParams = user_id;
        List<Integer> diary_id = this.jdbcTemplate.queryForList(getDiaryIdSql, Integer.class, getDiaryIdParams);


        //다이어리 아이디에 맞는 이미지 리스트 가져오기
        List<GetDiaryRes> getDiaryRes = new ArrayList<>();
        for(int diary_idx : diary_id){
            GetDiary getDiary=getDiaryResObject(diary_idx);
            List<String> img = getAllImages(user_id,diary_idx);
            GetDiaryRes temp = new GetDiaryRes(getDiary.getDiary_id(),getDiary.getEmotion(),getDiary.getEvaluation(),getDiary.getContent(),getDiary.getCreated_at(),img);
            getDiaryRes.add(temp);
        }
        return getDiaryRes;


    }
    @Transactional
    public List<GetDiaryRes> getDiary(int user_id, GetDiaryReq getDiaryReq) {
        String getDiaryIdSql = "select diary_id from diary where date_format(created_at,'%y%m%d') >=? and date_format(created_at,'%y%m%d') <= ? and user_id =? and status = 1 order by created_at desc";
        Object[] getDiaryParams = new Object[]{
                getDiaryReq.getStart_date(),
                getDiaryReq.getEnd_date(),
                user_id
        };
        List<Integer> diary_id = this.jdbcTemplate.queryForList(getDiaryIdSql, Integer.class, getDiaryParams);


        List<GetDiaryRes> getDiaryRes = new ArrayList<>();
        for(int diary_idx : diary_id){
            GetDiary getDiary=getDiaryResObject(diary_idx);
            List<String> img = getAllImages(user_id,diary_idx);
            GetDiaryRes temp = new GetDiaryRes(getDiary.getDiary_id(),getDiary.getEmotion(),getDiary.getEvaluation(),getDiary.getContent(),getDiary.getCreated_at(),img);
            getDiaryRes.add(temp);
        }
        return getDiaryRes;
    }

    public double[] getEval(int user_id) {
        double[] eval = new double[5];
        int start_date = 35;
        int end_date = 28;
        for(int i = 0 ; i <eval.length;i++){
            String Sql = "select ifnull(sum(evaluation)/count(*),0) from diary where user_id = ? and created_at between date_sub(now(),interval ? day) and date_sub(now(),interval ? day)";
            Object[] Params = new Object[]{user_id,start_date,end_date};
            eval[i]=this.jdbcTemplate.queryForObject(Sql,double.class,Params);
            start_date-=7;
            end_date-=7;
        }
        return eval;
    }
}

package com.example.demo.src.diary;

import com.example.demo.src.diary.model.PostDiary;
import com.example.demo.src.diary.model.PostDiaryReq;
import com.example.demo.src.diary.model.PostDiaryRes;
import com.example.demo.src.journey.model.PostJourneyRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
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


}

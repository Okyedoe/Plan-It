package com.example.demo.src.diary;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.diary.model.*;
import com.example.demo.utils.image.model.GetImageList;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

            String addDiaryQuery = "insert into diary(journey_id,emotion,evaluation,content) VALUES(?,?,?,?)";
            Object[] addDiaryParams = new Object[]{postDiaryReq.getJourney_id(),postDiaryReq.getEmotion(),postDiaryReq.getEvaluation(),postDiaryReq.getContent()};
             this.jdbcTemplate.update(addDiaryQuery,addDiaryParams);


        if(!postDiaryReq.getImage_url().equals("")){
            String idpeekQuery = "select last_insert_id()";
            int diary_id = this.jdbcTemplate.queryForObject(idpeekQuery,int.class); // 다이어리 아이디 가져오기.
            String addImageQuery = "insert into diary_image(diary_id,diary_image_url) VALUES(?,?)";
                Object[] addImageParams = new Object[]{diary_id,postDiaryReq.getImage_url()};
                this.jdbcTemplate.update(addImageQuery,addImageParams);
        }

        PostDiaryRes postdiaryRes = new PostDiaryRes(); // 리턴값만들기
        postdiaryRes.setJourney_id(postDiaryReq.getJourney_id());
        postdiaryRes.setEmotion(postDiaryReq.getEmotion());
        postdiaryRes.setEvaluation(postDiaryReq.getEvaluation());
        postdiaryRes.setContent(postDiaryReq.getContent());
        return postdiaryRes;
    }
    @Transactional
    public List<String> getAllImages(int diary_id){
        String getAllImagesQuery = "select diary_image_url from diary as a join diary_image as b on a.diary_id=b.diary_id where a.diary_id = ? and b.status=1 order by a.created_at desc";
        int getAllImagesParams = diary_id;
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

    @Transactional
    public int deleteDiary(int diary_id) {
        String deleteUserQuery = "update diary set status = 0 where diary_id =? and status = 1";
        int deleteUserParams = diary_id;
        return this.jdbcTemplate.update(deleteUserQuery,deleteUserParams);
    }



    //날짜 필터링 없이 모든 다이어리 가져오기.
    @Transactional
    public List<GetDiaryRes> getAllDiary(int user_id)  {

        String getDiaryIdSql = "select diary_id\n"
            + "from diary as a\n"
            + "         join journey as b on a.journey_id = b.journey_id\n"
            + "where b.user_id = ?\n"
            + "  and a.status = 1\n"
            + "order by a.created_at desc;";

        int getDiaryIdParams = user_id;
        List<Integer> diary_id = this.jdbcTemplate.queryForList(getDiaryIdSql, Integer.class, getDiaryIdParams);


        //다이어리 아이디에 맞는 이미지 리스트 가져오기
        List<GetDiaryRes> getDiaryRes = new ArrayList<>();
        for(int diary_idx : diary_id){
            GetDiary getDiary=getDiaryResObject(diary_idx);
            List<String> img = getAllImages(diary_idx);
            GetDiaryRes temp = new GetDiaryRes(getDiary.getDiary_id(),getDiary.getEmotion(),getDiary.getEvaluation(),getDiary.getContent(),getDiary.getCreated_at(),img);
            getDiaryRes.add(temp);
        }
        return getDiaryRes;


    }
    //날짜별 필터링 된 다이어리 가져오기
    @Transactional
    public List<GetDiaryRes> getDiary(int user_id, GetDiaryReq getDiaryReq) {
        String getDiaryIdSql = "select diary_id from diary as a join journey as b on a.journey_id=b.journey_id where date_format(a.created_at,'%y%m%d') >=? and date_format(a.created_at,'%y%m%d') <= ? and user_id =? and a.status = 1 order by a.created_at desc";
        Object[] getDiaryParams = new Object[]{
                getDiaryReq.getStart_date(),
                getDiaryReq.getEnd_date(),
                user_id
        };
        List<Integer> diary_id = this.jdbcTemplate.queryForList(getDiaryIdSql, Integer.class, getDiaryParams);


        List<GetDiaryRes> getDiaryRes = new ArrayList<>();
        for(int diary_idx : diary_id){
            GetDiary getDiary=getDiaryResObject(diary_idx);
            List<String> img = getAllImages(diary_idx);
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
            String Sql = "select ifnull(sum(evaluation)/count(*),0) from diary as a join journey as b on where user_id = ? and created_at between date_sub(now(),interval ? day) and date_sub(now(),interval ? day)";
            Object[] Params = new Object[]{user_id,start_date,end_date};
            eval[i]=this.jdbcTemplate.queryForObject(Sql,double.class,Params);
            start_date-=7;
            end_date-=7;
        }
        return eval;
    }
    public GetImageList yesterdayDiary(int user_id){
        try{
            String sql = "select diary_image_url from diary_image as a join diary as b on a.diary_id = b.diary_id join journey as j on b.journey_id = j.journey_id where a.status = 1 and user_id = ? and a.updated_at >= date_sub(now(),interval 1 day) order by a.updated_at desc limit 1";
            int param = user_id;
            List<String> img = this.jdbcTemplate.queryForList(sql, String.class, param);
            GetImageList getImageList = new GetImageList(img);
            return getImageList;
        }
        catch(NullPointerException exception){
            String[] tmp = new String[1];
            List<String> img = new ArrayList<>();
            img.add(tmp[0]);
            GetImageList getImageList = new GetImageList(img);
            return getImageList;
        }

        }
        //가장 최근 4개 다이어리 이미지 가져오기 만약 null이면 ""

    public GetImageList getFourImages(int journey_id) {
            String[] tmp = new String[1];
            String sql = "select diary_image_url from diary_image as a join diary as b on a.diary_id = b.diary_id join journey as j on b.journey_id = j.journey_id where a.status = 1 and b.journey_id = ? order by a.created_at desc limit 4";
            int param = journey_id;
            List<String> img = this.jdbcTemplate.queryForList(sql, String.class, param);

            switch (img.size()){
                case 0:
                    img.add(tmp[0]);
                    img.add(tmp[0]);
                    img.add(tmp[0]);
                    img.add(tmp[0]);
                    break;

                case 1:
                    img.add(tmp[0]);
                    img.add(tmp[0]);
                    img.add(tmp[0]);
                    break;
                case 2:
                    img.add(tmp[0]);
                    img.add(tmp[0]);
                    break;
                case 3:
                    img.add(tmp[0]);
                    break;
            }
            GetImageList getImageList = new GetImageList(img);
            return getImageList;
    }



}

package com.example.demo.src.diary;

import com.example.demo.config.BaseException;
import com.example.demo.src.diary.model.PostDiary;
import com.example.demo.src.diary.model.PostDiaryReq;
import com.example.demo.src.diary.model.PostDiaryRes;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.image.AwsS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.DELETE_DIARY_ERROR;

@Service
public class DiaryService {
    private final DiaryDao diaryDao;
    private final DiaryProvider diaryProvider;
    private final JwtService jwtService;
    private final AwsS3Service awsS3Service;

    @Autowired
    public DiaryService(DiaryDao diaryDao, DiaryProvider diaryProvider, JwtService jwtService, AwsS3Service awsS3Service) {
        this.diaryDao = diaryDao;
        this.diaryProvider = diaryProvider;
        this.jwtService = jwtService;
        this.awsS3Service = awsS3Service;
    }


    @Transactional
    public PostDiaryRes createDiary(PostDiaryReq postDiaryReq) throws BaseException{
        try{    String filename = "";
                if(!postDiaryReq.getImages().isEmpty()){
                    filename= awsS3Service.uploadImage(postDiaryReq.getImages());
                }
                PostDiary postDiary = new PostDiary();
                postDiary.setContent(postDiaryReq.getContent());
                postDiary.setEmotion(postDiaryReq.getEmotion());
                postDiary.setEvaluation(postDiaryReq.getEvaluation());
                postDiary.setJourney_id(postDiaryReq.getJourney_id());
                postDiary.setImage_url(filename);
                return diaryDao.createDiary(postDiary);
        }
        catch(Exception exception){
            exception.printStackTrace();
            throw   new BaseException(DATABASE_ERROR);
        }
    }



    @Transactional
    public void deleteDiary(int diary_id) throws BaseException{
        try{
            int result = diaryDao.deleteDiary(diary_id);
            if(result == 0 ){
                throw new BaseException(DELETE_DIARY_ERROR);
            }
        }catch(Exception exception){
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

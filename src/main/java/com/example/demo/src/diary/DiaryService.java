package com.example.demo.src.diary;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.diary.model.PostDiary;
import com.example.demo.src.diary.model.PostDiaryReq;
import com.example.demo.src.diary.model.PostDiaryRes;
import com.example.demo.src.journey.JourneyDao;
import com.example.demo.src.journey.JourneyProvider;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.image.AwsS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

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
        try{
            List<MultipartFile> images = postDiaryReq.getImages();
            List<String> url = new ArrayList<>();
            if(images!=null){
                for(MultipartFile img : images){
                    String filename = awsS3Service.uploadImage(img);
                    url.add(filename);
                }
            }
            PostDiary postDiary = new PostDiary();
            postDiary.setContent(postDiaryReq.getContent());
            postDiary.setEmotion(postDiaryReq.getEmotion());
            postDiary.setEvaluation(postDiaryReq.getEvaluation());
            postDiary.setUser_id(postDiaryReq.getUser_id());
            postDiary.setImage_url(url);

            return diaryDao.createDiary(postDiary);
        }
        catch(Exception exception){
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

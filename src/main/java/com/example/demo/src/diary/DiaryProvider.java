package com.example.demo.src.diary;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.diary.model.GetDiaryReq;
import com.example.demo.src.diary.model.GetDiaryRes;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.image.model.GetImageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DiaryProvider {
    private final DiaryDao diaryDao;
    private final JwtService jwtService;

    @Autowired
    public DiaryProvider(DiaryDao diaryDao, JwtService jwtService) {
        this.diaryDao = diaryDao;
        this.jwtService = jwtService;
    }
     /* 이미지 쿼리문 잘 작동하는지 테스트하는 메소드
    public List<String>  getAllImages(int user_id) {
        return diaryDao.getAllImages(user_id);
    }

      */

    @Transactional
    public List<GetDiaryRes> getAllDiary(int user_id) throws BaseException {
    try{
        return diaryDao.getAllDiary(user_id);
    }catch (Exception exception){
        exception.printStackTrace();
        throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
    }
    }

    @Transactional
    public List<GetDiaryRes> getDiary(int user_id, GetDiaryReq getDiaryReq) throws BaseException{
        try{
            return diaryDao.getDiary(user_id,getDiaryReq);
        }catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public double[] getEval(int user_id) throws BaseException{
        try{
            return diaryDao.getEval(user_id);
        }
        catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    public GetImageList yesterdayDiary(int user_id) throws BaseException{
        try{
            return diaryDao.yesterdayDiary(user_id);
        }
        catch(Exception exception){
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

    }

    public GetImageList getFourImages(int user_id) throws BaseException{
        try{
            return diaryDao.getFourImages(user_id);
        }
        catch(Exception exception){
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }



}

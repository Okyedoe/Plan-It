package com.example.demo.src.diary;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.diary.model.PostDiaryReq;
import com.example.demo.src.diary.model.PostDiaryRes;
import com.example.demo.src.journey.JourneyProvider;
import com.example.demo.src.journey.JourneyService;
import com.example.demo.src.journey.model.PostJourneyRes;
import com.example.demo.utils.JwtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;

@Api(tags = "DIARY API")
@RestController
@RequestMapping("/diary")
public class DiaryController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private final DiaryProvider diaryProvider;
    @Autowired
    private final DiaryService diaryService;
    @Autowired
    private final JwtService jwtService;

    public DiaryController (DiaryProvider diaryProvider ,DiaryService diaryService ,JwtService jwtService )
    {
        this.diaryProvider = diaryProvider;
        this.diaryService= diaryService;
        this.jwtService = jwtService;
    }
    @ApiOperation(value = "다이어리 생성 api",notes = "기간,키워드,행성이름,행성세부계획까지 모든 정보를 받아와서 여정을 만듭니다.")
    @ResponseBody
    @PostMapping("/{user_id}")
    @Transactional
    public BaseResponse<PostDiaryRes> createDiary(@PathVariable("user_id")int user_id, PostDiaryReq postDiaryReq) throws BaseException{
        try{

            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(user_id != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostDiaryRes postDiaryRes = new PostDiaryRes();
            postDiaryRes = diaryService.createDiary(postDiaryReq);
            return new BaseResponse<>(postDiaryRes);


        }catch (BaseException exception)
        {
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }



    }





}

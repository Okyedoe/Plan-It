package com.example.demo.src.diary;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.diary.model.PostDiaryReq;
import com.example.demo.src.diary.model.PostDiaryRes;
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

    public DiaryController(DiaryProvider diaryProvider, DiaryService diaryService, JwtService jwtService) {
        this.diaryProvider = diaryProvider;
        this.diaryService = diaryService;
        this.jwtService = jwtService;
    }

    @ApiOperation(value = "다이어리 생성 api", notes = "기간,키워드,행성이름,행성세부계획까지 모든 정보를 받아와서 여정을 만듭니다.")
    @ResponseBody
    @PostMapping("/{user_id}")
    @Transactional
    public BaseResponse<PostDiaryRes> createDiary(@PathVariable("user_id") int user_id, PostDiaryReq postDiaryReq) throws BaseException {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (user_id != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostDiaryRes postDiaryRes = new PostDiaryRes();
            postDiaryRes = diaryService.createDiary(postDiaryReq);
            return new BaseResponse<>(postDiaryRes);


        } catch (BaseException exception) {
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /*
    @ResponseBody
    @GetMapping("/{user_id}")
    public BaseResponse<List<GetDiaryRes>> getAllDiary(@PathVariable("user_id") int user_id) throws BaseException{
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(user_id != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetDiaryRes> getDiaryRes = diaryProvider.getAllDiary(user_id);
            return new BaseResponse<>(getDiaryRes);


    }      catch (BaseException exception)
        {
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }
}

     */


    /*  이미지 url리스트 형식으로 잘 나오는지 테스트해본 컨트롤러
        @ResponseBody
        @GetMapping("/{user_id}")
        public List<String> getAllDiary(@PathVariable("user_id") int user_id) {
           return diaryProvider.getAllImages(user_id);
        }

     */
    @ResponseBody
    @DeleteMapping("/{user_id}")
    public BaseResponse<String> deleteDiary(@PathVariable("user_id") int user_id, int diary_id) throws BaseException {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (user_id != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            diaryService.deleteDiary(diary_id);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }
    }
}

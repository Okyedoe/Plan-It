package com.example.demo.src.diary;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.diary.model.GetDiaryReq;
import com.example.demo.src.diary.model.GetDiaryRes;
import com.example.demo.src.diary.model.PatchDeleteDiaryReq;
import com.example.demo.src.diary.model.PostDiaryReq;
import com.example.demo.src.diary.model.PostDiaryRes;
import com.example.demo.src.planet.PlanetProvider;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.image.model.GetImageList;
import com.fasterxml.jackson.databind.ser.Serializers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexDate;

@Api(tags = "하루기록 API")
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
    @Autowired
    private final PlanetProvider planetProvider;

    public DiaryController(DiaryProvider diaryProvider, DiaryService diaryService, JwtService jwtService,PlanetProvider planetProvider) {
        this.diaryProvider = diaryProvider;
        this.diaryService = diaryService;
        this.jwtService = jwtService;
        this.planetProvider = planetProvider;
    }

    @ApiOperation(value = "하루기록 생성 api", notes = "유저아이디, 감정 한줄평, 하루평가, 하루기록 내용, 이미지를 받아옵니다.")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요."),
                    @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다."),
                    @ApiResponse(responseCode = "2003", description = "권한이 없는 유저의 접근입니다.")
            }
    )
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "journey_id", value = "journey")
            }

    )
    @ResponseBody
    @PostMapping("")
    @Transactional
    public BaseResponse<PostDiaryRes> createDiary(PostDiaryReq postDiaryReq) throws BaseException {
        try {
            //jwt에서 idx 추출.
            int user_id = planetProvider.getUser_id(postDiaryReq.getJourney_id());
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (user_id != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostDiaryRes postDiaryRes = diaryService.createDiary(postDiaryReq);
            return new BaseResponse<>(postDiaryRes);


        } catch (BaseException exception) {
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ApiOperation(value = "전체 하루기록 조회 api", notes = "날짜 필터링 전 모든 하루기록을 보여줍니다.")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요."),
                    @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다."),
                    @ApiResponse(responseCode = "2003", description = "권한이 없는 유저의 접근입니다.")
            }
    )
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "user_id", value = "유저아이디")
            }

    )


    @ResponseBody
    @GetMapping("/{user_id}")
    public BaseResponse<List<GetDiaryRes>> getAllDiary(@PathVariable("user_id") int user_id) throws BaseException {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (user_id != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetDiaryRes> getDiaryRes = diaryProvider.getAllDiary(user_id);
            return new BaseResponse<>(getDiaryRes);


        } catch (BaseException exception) {
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }
    }




    /*  이미지 url리스트 형식으로 잘 나오는지 테스트해본 컨트롤러
        @ResponseBody
        @GetMapping("/{user_id}")
        public List<String> getAllDiary(@PathVariable("user_id") int user_id) {
           return diaryProvider.getAllImages(user_id);
        }

     */


    @ApiOperation(value = "하루기록 삭제 api", notes = "여정아이디, 다이어리아이디를 받아와서 하루기록을 삭제합니다.")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요."),
                    @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다."),
                    @ApiResponse(responseCode = "2003", description = "권한이 없는 유저의 접근입니다."),
                    @ApiResponse(responseCode = "4501", description = "다이어리 삭제에 실패했습니다")
            }
    )
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "journey_id", value = "여정아이디")
            }

    )
    @Transactional
    @ResponseBody
    @PatchMapping("/{journey_id}")
    public BaseResponse<String> deleteDiary(@PathVariable("journey_id") int journey_id, @RequestBody
        PatchDeleteDiaryReq patchDeleteDiaryReq) throws BaseException {
        try {
            int diary_id = patchDeleteDiaryReq.getDiary_id();
            //jwt에서 idx 추출.
            int user_id = planetProvider.getUser_id(journey_id);
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

    @ApiOperation(value = "날짜별 하루기록 조회 api", notes = "유저아이디,yymmdd형식의 시작일과 마지막날 날짜를 받아 하루기록을 보여줍니다.")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요."),
                    @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다."),
                    @ApiResponse(responseCode = "2003", description = "권한이 없는 유저의 접근입니다."),
                    @ApiResponse(responseCode = "2501", description = "시작날짜를 입력해주세요"),
                    @ApiResponse(responseCode = "2502", description = "마지막날짜를 입력해주세요"),
                    @ApiResponse(responseCode = "2503", description = "날짜에 숫자를 입력해주세요")
            }
    )
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "user_id", value = "유저아이디")
            }

    )

    @Transactional
    @ResponseBody
    @GetMapping("/date/{user_id}")
    public BaseResponse<List<GetDiaryRes>> getDiary(@PathVariable("user_id") int user_id, GetDiaryReq getDiaryReq) throws BaseException {
        if (getDiaryReq.getStart_date() == null) {
            return new BaseResponse<>(START_DATE_ERROR);
        } else if (getDiaryReq.getEnd_date() == null) {
            return new BaseResponse<>(END_DATE_ERROR);
        } else if (!isRegexDate(getDiaryReq.getStart_date()) || !isRegexDate(getDiaryReq.getEnd_date())) {
            return new BaseResponse<>(DATE_NUM_ERROR);
        }
        try {//jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (user_id != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetDiaryRes> getDiaryRes = diaryProvider.getDiary(user_id, getDiaryReq);
            return new BaseResponse<>(getDiaryRes);

        } catch (BaseException exception) {
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }
    }


    @ApiOperation(value = "5주전~저번주까지 하루기록 평가 평균 api", notes = "유저아이디를 받아와서 첫번째 인덱스부터 5주전,4주전 ~ 저번주까지입니다.")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요."),
                    @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다."),
                    @ApiResponse(responseCode = "2003", description = "권한이 없는 유저의 접근입니다.")
            }
    )
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "user_id", value = "유저아이디")
            }

    )
    @ResponseBody
    @GetMapping("/eval/{user_id}")
    public BaseResponse<double[]> getEval(@PathVariable("user_id") int user_id) throws BaseException {
        try {//jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (user_id != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            double[] eval = new double[5];
            eval = diaryProvider.getEval(user_id);
            return new BaseResponse<>(eval);

        } catch (BaseException exception) {
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }

    }
    @ApiOperation(value = "홈화면 어제기록 api", notes = "유저아이디를 받아와서 어제의 기록을 보여줍니다..")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요."),
                    @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다."),
                    @ApiResponse(responseCode = "2003", description = "권한이 없는 유저의 접근입니다.")
            }
    )
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "user_id", value = "유저아이디")
            }

    )
    @ResponseBody
    @GetMapping("/yesterday/{user_id}")
    public BaseResponse<GetImageList> yesterdayDiary(@PathVariable("user_id") int user_id) throws BaseException{
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (user_id != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            return new BaseResponse<>(diaryProvider.yesterdayDiary(user_id));
        } catch(BaseException exception){
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }
    }
}



package com.example.demo.src.journey;

import com.example.demo.src.journey.model.PostJourneyReq;
import com.example.demo.src.journey.model.PostJourneyRes;
import com.example.demo.src.planet.PlanetProvider;
import com.example.demo.src.planet.PlanetService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/journey")
public class JourneyController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private final JourneyProvider journeyProvider;
    @Autowired
    private final JourneyService journeyService;
    @Autowired
    private final JwtService jwtService;

    public JourneyController (JourneyProvider journeyProvider ,JourneyService journeyService ,JwtService jwtService )
    {
        this.journeyProvider = journeyProvider;
        this.journeyService = journeyService;
        this.jwtService = jwtService;
    }



    /**
     * 여정만들기 -> 기간 , 성격/모습/능력(키워드), 행성목록 , 각 행성별 세부계획
     *  기간 -> int
     *  성격/모습/능력(키워드) 숫자제한이없으므로 json 배열로.
     *  행성목록 + 각 세부계획은 json 배열로
     *  헤더로는 jwt를 받는다. pathvriable로는 user_id를 받는다.
     * */

    @ApiOperation(value = "여정만들기 api",notes = "기간,키워드,행성이름,행성세부계획까지 모든 정보를 받아와서 여정을 만듭니다.")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "4001", description = "서버와의 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요."),
                    @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다."),
                    @ApiResponse(responseCode = "2003", description = "권한이 없는 유저의 접근입니다.")
            }
    )
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "user_id",value = "유저아이디")
            }

    )
    @Transactional
    @ResponseBody
    @PostMapping("/{user_id}")
    public BaseResponse<PostJourneyRes> createJourney (@PathVariable("user_id")int user_id , @RequestBody PostJourneyReq postJourneyReq)
    {

        postJourneyReq.setUser_id(user_id); // 받아온 유저아이디 추가해줌
        //validation 처리 일단 스킵.
        try{

            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(user_id != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostJourneyRes postJourneyRes = journeyService.createJourney(postJourneyReq);
            return new BaseResponse<>(postJourneyRes);


        }catch (BaseException exception)
        {
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }

    }

//    @ResponseBody
//    @GetMapping("/jwt")
//    public String temp ()
//    {
//        //임시! ) 회원가입,로그인이 없어서 1번 jwt 발급 임시로
//        String jwt = jwtService.createJwt(1);
//        System.out.println("jwt = " + jwt);
//        return jwt;
//
//    }








}

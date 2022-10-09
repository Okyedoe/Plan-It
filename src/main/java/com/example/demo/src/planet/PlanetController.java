package com.example.demo.src.planet;

import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.planet.model.GetPlanetsRes;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

@Api(tags = "행성관련 컨트롤러")
@RestController
@RequestMapping("/planets")
public class PlanetController {



    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private final PlanetProvider planetProvider;
    @Autowired
    private final PlanetService planetService;
    @Autowired
    private final JwtService jwtService;

    public PlanetController (PlanetProvider planetProvider ,PlanetService planetService ,JwtService jwtService )
    {
        this.planetProvider = planetProvider;
        this.planetService = planetService;
        this.jwtService = jwtService;
    }


    /**
     * 행성조회
     * 헤더로 jwt 받고 ,journey에서 유저아이디와 비교함.
     * */
    @Operation(summary = "해당여정의 행성들+정보를 가져오는 api ", description = "헤더로 jwt를 받고 ,path로 여정아이디를 받습니다." +
            "jwt에서 user_id를 뽑아와서 해당 여정의 주인인지 체크합니다." +
            "결과값으로 해당여정의 행성들과 각각의 기본적인 정보를 제공합니다.")
    @ApiResponses(
            {
                    @ApiResponse(code = 200, message = "코드200은 사용되지않습니다!"),
                    @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
                    @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(code = 4001, message = "서버와의 연결에 실패하였습니다."),
                    @ApiResponse(code = 2001, message = "JWT를 입력해주세요."),
                    @ApiResponse(code = 2002, message = "유효하지 않은 JWT입니다."),
                    @ApiResponse(code = 2003, message = "권한이 없는 유저의 접근입니다.")
            }
    )
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "journey_id", value = "여정 아이디를 입력하세요")
            }
    )
    @ResponseBody
    @GetMapping("/{journey_id}")
    public BaseResponse<List<GetPlanetsRes>> getPlanets (@PathVariable("journey_id")int journey_id)
    {
        try{
            //journey_id로 해당 여정의 user_id 받아오는 부분
            int user_id  = planetProvider.getUser_id(journey_id);

            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(user_id != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetPlanetsRes> getPlanetsRes = planetProvider.getPlanets(journey_id);

            return new BaseResponse<>(getPlanetsRes);



        }catch (BaseException exception)
        {
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }

    }




}

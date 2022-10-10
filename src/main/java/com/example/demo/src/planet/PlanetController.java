package com.example.demo.src.planet;

import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.planet.model.GetDetailedInfoRes;
import com.example.demo.src.planet.model.GetPlanetsRes;
//import io.swagger.annotations.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Api(tags = "여정의 행성목록조회 api ")
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
    @ApiOperation(value = "해당 여정의 행성들 목록과 각 행성의 정보를 가져오는 api  ", notes = "헤더로 해당 유저의 jwt를 받고 ,path로 여정아이디를 받습니다." +
            "  결과값으로 해당 여정의 행성들과 각각의 기본적인 정보를 제공합니다.")
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
                @ApiImplicitParam(name = "journey_id",value = "여정아이디")
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

    /**
     * 행성 세부내용 조회
     *
     * */
    @ApiOperation(value = "행성 세부내용을 조회하는 api입니다.", notes = "행성목록에서 행성을 클릭했을때 나오는 정보들을 반환합니다.")
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
                    @ApiImplicitParam(name = "planet_id",value = "행성아이디")
            }
    )
    @ResponseBody
    @GetMapping("/detail/{planet_id}")
    public BaseResponse<GetDetailedInfoRes> getDetailedInfo (@PathVariable("planet_id")int planet_id)
    {//validation 추가해야함.
        try{

            //jwt에서 idx 추출겸 , jwt검사
            int userIdxByJwt = jwtService.getUserIdx();

            GetDetailedInfoRes getDetailedInfoRes = planetProvider.getDetailedInfo(planet_id);
            return new BaseResponse<>(getDetailedInfoRes);

        }catch (BaseException exception)
        {
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }

    }





}

package com.example.demo.src.planet;

import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.plan.PlanDao;
import com.example.demo.src.plan.PlanProvider;
import com.example.demo.src.planet.model.*;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

@Api(tags = "행성관련 api ")
@RestController
@RequestMapping("/planets")
public class PlanetController {

    //주석주석주석주석

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private final PlanetProvider planetProvider;
    @Autowired
    private final PlanetService planetService;
    @Autowired
    private final JwtService jwtService;
    /*
    @Autowired
    private final AwsS3Service awsS3Service;

     */
    @Autowired
    private final PlanProvider planProvider;

    public PlanetController (PlanetProvider planetProvider ,PlanetService planetService ,JwtService jwtService,PlanProvider planProvider )
    {
        this.planetProvider = planetProvider;
        this.planetService = planetService;
        this.jwtService = jwtService;
        this.planProvider = planProvider;
    }


    /**
     * 행성조회
     * 헤더로 jwt 받고 ,journey에서 유저아이디와 비교함.
     * */
    @ApiOperation(value = "(행성조회 api) 해당 여정의 행성들 목록과 각 행성의 정보를 가져오는 api  ", notes = "헤더로 해당 유저의 jwt를 받고 ,path로 여정아이디를 받습니다." +
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
        //status가 1인것만 가져온다. -> 삭제된 여정관련해서는 아직.
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
            //삭제된행성을 가져오는지 체크하는 vaildation
            if(planetProvider.checkPlanet(planet_id) == 0)
            {
                return new BaseResponse<>(DELETED_PLANET);
            }

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

    /**
     * 새 행성 추가.
     * */
    @ApiOperation(value = "행성 추가 api", notes = "새로운 행성을 생성할때 사용하는 api입니다.")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "4001", description = "서버와의 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요."),
                    @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다."),
                    @ApiResponse(responseCode = "2003", description = "권한이 없는 유저의 접근입니다."),
                    @ApiResponse(responseCode = "2025", description = "행성이름이 비어있습니다.."),
                    @ApiResponse(responseCode = "2026", description = "중복되는 행성이름입니다."),
                    @ApiResponse(responseCode = "2027", description = "세부계획목록이 비어있습니다."),
                    @ApiResponse(responseCode = "2028", description = "중복된 계획이 존재합니다."),
                    @ApiResponse(responseCode = "2018", description = "jwt에서 추출한 유저아이디와 여정아이디에서 추출한 유저아이디가 다릅니다.")
            }
    )
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "journey_id",value = "여정아이디")
            }
    )
    @Transactional
    @ResponseBody
    @PostMapping("/new/{journey_id}")
    public BaseResponse<PostNewPlanetRes> createNewPlanet (@PathVariable("journey_id")int journey_id,@RequestBody PostNewPlanetReq postNewPlanetReq)
    {

        try{
            //validation 처리 (안한것 -> 여정이 끝난거라면? , )

//            //여정이 끝난거라면 (status가 0이라면) -> 여정이 끝난거에 대한 처리는 나중에 바꿔야함 status는 삭제전용.
//            if(planetProvider.checkJourney(journey_id) == 0)
//            {
//                return new BaseResponse<>(END_JOURNEY);
//            }

            //행성이름 빈값, 행성이름 중복
            if(postNewPlanetReq.getPlanet_name() == null || postNewPlanetReq.getPlanet_name().equals(""))
            {
                return new BaseResponse<>(EMPTY_PLANET_NAME);
            }
            if(planetProvider.checkPlanetExist(postNewPlanetReq.getPlanet_name()) >= 1)
            {
                return new BaseResponse<>(DUPLICATE_PLANET_NAME);
            }
            //세부계획 빈값
            if(postNewPlanetReq.getDetailed_plans().size() == 0 || postNewPlanetReq.getDetailed_plans() == null)
            {
                return new BaseResponse<>(EMPTY_DETAILED_PLANS);
            }
            //세부계획안에서 중복값
            int List_length = postNewPlanetReq.getDetailed_plans().size();
            HashSet<String> set = new HashSet<>(postNewPlanetReq.getDetailed_plans());
            if(List_length != set.size())
            {
                //사이즈가 다르면 중복값이 존재한다는뜻
                return new BaseResponse<>(DUPLICATE_PLAN);
            }


            //journey_id 를 이용해서 user_id 를 가져오고 , 그 유저아이디랑 jwt에서 추출한 유저아이디랑 같은지 체크
            int user_id = planetProvider.getUser_id(journey_id);
            //jwt에서 idx 추출겸 , jwt검사
            int userIdxByJwt = jwtService.getUserIdx();

            if(user_id != userIdxByJwt) // jwt로 가져온 유저아이디와 여정아이디로 추출한 유저아이디가 다름.
            { //jwt로 받아온 유저아이디와 해당 여정의 유저아이디가 다르다. 둘중하나가 잘못됬음.
                return new BaseResponse<>(JOURNEY_JWT_CHECK_ERROR);
            }



            PostNewPlanetRes postNewPlanetRes = planetService.createNewPlanet(postNewPlanetReq,journey_id);
            return new BaseResponse<>(postNewPlanetRes);

        }catch (BaseException exception)
        {
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }



    }

    /**
     * 행성삭제 (status를 0으로)
     * */
    @ApiOperation(value = "행성 삭제 api", notes = "행성 삭제시 사용하는 api입니다. status를 0으로 바꿔서 삭제처리를 하고있습니다.")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "4001", description = "서버와의 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요."),
                    @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다."),
                    @ApiResponse(responseCode = "2003", description = "권한이 없는 유저의 접근입니다."),
                    @ApiResponse(responseCode = "2019", description = "jwt에서 추출한 유저아이디와 행성아이디에서 추출한 유저아이디가 다릅니다."),
                    @ApiResponse(responseCode = "2023", description = "삭제된 행성입니다.")

            }
    )
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "journey_id",value = "여정아이디")
            }
    )
    @Transactional
    @PatchMapping("/delete/{planet_id}")
    public BaseResponse<String> deletePlanet (@PathVariable("planet_id")int planet_id)
    {

        //이미 삭제한 행성에 관한 부분도 필요


        try{
            //jwt에서 idx 추출겸 , jwt검사
            int userIdxByJwt = jwtService.getUserIdx();
            int user_id_planet = planProvider.getUser_id_from_planet_id(planet_id);

            if(planetProvider.checkPlanet(planet_id)==0){ //삭제된 행성인지
                return new BaseResponse<>(DELETED_PLANET);
            }

            if(user_id_planet != userIdxByJwt) //들어온 jwt의 유저가 행성의 주인인지
            {
                return new BaseResponse<>(PLANET_JWT_CHECK_ERROR);
            }


            String result = planetService.deletePlanet(planet_id);
            return new BaseResponse<>(result);


        }catch (BaseException exception)
        {
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /*

    @ApiOperation(value = "업로드 실험용 api", notes = "개발 테스트용입니다.")
    @Transactional
    @ResponseBody
    @PostMapping("/upload")
    public BaseResponse<PostUploadRes> uploadFiles (@RequestPart(value = "files")List<MultipartFile> multipartFiles)
    {
        try{
            List<String> result_urls = awsS3Service.uploadFileV1(multipartFiles);
            PostUploadRes postUploadRes = new PostUploadRes(result_urls);
            return new BaseResponse<>(postUploadRes);
        }catch (BaseException e)
        {
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }

    }

     */











}

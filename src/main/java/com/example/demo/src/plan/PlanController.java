package com.example.demo.src.plan;

import com.example.demo.src.journey.JourneyProvider;
import com.example.demo.src.journey.JourneyService;
import com.example.demo.src.journey.model.PostJourneyReq;
import com.example.demo.src.journey.model.PostJourneyRes;
import com.example.demo.src.plan.model.GetTodayPlanRes;
import com.example.demo.src.plan.model.PostPlanReq;
import com.example.demo.src.plan.model.PostPlanRes;
import com.example.demo.src.planet.PlanetProvider;
import com.example.demo.src.planet.PlanetService;
import io.swagger.annotations.Api;
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

import java.time.LocalDate;
import java.util.List;
import java.util.StringTokenizer;

import static com.example.demo.config.BaseResponseStatus.*;

@Api(tags = "행성의 세부계획 관련 api들")
@RestController
@RequestMapping("/plans")

public class PlanController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private final PlanProvider planProvider;
    @Autowired
    private final PlanService planService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final PlanetProvider planetProvider;

    @Autowired
    private final JourneyProvider journeyProvider;

    public PlanController (PlanProvider planProvider ,PlanService planService ,JwtService jwtService,PlanetProvider planetProvider,JourneyProvider journeyProvider )
    {
        this.planProvider = planProvider;
        this.planService = planService;
        this.jwtService = jwtService;
        this.planetProvider = planetProvider;
        this.journeyProvider =journeyProvider;
    }


    /**
     * 행성 세부계획 추가
     * */
    @ApiOperation(value = "행성 세부계획 추가 api  ", notes = "이미 생성된 행성에 추가적으로 세부계획을 추가할때 사용합니다.")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "4001", description = "서버와의 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요."),
                    @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다."),
                    @ApiResponse(responseCode = "2003", description = "권한이 없는 유저의 접근입니다."),
                    @ApiResponse(responseCode = "2029", description = "계획내용이 비어있습니다.."),
                    @ApiResponse(responseCode = "2030", description = "타입이 비어있습니다."),
                    @ApiResponse(responseCode = "2023", description = "삭제된 행성입니다."),
                    @ApiResponse(responseCode = "2039", description = "잘못된 타입값입니다.")
            }
    )
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "planet_id",value = "행성아이디")
            }

    )
    @Transactional
    @ResponseBody
    @PostMapping("/{planet_id}")
    public BaseResponse<PostPlanRes> createPlan (@PathVariable("planet_id")int planet_id, @RequestBody PostPlanReq postPlanReq)
    {
        //세부계획내용 중복관련경우는 넘어감 -> 일정관련 프로그램에서 내용중복은 그냥 생성해주는듯함.
        try{
            //계획 내용이 빈값인지
            if(postPlanReq.getPlan_content() == null || postPlanReq.getPlan_content().equals(""))
            {
                return new BaseResponse<>(EMPTY_PLAN_CONTENT);
            }
            //타입이 빈값인지
            if(postPlanReq.getType() == null || postPlanReq.getType().equals(""))
            {
                return new BaseResponse<>(EMPTY_TYPE);
            }
            //타입이 잘들어왔는지 체크
            String temp = postPlanReq.getType();
            boolean check =true;
            if(temp.equals("마음가짐") || temp.equals("1회성")|| temp.equals("매일루틴") )
            {
                check = true;
            }
            else{
                StringTokenizer st = new StringTokenizer(temp,",");
                loop:
                while(st.hasMoreTokens())
                {
                    String day = st.nextToken();
                    switch (day) {
                        case "월":
                            break;
                        case "화":
                            break;
                        case "수":
                            break;
                        case "목":
                            break;
                        case "금":
                            break;
                        case "토":
                            break;
                        case "일":
                            break;
                        default :
                            check = false;
                            break loop;
                    }
                }
            }
            if(!check)
            {
                return new BaseResponse<>(WRONG_TYPE);
            }



            //삭제된 행성인지 체크
            if(planetProvider.checkPlanet(planet_id) == 0)
            {
                return new BaseResponse<>(DELETED_PLANET);
            }


            //입력받은 jwt로 추출한 유저아이디를 이용하여 해당 여정의 주인이 맞는지 체크.
            int user_id_jwt = jwtService.getUserIdx();
            int user_id_planet = planProvider.getUser_id_from_planet_id(planet_id);

            if(user_id_planet != user_id_jwt)
            {//jwt로 받은 유저아이디와 행성아이디로 받은 유저아이디가 다르다면
                return new BaseResponse<>(PLANET_JWT_CHECK_ERROR);
            }

            PostPlanRes postPlanRes = planService.createPlan(postPlanReq,planet_id);
            return new BaseResponse<>(postPlanRes);






        }catch (BaseException exception)
        {
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     *  오늘 세부계획 출력
     */
    @ApiOperation(value = "오늘 세부계획 출력 api  ", notes = "홈화면에 오늘의 성장계획 리스트를 가져오는 api입니다. 마음가짐,1회성,매일루틴,그리고 오늘 요일에 맞는 값을 가져옵니다.")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "4001", description = "서버와의 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요."),
                    @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다."),
                    @ApiResponse(responseCode = "2003", description = "권한이 없는 유저의 접근입니다."),
                    @ApiResponse(responseCode = "2018", description = "jwt에서 추출한 유저아이디와 여정아이디에서 추출한 유저아이디가 다릅니다.")
            }
    )
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "journey_id",value = "여정아이디")
            }

    )
    @ResponseBody
    @GetMapping("/today/{journey_id}")
    public BaseResponse<List<GetTodayPlanRes>> getTodayPlans (@PathVariable("journey_id")int journey_id)
    {
        try{
            //입력받은 jwt로 추출한 유저아이디를 이용하여 해당 행성의 주인인지 체크하는부분.
            int user_id_jwt = jwtService.getUserIdx();
            int user_id_by_journeyId = journeyProvider.getUserIdByJourneyId(journey_id);

            if(user_id_by_journeyId != user_id_jwt)
            {//jwt로 받은 유저아이디와 여정아이디로 뽑아낸 유저아이디가 다르다면
                return new BaseResponse<>(JOURNEY_JWT_CHECK_ERROR);
            }


            List<GetTodayPlanRes> getTodayPlanRes = planProvider.getTodayPlans(journey_id);
            return new BaseResponse<>(getTodayPlanRes);



        }catch (BaseException e)
        {
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }

    }


    /**
     * 세부 계획 완료처리 , 미완료 처리
     * 완료처리인데 누르면 미완료
     * 미완료인데 누르면 완료처리
     * */
    @ApiOperation(value = "세부계획 완료,미완료 처리 api  ", notes = "세부계획 완료,미완료처리 api입니다 완료상태인 세부계획을 누르면 미완료로 미완료상태인 세부계획을 누르면 완료처리해줍니다.")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "4001", description = "서버와의 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요."),
                    @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다."),
                    @ApiResponse(responseCode = "2003", description = "권한이 없는 유저의 접근입니다."),
                    @ApiResponse(responseCode = "2034", description = "입력된jwt의 유저가 해당 계획의 주인이 아닙니다.")
            }
    )
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "detailed_plan_id",value = "세부계획아이디")
            }

    )
    @Transactional
    @ResponseBody
    @PatchMapping("/{detailed_plan_id}")
    public BaseResponse<String> completePlan (@PathVariable("detailed_plan_id")int detailed_plan_id)
    {
        try{
            //입력받은 jwt로 추출한 유저아이디를 이용하여 해당 세부계획 주인이 맞는지 체크.
            int user_id_jwt = jwtService.getUserIdx();

            int user_id_by_detailed_plan = planProvider.getUser_id_from_detailed_plan_id(detailed_plan_id);
            if(user_id_by_detailed_plan != user_id_jwt)
            {
                return new BaseResponse<>(WRONG_JWT);
            }
            //삭제된 세부계획인지 체크


            String result = planService.completePlan(detailed_plan_id);
            return new BaseResponse<>(result);

        }catch (BaseException e)
        {
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }

    }
    /**
     * 세부 계획 삭제처리
     * */
    @ApiOperation(value = "세부계획 삭제 api  ", notes = "삭제는 status를 0으로 처리합니다. ")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "4001", description = "서버와의 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요."),
                    @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다."),
                    @ApiResponse(responseCode = "2003", description = "권한이 없는 유저의 접근입니다."),
                    @ApiResponse(responseCode = "2034", description = "입력된jwt의 유저가 해당 계획의 주인이 아닙니다.")
            }
    )
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "detailed_plan_id",value = "세부계획아이디")
            }

    )
    @Transactional
    @ResponseBody
    @PatchMapping("/delete/{detailed_plan_id}")
    public BaseResponse<String> deletePlan (@PathVariable("detailed_plan_id")int detailed_plan_id)
    {
        try{
            //입력받은 jwt로 추출한 유저아이디를 이용하여 해당 세부계획 주인이 맞는지 체크.
            int user_id_jwt = jwtService.getUserIdx();

            int user_id_by_detailed_plan = planProvider.getUser_id_from_detailed_plan_id(detailed_plan_id);
            if(user_id_by_detailed_plan != user_id_jwt)
            {
                return new BaseResponse<>(WRONG_JWT);
            }

            String result = planService.deletePlan(detailed_plan_id);
            return new BaseResponse<>(result);

        }catch (BaseException e)
        {
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }

    }




//    /**
//     * 세부계획 수정
//     * */
//    @Transactional
//    @ResponseBody
//    @PatchMapping("/revise/{detailed_plan_id}")
//    public BaseResponse<> revisePlan (@PathVariable("detailed_plan_id")int detailed_plan_id, @RequestBody     )


//    //jwt얻을려고
//    @ResponseBody
//    @GetMapping("/jwt/{user_id}")
//    public BaseResponse<String> getJwtEx (@PathVariable("user_id")int user_id)
//    {
//        String jwt = jwtService.createJwt(user_id);
//        return new BaseResponse<>(jwt);
//    }

    //현재시간 출력 테스트.
//    @ResponseBody
//    @GetMapping("/now")
//    public void getJwtEx ()
//    {
//        LocalDate now = LocalDate.now();
//        String day = now.getDayOfWeek().toString();
//        int day_val = now.getDayOfWeek().getValue() ;
//
//        System.out.println(now);
//        System.out.println("day = " + day);
//        System.out.println("day_val = " + day_val);
//    }


}

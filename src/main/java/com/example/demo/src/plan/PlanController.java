package com.example.demo.src.plan;

import com.example.demo.src.journey.JourneyProvider;
import com.example.demo.src.journey.JourneyService;
import com.example.demo.src.journey.model.PatchPlanRes;
import com.example.demo.src.journey.model.PostJourneyReq;
import com.example.demo.src.journey.model.PostJourneyRes;
import com.example.demo.src.plan.model.GetTodayPlanRes;
import com.example.demo.src.plan.model.PatchPlanReviseReq;
import com.example.demo.src.plan.model.PatchPlanReviseRes;
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
     * 추가되면 원래 오늘 할일 갯수의 변동이 있는지 체크한다. 변동이 있다면 그 값만큼 today_totalplan_completedplan에 가서 total_plans값을 수정해준다.
     * today_totalplan_completedplan에 데이터가 없다면 validation.
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
                    @ApiResponse(responseCode = "2039", description = "잘못된 타입값입니다."),
                    @ApiResponse(responseCode = "2018", description = "jwt에서 추출한 유저아이디와 여정아이디에서 추출한 유저아이디가 다릅니다."),
                @ApiResponse(responseCode = "2039", description = "잘못된 타입값입니다."),
                @ApiResponse(responseCode = "2044", description = "매일 자동생성되어야하는 데이터가 생성되지않았거나 , 문제발생"),

            }
    )
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "planet_id",value = "행성아이디")
            }

    )
    @Transactional
    @ResponseBody
    @PostMapping("/{journey_id}/{planet_id}")
    public BaseResponse<PostPlanRes> createPlan (@PathVariable("journey_id")int journey_id,@PathVariable("planet_id")int planet_id, @RequestBody PostPlanReq postPlanReq)
    {

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
            if(temp.equals("마음가짐") || temp.equals("1회성")|| temp.equals("매일루틴") ||temp.equals("비정기적"))
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


            //planet_id 가 -1이라면 -> 행성선택에서 해당없음을 선택한것임,  jwt와 여정아이디를 이용해서 맞는 사용자인지 체크 후 해당유저의 해당없음 행성아이디를 가져온다.
            //행성을 고르지않는건 1회성만 가능하다.( vaildation 처리)
            if (planet_id == -1) {
                if (!temp.equals("1회성")) {
                    return new BaseResponse<>(PLANET_ERROR);
                }

                int user_id_jwt = jwtService.getUserIdx();
                int user_id_by_journey_id = planetProvider.getUser_id(journey_id);

                if (user_id_by_journey_id != user_id_jwt) {
                    return new BaseResponse<>(JOURNEY_JWT_CHECK_ERROR);
                }
                //해당 유저의 해당없음 행성의 아이디를 가져온다.
                planet_id = planetProvider.getNotBelongPlanetId(journey_id);


            }
            else  {
                //입력받은 jwt로 추출한 유저아이디를 이용하여 해당 여정의 주인이 맞는지 체크.
                int user_id_jwt = jwtService.getUserIdx();
                int user_id_planet = planProvider.getUser_id_from_planet_id(planet_id);

                if(user_id_planet != user_id_jwt)
                {//jwt로 받은 유저아이디와 행성아이디로 받은 유저아이디가 다르다면
                    return new BaseResponse<>(PLANET_JWT_CHECK_ERROR);
                }

            }
            //삭제된 행성인지 체크
            if(planetProvider.checkPlanet(planet_id) == 0)
            {
                return new BaseResponse<>(DELETED_PLANET);
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
    @ApiOperation(value = "오늘 세부계획 출력 api  ", notes = "홈화면에 오늘의 성장계획 리스트를 가져오는 api입니다. 마음가짐,1회성,매일루틴,그리고 루틴타입에서 오늘 요일에 맞는 값을 가져옵니다.(비정기적은 안가져옵니다)")
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
     *
     * 완료시 today_totalplan_completedplan에 가서 completed_plans 값을 증가시켜준다. ( 없다는것에 대한 validation 처리)
     * 그리고 today_completed_plans에 데이터를 추가해준다( 어떤 세부계획이 완료되었는지) -> 이미 데이터가 있는지 체크하고 있다면 status를 1로 바꿔줌.
     * 완료를 미완료 처리시 today_completed_plans에 데이터 추가해준걸 status 0으로 수정해준다. 그리고 today_totalplan_completedplan에  completed_plans 값을 1감소 시킨다.
     * */
    @ApiOperation(value = "세부계획 완료,미완료 처리 api  ", notes = "세부계획 완료,미완료처리 api입니다 완료상태인 세부계획을 누르면 미완료로 미완료상태인 세부계획을 누르면 완료처리해줍니다. + 마음가짐타입의 세부계획은 완료를 못하게 vaildation 처리하였습니다.")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "4001", description = "서버와의 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요."),
                    @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다."),
                    @ApiResponse(responseCode = "2003", description = "권한이 없는 유저의 접근입니다."),
                    @ApiResponse(responseCode = "2034", description = "입력된jwt의 유저가 해당 계획의 주인이 아닙니다."),
                @ApiResponse(responseCode = "2042", description = "마음가짐타입은 완료처리할 수 없습니다.")
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
    public BaseResponse<PatchPlanRes> completePlan (@PathVariable("detailed_plan_id")int detailed_plan_id)
    {
        try{
            //입력받은 jwt로 추출한 유저아이디를 이용하여 해당 세부계획 주인이 맞는지 체크.
            int user_id_jwt = jwtService.getUserIdx();

            int user_id_by_detailed_plan = planProvider.getUser_id_from_detailed_plan_id(detailed_plan_id);
            if(user_id_by_detailed_plan != user_id_jwt)
            {
                return new BaseResponse<>(WRONG_JWT);
            }
            //삭제된 세부계획인지 체크 -> dao에서


            PatchPlanRes patchPlanRes = planService.completePlan(detailed_plan_id);
            return new BaseResponse<>(patchPlanRes);

        }catch (BaseException e)
        {
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }

    }
    /**
     * 세부 계획 삭제처리
     * 삭제되면 원래 오늘 할일 갯수의 변동이 있는지 체크한다. 변동이 있다면 그 값만큼 today_totalplan_completedplan에 가서 total_plans값을 수정해준다.
     * 오늘 할일에 포함되는 일인데 , 이미 완료를 했다면 total_plans값과 더불어 completed_plans값도 감소시켜준다.
     * 그리고 세부계획완료된 데이터에서 해당 세부계획의 오늘 완료된 데이터의 status를 0으로 바꾼다.
     * 루틴을 삭제시 루틴과 관련된 요일 값들도 status를 0처리 해준다.
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


    /**
     * 세부계획 수정
     * 둘다 빈값인지 체크
     * 타입값 수정시 , 오늘 완료한 세부계획이라면 수정을 못하게 한다 ( 너무 꼬임)
     * 완료하지않은 세부계획이라면 수정하고 오늘의 총 할일에 수정사항있으면 수정한다.
     */
    @Transactional
    @ResponseBody
    @PatchMapping("/revise/{detailed_plan_id}")
    public BaseResponse<PatchPlanReviseRes> revisePlan(
        @PathVariable("detailed_plan_id") int detailed_plan_id, @RequestBody
        PatchPlanReviseReq patchPlanReviseReq) {

        try{
            //삭제된 세부계획인지 체크
            if (planProvider.checkExistPlan(detailed_plan_id) == 0) {
                return new BaseResponse<>(DELETED_PLAN);
            }

            //입력받은 jwt로 추출한 유저아이디를 이용하여 해당 세부계획 주인이 맞는지 체크.
            int user_id_jwt = jwtService.getUserIdx();

            int user_id_by_detailed_plan = planProvider.getUser_id_from_detailed_plan_id(detailed_plan_id);
            if(user_id_by_detailed_plan != user_id_jwt)
            {
                return new BaseResponse<>(WRONG_JWT);
            }

            //둘다 빈값체크 -> 수정하고자하는것만 값을 넣어주면 되긴함 하지만 둘다 빈값은 에러
            // 세부계획 이름에 대한 중복체크는 하지않는다.
            if (patchPlanReviseReq.getPlan_content() == null
                || patchPlanReviseReq.getPlan_content().length() == 0) {
                if (patchPlanReviseReq.getType() == null
                    || patchPlanReviseReq.getType().length() == 0) {
                    return new BaseResponse<>(EMPTY_CONTENT_AND_TYPE);
                }
            }
            PatchPlanReviseRes patchPlanReviseRes = new PatchPlanReviseRes();
            //이름이 값이 있다면 이름은 바꿔주기만 하면된다.
            if (patchPlanReviseReq.getPlan_content() != null) {
                if (patchPlanReviseReq.getPlan_content().length() != 0) {
                    //이름 값이 있다면 이름만 수정해준다.
                    patchPlanReviseRes = planService.reviseContent(patchPlanReviseReq,
                        detailed_plan_id);
                }
            }
            if (patchPlanReviseReq.getType() != null) {
                if(patchPlanReviseReq.getType().length() != 0)
                {
                    //타입값이 들어왔다
                    String type = patchPlanReviseReq.getType();
                    //타입검사
                    if(type.equals("마음가짐") || type.equals("비정기적") || type.equals("1회성") || type.equals("매일루틴"))
                    {

                    }
                    else{
                        boolean check = true;
                        StringTokenizer st = new StringTokenizer(type,",");
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
                        if (!check) {
                            return new BaseResponse<>(WRONG_TYPE);
                        }

                    }
                    //다 통과했다면 맞는 타입값이다. 원래거랑 같다면 그냥 값을 가져와서 리턴 ( 루틴 제외)
                    PatchPlanReviseRes currentpatchPlanReviseRes = planProvider.getInfo(detailed_plan_id);
                    String currentType = currentpatchPlanReviseRes.getType();
                    //루틴은 입력값이 요일값이니까 기존에 저장된값의 타입을 가져온 루틴과 다르다.
                    //루틴말고 나머지 타입값은 같다면 그냥 원래걸로 리턴해준다.
                    if (currentType.equals(type)) {
                        patchPlanReviseRes = currentpatchPlanReviseRes;
                    }
                    else{
                        //다르다면 service로 타입 전달
                        patchPlanReviseRes = planService.reviseType(patchPlanReviseReq, detailed_plan_id);

                    }

                    return new BaseResponse<>(patchPlanReviseRes);

                }




            }
            patchPlanReviseRes = planProvider.getPlanetIdByDetailedPlanInfo(detailed_plan_id);
            return new BaseResponse<>(patchPlanReviseRes);



        }catch (BaseException e)
        {
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }

    }







//    //jwt얻을려고
//    @ResponseBody
//    @GetMapping("/jwt/{user_id}")
//    public BaseResponse<String> getJwtEx (@PathVariable("user_id")int user_id)
//    {
//        String jwt = jwtService.createJwt(user_id);
//        return new BaseResponse<>(jwt);
//    }




}

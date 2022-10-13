package com.example.demo.src.plan;

import com.example.demo.src.journey.JourneyProvider;
import com.example.demo.src.journey.JourneyService;
import com.example.demo.src.journey.model.PostJourneyReq;
import com.example.demo.src.journey.model.PostJourneyRes;
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
import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

@Api(tags = "행성의 세부게획 관련 api들")
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

    public PlanController (PlanProvider planProvider ,PlanService planService ,JwtService jwtService,PlanetProvider planetProvider )
    {
        this.planProvider = planProvider;
        this.planService = planService;
        this.jwtService = jwtService;
        this.planetProvider = planetProvider;
    }


    /**
     * 행성 세부계획 추가
     * */
    @Transactional
    @ResponseBody
    @PostMapping("/{planet_id}")
    public BaseResponse<PostPlanRes> createPlan (@PathVariable("planet_id")int planet_id, @RequestBody PostPlanReq postPlanReq)
    {


        try{
            //계획 내용이 빈값인지
            if(postPlanReq.getPlan_content() == null)
            {
                return new BaseResponse<>(EMPTY_PLAN_CONTENT);
            }
            //타입이 빈값인지
            if(postPlanReq.getType() == null)
            {
                return new BaseResponse<>(EMPTY_TYPE);
            }
            //타입이 종류랑 다른지? -> 타입이 정해지고.

            //삭제된 행성인지 체크
            if(planetProvider.checkPlanet(planet_id) == 0)
            {
                return new BaseResponse<>(DELETED_PLANET);
            }
            //행성세부계획이 같은게 있는지 체크 -> 일단 스킵 어떤식으로 추가될지 모르니.



            //입력받은 jwt로 추출한 유저아이디를 이용하여 해당 행성의 주인인지 체크하는부분.
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


//    /**
//     * 세부계획 수정
//     * */
//    @Transactional
//    @ResponseBody
//    @PatchMapping("/revise/{detailed_plan_id}")
//    public BaseResponse<> revisePlan (@PathVariable("detailed_plan_id")int detailed_plan_id, @RequestBody     )



}
